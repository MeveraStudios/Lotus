package studio.mevera.lotus.paper.internal.pagination;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.ClickAction;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.paper.api.menu.PaperInteractiveMenu;
import studio.mevera.lotus.paper.api.pagination.PaperPageContext;
import studio.mevera.lotus.paper.api.pagination.PaperPageLayout;

import java.util.Iterator;
import java.util.List;

/**
     * Synthesised page menu backed by a {@link PaperPageLayout}. Returns Adventure
     * {@link Component} titles with no serialization round-trip.
     */
public record PaperPageMenu<T>(
    @NotNull PaperPaginationSession<T> session,
    int pageIndex
) implements PaperInteractiveMenu {

    @Override
    public @NotNull Component title(@NotNull MenuView<Component, ?> view) {
        return paperLayout().title(contextFor(view));
    }

    @Override
    public @NotNull Capacity capacity(@NotNull MenuView<Component, ?> view) {
        return paperLayout().capacity();
    }

    @Override
    public @NotNull Content content(@NotNull MenuView<Component, ?> view) {
        PaperPageLayout<T> layout = paperLayout();
        PaperPageContext<T> ctx = contextFor(view);
        Capacity capacity = layout.capacity();

        Content content = Content.empty(capacity);
        layout.decorations(ctx).forEach(content::set);

        List<T> items = session.sliceFor(pageIndex);
        Iterator<Slot> slotIterator = layout.fillMask().stream().iterator();
        var renderer = session.definition().renderer();
        int rendered = 0;
        int max = session.definition().trimOverflow() ? layout.fillMask().size() : Integer.MAX_VALUE;
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
        return "PaperPageMenu#" + pageIndex;
    }

    private @NotNull PaperPageLayout<T> paperLayout() {
        return (PaperPageLayout<T>) session.definition().layout();
    }

    private @NotNull PaperPageContext<T> contextFor(@NotNull MenuView<Component, ?> view) {
        return new PaperPageContext<>(pageIndex, session.totalPages(), view.viewer(), session);
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