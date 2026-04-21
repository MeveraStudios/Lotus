package studio.mevera.lotus.spigot.api.pagination;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

import java.util.Objects;
import java.util.function.Function;

/**
 * Spigot-specific builder that exposes {@link SpigotPageContext} with a typed
 * {@link studio.mevera.lotus.spigot.internal.SpigotPaginationSession}.
 */
public final class SpigotPageLayoutBuilder<T> {

    private final Capacity capacity;
    private SlotMask fillMask;
    private Slot previousSlot;
    private Slot nextSlot;
    private Function<SpigotPageContext<T>, String> title = ctx -> "Page " + (ctx.pageIndex() + 1);
    private Function<SpigotPageContext<T>, Button> previousButton;
    private Function<SpigotPageContext<T>, Button> nextButton;
    private Function<SpigotPageContext<T>, Content> decorations;

    public SpigotPageLayoutBuilder(@NotNull Capacity capacity) {
        this.capacity = Objects.requireNonNull(capacity);
        this.previousSlot = Slot.at(capacity.rows() - 1, 0, capacity);

        ItemStack previousPageItem = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = previousPageItem.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName(ChatColor.GREEN + "<- Previous Page");
            previousPageItem.setItemMeta(prevMeta);
        }
        this.previousButton = ctx -> Button.of(previousPageItem);

        this.nextSlot = Slot.at(capacity.rows() - 1, capacity.columns() - 1, capacity);

        ItemStack nextPageItem = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPageItem.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName(ChatColor.GREEN + "Next Page ->");
            nextPageItem.setItemMeta(nextMeta);
        }
        this.nextButton = ctx -> Button.of(nextPageItem);

        this.decorations = ctx -> Content.empty(this.capacity);
    }

    public @NotNull SpigotPageLayoutBuilder<T> title(@NotNull Function<SpigotPageContext<T>, String> title) {
        this.title = Objects.requireNonNull(title);
        return this;
    }

    public @NotNull SpigotPageLayoutBuilder<T> fillMask(@NotNull SlotMask mask) {
        this.fillMask = mask;
        return this;
    }

    public @NotNull SpigotPageLayoutBuilder<T> previousButton(
        @NotNull Slot slot,
        @NotNull Function<SpigotPageContext<T>, Button> button
    ) {
        this.previousSlot = slot;
        this.previousButton = button;
        return this;
    }

    public @NotNull SpigotPageLayoutBuilder<T> previousButton(@NotNull Function<SpigotPageContext<T>, Button> previousButton) {
        this.previousButton = previousButton;
        return this;
    }

    public @NotNull SpigotPageLayoutBuilder<T> nextButton(
        @NotNull Slot slot,
        @NotNull Function<SpigotPageContext<T>, Button> button
    ) {
        this.nextSlot = slot;
        this.nextButton = button;
        return this;
    }

    public @NotNull SpigotPageLayoutBuilder<T> nextButton(@NotNull Function<SpigotPageContext<T>, Button> nextButton) {
        this.nextButton = nextButton;
        return this;
    }

    public @NotNull SpigotPageLayoutBuilder<T> decorations(@NotNull Function<SpigotPageContext<T>, Content> decorations) {
        this.decorations = Objects.requireNonNull(decorations);
        return this;
    }

    public @NotNull SpigotPageLayout<T> build() {
        if (previousButton == null) throw new IllegalStateException("previousButton not configured");
        if (nextButton == null) throw new IllegalStateException("nextButton not configured");
        var resolvedMask = fillMask != null
            ? fillMask
            : SlotMask.full(capacity).excluding(previousSlot, nextSlot);
        return new BuiltSpigotPageLayout<>(
            capacity,
            resolvedMask,
            previousSlot,
            nextSlot,
            title,
            previousButton,
            nextButton,
            decorations
        );
    }

    private record BuiltSpigotPageLayout<T>(
        @NotNull Capacity capacity,
        @NotNull SlotMask fillMask,
        @NotNull Slot previousButtonSlot,
        @NotNull Slot nextButtonSlot,
        @NotNull Function<SpigotPageContext<T>, String> titleFn,
        @NotNull Function<SpigotPageContext<T>, Button> previousButtonFn,
        @NotNull Function<SpigotPageContext<T>, Button> nextButtonFn,
        @NotNull Function<SpigotPageContext<T>, Content> decorationsFn
    ) implements SpigotPageLayout<T> {

        @Override public @NotNull String title(@NotNull SpigotPageContext<T> ctx) { return titleFn.apply(ctx); }
        @Override public @NotNull Button previousButton(@NotNull SpigotPageContext<T> ctx) { return previousButtonFn.apply(ctx); }
        @Override public @NotNull Button nextButton(@NotNull SpigotPageContext<T> ctx) { return nextButtonFn.apply(ctx); }
        @Override public @NotNull Content decorations(@NotNull SpigotPageContext<T> ctx) { return decorationsFn.apply(ctx); }
    }
}
