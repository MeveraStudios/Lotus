package studio.mevera.lotus.paper.internal.pagination;

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
import studio.mevera.lotus.api.pagination.Pagination;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.internal.pagination.DefaultPaginationSession;
import studio.mevera.lotus.paper.api.menu.PaperInteractiveMenu;
import studio.mevera.lotus.paper.api.pagination.PaperPageLayout;

import java.util.Iterator;
import java.util.List;

/**
 * Paper-specific pagination session. Extends {@link DefaultPaginationSession} and overrides
 * {@link #buildPageMenu(int)} to synthesise a {@link PaperPageMenu} — a {@link PaperInteractiveMenu}
 * whose {@code title()} returns an Adventure {@link Component} directly, preserving full fidelity.
 */
public class PaperPaginationSession<T> extends DefaultPaginationSession<T> {

    public PaperPaginationSession(
        @NotNull Pagination<T> definition,
        @NotNull Lotus lotus,
        @NotNull Player viewer
    ) {
        super(definition, lotus, viewer);
    }

    @Override
    protected @NotNull InteractiveMenu<?> buildPageMenu(int pageIndex) {
        return new PaperPageMenu(this, pageIndex);
    }

    /**
     * Synthesised page menu backed by a {@link PaperPageLayout}. Returns Adventure
     * {@link Component} titles with no serialization round-trip.
     */
    private record PaperPageMenu(
        @NotNull PaperPaginationSession<?> session,
        int pageIndex
    ) implements PaperInteractiveMenu {

        @Override
        public @NotNull Component title(@NotNull MenuView<?> view) {
            // Safe cast: PaperPaginationSession is only constructed with PaperPageLayout.
            return paperLayout().title(contextFor(view));
        }

        @Override
        public @NotNull Capacity capacity(@NotNull MenuView<?> view) {
            return paperLayout().capacity();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public @NotNull Content content(@NotNull MenuView<?> view) {
            PaperPageLayout layout = paperLayout();
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
            return "PaperPageMenu#" + pageIndex;
        }

        private @NotNull PaperPageLayout paperLayout() {
            return (PaperPageLayout) session.definition.layout();
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
