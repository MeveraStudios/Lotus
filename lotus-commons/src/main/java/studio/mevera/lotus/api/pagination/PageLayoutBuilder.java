package studio.mevera.lotus.api.pagination;

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
 * Generic builder for {@link PageLayout}.
 * Required: capacity (constructor), title, nav buttons. Optional:
 * fill mask (defaults to full minus nav slots), nav slots (default last row), decorations.
 */
public class PageLayoutBuilder<C, X extends AbstractPageContext<C, ?, ?>> {

    private final Capacity capacity;
    private SlotMask fillMask;
    private Slot previousSlot;
    private Slot nextSlot;
    private Function<X, C> title;
    private Function<X, Button> previousButton;
    private Function<X, Button> nextButton;
    private Function<X, Content> decorations;

    public PageLayoutBuilder(@NotNull Capacity capacity) {
        this.capacity = capacity;
        this.previousSlot = Slot.at(capacity.rows() - 1, 0, capacity);

        ItemStack previousPageItem = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = previousPageItem.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName(ChatColor.GREEN + "<- Previous Page");
            previousPageItem.setItemMeta(prevMeta);
        }
        this.previousButton = (ctx) -> Button.of(previousPageItem);

        this.nextSlot = Slot.at(capacity.rows() - 1, capacity.columns() - 1, capacity);

        ItemStack nextPageItem = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPageItem.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName(ChatColor.GREEN + "Next Page ->");
            nextPageItem.setItemMeta(nextMeta);
        }
        this.nextButton = (ctx) -> Button.of(nextPageItem);

        this.decorations = ctx -> Content.empty(this.capacity);
    }

    public @NotNull PageLayoutBuilder<C, X> title(@NotNull Function<X, C> title) {
        this.title = Objects.requireNonNull(title);
        return this;
    }

    public @NotNull PageLayoutBuilder<C, X> fillMask(@NotNull SlotMask mask) {
        this.fillMask = mask;
        return this;
    }

    public @NotNull PageLayoutBuilder<C, X> previousButton(
        @NotNull Slot slot,
        @NotNull Function<X, Button> button
    ) {
        this.previousSlot = slot;
        this.previousButton = button;
        return this;
    }

    public @NotNull PageLayoutBuilder<C, X> previousButton(@NotNull Function<X, Button> previousButton) {
        this.previousButton = previousButton;
        return this;
    }

    public @NotNull PageLayoutBuilder<C, X> nextButton(
        @NotNull Slot slot,
        @NotNull Function<X, Button> button
    ) {
        this.nextSlot = slot;
        this.nextButton = button;
        return this;
    }

    public @NotNull PageLayoutBuilder<C, X> nextButton(@NotNull Function<X, Button> nextButton) {
        this.nextButton = nextButton;
        return this;
    }

    public @NotNull PageLayoutBuilder<C, X> decorations(@NotNull Function<X, Content> decorations) {
        this.decorations = Objects.requireNonNull(decorations);
        return this;
    }

    public @NotNull PageLayout<C, X> build() {
        if (title == null) throw new IllegalStateException("title not configured");
        if (previousButton == null) throw new IllegalStateException("previousButton not configured");
        if (nextButton == null) throw new IllegalStateException("nextButton not configured");
        var resolvedMask = fillMask != null
            ? fillMask
            : SlotMask.full(capacity).excluding(previousSlot, nextSlot);
        return new BuiltPageLayout<>(capacity, resolvedMask, previousSlot, nextSlot,
            title, previousButton, nextButton, decorations);
    }

    private record BuiltPageLayout<C, X extends AbstractPageContext<C, ?, ?>>(
        @NotNull Capacity capacity,
        @NotNull SlotMask fillMask,
        @NotNull Slot previousButtonSlot,
        @NotNull Slot nextButtonSlot,
        @NotNull Function<X, C> titleFn,
        @NotNull Function<X, Button> previousButtonFn,
        @NotNull Function<X, Button> nextButtonFn,
        @NotNull Function<X, Content> decorationsFn
    ) implements PageLayout<C, X> {

        @Override public @NotNull C title(@NotNull X ctx) { return titleFn.apply(ctx); }
        @Override public @NotNull Button previousButton(@NotNull X ctx) { return previousButtonFn.apply(ctx); }
        @Override public @NotNull Button nextButton(@NotNull X ctx) { return nextButtonFn.apply(ctx); }
        @Override public @NotNull Content decorations(@NotNull X ctx) { return decorationsFn.apply(ctx); }
    }
}
