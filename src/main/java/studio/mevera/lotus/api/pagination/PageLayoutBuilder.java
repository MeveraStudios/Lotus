package studio.mevera.lotus.api.pagination;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

import java.util.Objects;
import java.util.function.Function;

/**
 * Builder for {@link PageLayout}. Required: capacity (constructor), title, nav buttons. Optional:
 * fill mask (defaults to full minus nav slots), nav slots (default last row), decorations.
 */
public final class PageLayoutBuilder {

    private final Capacity capacity;
    private SlotMask fillMask;
    private Slot previousSlot;
    private Slot nextSlot;
    private Function<PageContext, Component> title = ctx -> Component.text("Page " + (ctx.pageIndex() + 1));
    private Function<PageContext, Button> previousButton;
    private Function<PageContext, Button> nextButton;
    private Function<PageContext, Content> decorations;

    PageLayoutBuilder(@NotNull Capacity capacity) {
        this.capacity = capacity;
        this.previousSlot = Slot.at(capacity.rows() - 1, 0, capacity);

        ItemStack previousPageItem = ItemStack.of(Material.ARROW);
        previousPageItem.editMeta((meta)-> meta.displayName(Component.text("<- Previous Page", NamedTextColor.GREEN)));
        this.previousButton = (ctx)-> Button.of(previousPageItem);

        this.nextSlot = Slot.at(capacity.rows() - 1, capacity.columns() - 1, capacity);

        ItemStack nextPageItem = ItemStack.of(Material.ARROW);
        nextPageItem.editMeta((meta)-> meta.displayName(Component.text("Next Page ->", NamedTextColor.GREEN)));
        this.nextButton = (ctx)-> Button.of(nextPageItem);

        this.decorations = ctx -> Content.empty(this.capacity);
    }

    public @NotNull PageLayoutBuilder title(@NotNull Function<PageContext, Component> title) {
        this.title = Objects.requireNonNull(title);
        return this;
    }

    public @NotNull PageLayoutBuilder fillMask(@NotNull SlotMask mask) {
        this.fillMask = mask;
        return this;
    }

    public @NotNull PageLayoutBuilder previousButton(@NotNull Slot slot, @NotNull Function<PageContext, Button> button) {
        this.previousSlot = slot;
        this.previousButton = button;
        return this;
    }

    public @NotNull PageLayoutBuilder previousButton(@NotNull Function<PageContext, Button> previousButton) {
        this.previousButton = previousButton;
        return this;
    }

    public @NotNull PageLayoutBuilder nextButton(@NotNull Slot slot, @NotNull Function<PageContext, Button> button) {
        this.nextSlot = slot;
        this.nextButton = button;
        return this;
    }

    public @NotNull PageLayoutBuilder nextButton(@NotNull Function<PageContext, Button> nextButton) {
        this.nextButton = nextButton;
        return this;
    }

    public @NotNull PageLayoutBuilder decorations(@NotNull Function<PageContext, Content> decorations) {
        this.decorations = Objects.requireNonNull(decorations);
        return this;
    }

    public @NotNull PageLayout build() {
        if (previousButton == null) throw new IllegalStateException("previousButton not configured");
        if (nextButton == null) throw new IllegalStateException("nextButton not configured");
        var resolvedMask = fillMask != null
            ? fillMask
            : SlotMask.full(capacity).excluding(previousSlot, nextSlot);
        return new BuiltPageLayout(capacity, resolvedMask, previousSlot, nextSlot,
            title, previousButton, nextButton, decorations);
    }

    private record BuiltPageLayout(
        @NotNull Capacity capacity,
        @NotNull SlotMask fillMask,
        @NotNull Slot previousButtonSlot,
        @NotNull Slot nextButtonSlot,
        @NotNull Function<PageContext, Component> titleFn,
        @NotNull Function<PageContext, Button> previousButtonFn,
        @NotNull Function<PageContext, Button> nextButtonFn,
        @NotNull Function<PageContext, Content> decorationsFn
    ) implements PageLayout {

        @Override public @NotNull Component title(@NotNull PageContext ctx) { return titleFn.apply(ctx); }
        @Override public @NotNull Button previousButton(@NotNull PageContext ctx) { return previousButtonFn.apply(ctx); }
        @Override public @NotNull Button nextButton(@NotNull PageContext ctx) { return nextButtonFn.apply(ctx); }
        @Override public @NotNull Content decorations(@NotNull PageContext ctx) { return decorationsFn.apply(ctx); }
    }
}
