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
 * @param <X> the page context type supplied to layout hooks
 */
public interface PageLayout<C, X extends AbstractPageContext<C, ?, ?>> {

    /**
     * Returns the page capacity.
     */
    @NotNull Capacity capacity();

    /**
     * Returns the title for the current page.
     */
    @NotNull C title(@NotNull X context);

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
    @NotNull Button previousButton(@NotNull X context);

    /**
     * Returns the next-page button.
     */
    @NotNull Button nextButton(@NotNull X context);

    /**
     * Returns the static decorations for the current page.
     */
    default @NotNull Content decorations(@NotNull X context) {
        return Content.empty(capacity());
    }

    /**
     * Creates a generic builder for the given capacity.
     */
    static <C, X extends AbstractPageContext<C, ?, ?>> @NotNull PageLayoutBuilder<C, X> builder(@NotNull Capacity capacity) {
        return new PageLayoutBuilder<>(capacity);
    }
}
