package studio.mevera.lotus.spigot.internal.pagination;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.ClickAction;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.menu.InteractiveMenu;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.spigot.api.menu.SpigotInteractiveMenu;
import studio.mevera.lotus.spigot.api.pagination.SpigotPageContext;

import java.util.Iterator;
import java.util.List;

/**
     * Synthesised {@link InteractiveMenu}{@code <String>} representing one page.
     * Implements {@link InteractiveMenu} purely to wire navigation without exposing
     * handler types to users.
     */
public record SpigotPageMenu<T>(
        @NotNull SpigotPaginationSession<T> session,
        int pageIndex
    ) implements SpigotInteractiveMenu {

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
            return "SpigotPageMenu#" + pageIndex;
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