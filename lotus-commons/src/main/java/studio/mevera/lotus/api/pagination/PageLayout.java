package studio.mevera.lotus.api.pagination;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

/**
 * Defines the shared layout for all pages in a pagination.
 *
 * @param <C> the title component type — {@code Component} on Paper, {@code String} on Spigot
 */
public interface PageLayout<C> {

    /**
     * Returns the page capacity.
     */
    @NotNull Capacity capacity();

    /**
     * Returns the title for the current page.
     */
    @NotNull C title(@NotNull PageContext context);

    /**
     * Returns the slots used for page items.
     */
    @NotNull SlotMask fillMask();

    /**
     * Returns the slot used by the previous-page button.
     */
    @NotNull Slot previousButtonSlot();

    /**
     * Returns the slot used by the next-page button.
     */
    @NotNull Slot nextButtonSlot();

    /**
     * Returns the previous-page button.
     */
    @NotNull Button previousButton(@NotNull PageContext context);

    /**
     * Returns the next-page button.
     */
    @NotNull Button nextButton(@NotNull PageContext context);

    /**
     * Returns the static decorations for the current page.
     */
    default @NotNull Content decorations(@NotNull PageContext context) {
        return Content.empty(capacity());
    }

    /**
     * Creates a builder for the given capacity. Builds a {@link PageLayout}{@code <String>}
     * compatible with Spigot 1.8.8 and above.
     */
    static @NotNull PageLayoutBuilder builder(@NotNull Capacity capacity) {
        return new PageLayoutBuilder(capacity);
    }
}
