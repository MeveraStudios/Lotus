package studio.mevera.lotus.paper.api.pagination;

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
 * Standalone builder for {@link PaperPageLayout}. Uses Adventure {@link Component} for titles,
 * {@code ItemStack.of()} and {@code editMeta()} lambda — Paper 1.21+ specific APIs.
 * <p>
 * This builder is independent of {@link studio.mevera.lotus.api.pagination.PageLayoutBuilder}
 * (not extending it) to avoid fluent-inheritance issues.
 */
public final class PaperPageLayoutBuilder<T> {

    private final Capacity capacity;
    private SlotMask fillMask;
    private Slot previousSlot;
    private Slot nextSlot;
    private Function<PaperPageContext<T>, Component> title = ctx ->
        Component.text("Page " + (ctx.pageIndex() + 1));
    private Function<PaperPageContext<T>, Button> previousButton;
    private Function<PaperPageContext<T>, Button> nextButton;
    private Function<PaperPageContext<T>, Content> decorations;

    PaperPageLayoutBuilder(@NotNull Capacity capacity) {
        this.capacity = Objects.requireNonNull(capacity);
        this.previousSlot = Slot.at(capacity.rows() - 1, 0, capacity);

        ItemStack prevItem = ItemStack.of(Material.ARROW);
        prevItem.editMeta(meta -> meta.displayName(
            Component.text("<- Previous Page").color(NamedTextColor.GREEN)));
        this.previousButton = (ctx) -> Button.of(prevItem);

        this.nextSlot = Slot.at(capacity.rows() - 1, capacity.columns() - 1, capacity);

        ItemStack nextItem = ItemStack.of(Material.ARROW);
        nextItem.editMeta(meta -> meta.displayName(
            Component.text("Next Page ->").color(NamedTextColor.GREEN)));
        this.nextButton = (ctx) -> Button.of(nextItem);

        this.decorations = ctx -> Content.empty(this.capacity);
    }

    public @NotNull PaperPageLayoutBuilder<T> title(@NotNull Function<PaperPageContext<T>, Component> title) {
        this.title = Objects.requireNonNull(title);
        return this;
    }

    public @NotNull PaperPageLayoutBuilder<T> fillMask(@NotNull SlotMask mask) {
        this.fillMask = mask;
        return this;
    }

    public @NotNull PaperPageLayoutBuilder<T> previousButton(
        @NotNull Slot slot,
        @NotNull Function<PaperPageContext<T>, Button> button
    ) {
        this.previousSlot = slot;
        this.previousButton = button;
        return this;
    }

    public @NotNull PaperPageLayoutBuilder<T> previousButton(
        @NotNull Function<PaperPageContext<T>, Button> previousButton
    ) {
        this.previousButton = previousButton;
        return this;
    }

    public @NotNull PaperPageLayoutBuilder<T> nextButton(
        @NotNull Slot slot,
        @NotNull Function<PaperPageContext<T>, Button> button
    ) {
        this.nextSlot = slot;
        this.nextButton = button;
        return this;
    }

    public @NotNull PaperPageLayoutBuilder<T> nextButton(
        @NotNull Function<PaperPageContext<T>, Button> nextButton
    ) {
        this.nextButton = nextButton;
        return this;
    }

    public @NotNull PaperPageLayoutBuilder<T> decorations(
        @NotNull Function<PaperPageContext<T>, Content> decorations
    ) {
        this.decorations = Objects.requireNonNull(decorations);
        return this;
    }

    public @NotNull PaperPageLayout<T> build() {
        if (previousButton == null) throw new IllegalStateException("previousButton not configured");
        if (nextButton == null) throw new IllegalStateException("nextButton not configured");
        var resolvedMask = fillMask != null
            ? fillMask
            : SlotMask.full(capacity).excluding(previousSlot, nextSlot);
        return new BuiltPaperPageLayout<>(
            capacity, resolvedMask, previousSlot, nextSlot,
            title, previousButton, nextButton, decorations);
    }

    private record BuiltPaperPageLayout<T>(
        @NotNull Capacity capacity,
        @NotNull SlotMask fillMask,
        @NotNull Slot previousButtonSlot,
        @NotNull Slot nextButtonSlot,
        @NotNull Function<PaperPageContext<T>, Component> titleFn,
        @NotNull Function<PaperPageContext<T>, Button> previousButtonFn,
        @NotNull Function<PaperPageContext<T>, Button> nextButtonFn,
        @NotNull Function<PaperPageContext<T>, Content> decorationsFn
    ) implements PaperPageLayout<T> {

        @Override public @NotNull Component title(@NotNull PaperPageContext<T> ctx) { return titleFn.apply(ctx); }
        @Override public @NotNull Button previousButton(@NotNull PaperPageContext<T> ctx) { return previousButtonFn.apply(ctx); }
        @Override public @NotNull Button nextButton(@NotNull PaperPageContext<T> ctx) { return nextButtonFn.apply(ctx); }
        @Override public @NotNull Content decorations(@NotNull PaperPageContext<T> ctx) { return decorationsFn.apply(ctx); }
    }
}
