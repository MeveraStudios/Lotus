package studio.mevera.lotus.api.pagination;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

/**
 * Pure layout descriptor for a paginated menu — capacity, title, fill area, navigation positions
 * and items, plus optional decorations. Stateless and shared across pages and viewers.
 */
public interface PageLayout {

    @NotNull Capacity capacity();

    @NotNull Component title(@NotNull PageContext context);

    @NotNull SlotMask fillMask();

    @NotNull Slot previousButtonSlot();

    @NotNull Slot nextButtonSlot();

    @NotNull Button previousButton(@NotNull PageContext context);

    @NotNull Button nextButton(@NotNull PageContext context);

    /**
     * Static decorations painted on every page (borders, info panels). Defaults to none.
     */
    default @NotNull Content decorations(@NotNull PageContext context) {
        return Content.empty(capacity());
    }

    static @NotNull PageLayoutBuilder builder(@NotNull Capacity capacity) {
        return new PageLayoutBuilder(capacity);
    }
}
