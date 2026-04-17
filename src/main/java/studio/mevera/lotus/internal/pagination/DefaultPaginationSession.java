package studio.mevera.lotus.internal.pagination;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.ClickAction;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.menu.InteractiveMenu;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.pagination.PageContext;
import studio.mevera.lotus.api.pagination.PageLayout;
import studio.mevera.lotus.api.pagination.Pagination;
import studio.mevera.lotus.api.pagination.PaginationSession;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Per-player pagination runtime. Captures a snapshot of source items at construction so the page
 * count is stable for the session's lifetime, then synthesises a {@link PageMenu} per navigation.
 */
public final class DefaultPaginationSession<T> implements PaginationSession<T> {

    private final Lotus lotus;
    private final Pagination<T> definition;
    private final Player viewer;
    private final List<T> snapshot;
    private final int totalPages;
    private final int itemsPerPage;

    private int currentIndex;
    private boolean closed;

    public DefaultPaginationSession(@NotNull Lotus lotus, @NotNull Pagination<T> definition, @NotNull Player viewer) {
        this.lotus = Objects.requireNonNull(lotus);
        this.definition = Objects.requireNonNull(definition);
        this.viewer = Objects.requireNonNull(viewer);
        this.snapshot = List.copyOf(definition.source().provide(viewer));
        this.itemsPerPage = definition.layout().fillMask().size();
        this.totalPages = Math.max(1, (int) Math.ceil(snapshot.size() / (double) itemsPerPage));
    }

    @Override public @NotNull Pagination<T> definition() { return definition; }
    @Override public @NotNull Player viewer() { return viewer; }
    @Override public int currentIndex() { return currentIndex; }
    @Override public int totalPages() { return totalPages; }

    @Override
    public void next() {
        if (currentIndex + 1 >= totalPages) return;
        goTo(currentIndex + 1);
    }

    @Override
    public void previous() {
        if (currentIndex == 0) return;
        goTo(currentIndex - 1);
    }

    @Override
    public void goTo(int pageIndex) {
        Objects.checkIndex(pageIndex, totalPages);
        if (closed) throw new IllegalStateException("session closed");
        this.currentIndex = pageIndex;
        var menu = new PageMenu(this, pageIndex);
        lotus.openMenu(viewer, menu);
    }

    @Override
    public void close() {
        if (closed) return;
        this.closed = true;
        viewer.closeInventory();
    }

    List<T> sliceFor(int pageIndex) {
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, snapshot.size());
        if (start >= snapshot.size()) return List.of();
        return snapshot.subList(start, end);
    }

    /**
     * Synthesised {@link studio.mevera.lotus.api.menu.Menu} representing one page. Implements
     * {@link InteractiveMenu} purely to wire navigation without exposing handler types to users.
     */
    private record PageMenu(@NotNull DefaultPaginationSession<?> session, int pageIndex) implements InteractiveMenu {

        @Override
        public @NotNull Component title(@NotNull MenuView<?> view) {
            return session.definition.layout().title(contextFor(view));
        }

        @Override
        public @NotNull Capacity capacity(@NotNull MenuView<?> view) {
            return session.definition.layout().capacity();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public @NotNull Content content(@NotNull MenuView<?> view) {
            PageLayout layout = session.definition.layout();
            PageContext ctx = contextFor(view);
            Capacity capacity = layout.capacity();

            Content content = Content.empty(capacity);
            layout.decorations(ctx).forEach(content::set);

            List<?> items = session.sliceFor(pageIndex);
            Iterator<Slot> slotIterator = layout.fillMask().stream().iterator();
            var renderer = (studio.mevera.lotus.api.pagination.ComponentRenderer) session.definition.renderer();
            int rendered = 0;
            int max = session.definition.trimOverflow() ? layout.fillMask().size() : Integer.MAX_VALUE;
            for (Object item : items) {
                if (rendered++ >= max || !slotIterator.hasNext()) break;
                content.set(slotIterator.next(), renderer.render(item, ctx));
            }

            if (!ctx.isFirst()) {
                Button prev = layout.previousButton(ctx);
                content.set(layout.previousButtonSlot(), wrapNav(prev, (v, e) -> session.previous()));
            }
            if (!ctx.isLast()) {
                Button next = layout.nextButton(ctx);
                content.set(layout.nextButtonSlot(), wrapNav(next, (v, e) -> session.next()));
            }
            return content;
        }

        @Override
        public @NotNull String name() {
            return "PageMenu#" + pageIndex;
        }

        private @NotNull PageContext contextFor(@NotNull MenuView<?> view) {
            return new PageContext(pageIndex, session.totalPages, view.viewer(), session);
        }

        private static @NotNull Button wrapNav(@NotNull Button base, @NotNull ClickAction navigate) {
            ClickAction action = (view, event) -> {
                event.setCancelled(true);
                base.dispatch(view, event);
                navigate.onClick(view, event);
            };
            return Button.clickable(base.item(), action);
        }
    }
}
