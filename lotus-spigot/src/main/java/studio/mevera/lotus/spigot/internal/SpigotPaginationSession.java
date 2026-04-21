package studio.mevera.lotus.spigot.internal;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.ClickAction;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.menu.InteractiveMenu;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.pagination.PaginationSession;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.spigot.api.pagination.Pagination;
import studio.mevera.lotus.spigot.api.pagination.SpigotPageContext;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Per-player pagination runtime. Captures a snapshot of source items at construction so the page
 * count is stable for the session's lifetime, then synthesises a {@link PageMenu} per navigation.
 * <p>
 * Subclass and override {@link #buildPageMenu(int)} to supply a platform-specific menu
 * (e.g. {@code PaperPaginationSession} in lotus-paper uses Adventure Component titles).
 */
public class SpigotPaginationSession<T> implements PaginationSession<String, T, SpigotPageContext<T>> {

    protected final Lotus<String> lotus;
    protected final Pagination<T> definition;
    protected final Player viewer;
    protected final List<T> snapshot;
    protected final int totalPages;
    protected final int itemsPerPage;

    private int currentIndex;
    private boolean closed;

    public SpigotPaginationSession(
        @NotNull Pagination<T> definition,
        @NotNull Lotus<String> lotus,
        @NotNull Player viewer
    ) {
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
        if (currentIndex + 1 >= totalPages) {
            return;
        }
        goTo(currentIndex + 1);
    }

    @Override
    public void previous() {
        if (currentIndex == 0) {
            return;
        }
        goTo(currentIndex - 1);
    }

    @Override
    public void goTo(int pageIndex) {
        Objects.checkIndex(pageIndex, totalPages);
        if (closed) {
            throw new IllegalStateException("session closed");
        }
        this.currentIndex = pageIndex;
        InteractiveMenu<String> menu = buildPageMenu(pageIndex);
        lotus.openMenu(viewer, menu);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        this.closed = true;
        viewer.closeInventory();
    }

    /**
     * Factory method for constructing the page menu for a given index.
     * Override in subclasses to produce platform-specific menus with richer title types.
     */
    protected @NotNull InteractiveMenu<String> buildPageMenu(int pageIndex) {
        return new PageMenu<>(this, pageIndex);
    }

    protected List<T> sliceFor(int pageIndex) {
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, snapshot.size());
        if (start >= snapshot.size()) {
            return List.of();
        }
        return snapshot.subList(start, end);
    }

    /**
     * Synthesised {@link InteractiveMenu}{@code <String>} representing one page.
     * Implements {@link InteractiveMenu} purely to wire navigation without exposing
     * handler types to users.
     */
    protected record PageMenu<T>(
        @NotNull SpigotPaginationSession<T> session,
        int pageIndex
    ) implements InteractiveMenu<String> {

        @Override
        public @NotNull String title(@NotNull MenuView<String, ?> view) {
            return pageLayout().title(contextFor(view));
        }

        @Override
        public @NotNull Capacity capacity(@NotNull MenuView<String, ?> view) {
            return pageLayout().capacity();
        }

        @Override
        public @NotNull Content content(@NotNull MenuView<String, ?> view) {
            var layout = pageLayout();
            SpigotPageContext<T> ctx = contextFor(view);
            Capacity capacity = layout.capacity();

            Content content = Content.empty(capacity);
            layout.decorations(ctx).forEach(content::set);

            List<T> items = session.sliceFor(pageIndex);
            Iterator<Slot> slotIterator = layout.fillMask().stream().iterator();
            var renderer = pageRenderer();
            int rendered = 0;
            int max = session.definition.trimOverflow() ? layout.fillMask().size() : Integer.MAX_VALUE;
            for (T item : items) {
                if (rendered++ >= max || !slotIterator.hasNext()) {
                    break;
                }
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

        private @NotNull SpigotPageContext<T> contextFor(@NotNull MenuView<String, ?> view) {
            return new SpigotPageContext<>(pageIndex, session.totalPages, view.viewer(), session);
        }

        private @NotNull studio.mevera.lotus.api.pagination.PageLayout<String, SpigotPageContext<T>> pageLayout() {
            return session.definition.layout();
        }

        private @NotNull studio.mevera.lotus.api.pagination.ComponentRenderer<T, SpigotPageContext<T>> pageRenderer() {
            return session.definition.renderer();
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
