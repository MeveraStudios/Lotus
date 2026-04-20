package studio.mevera.lotus.api.content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.SpanningButton;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

import java.util.function.UnaryOperator;

/**
 * Write operations for menu content.
 * <p>
 * This interface is separate from {@link ContentView} so read-only code does not get mutation
 * methods by accident.
 */
public interface ContentEditor {

    /**
     * Sets or clears the button at the given slot.
     */
    void set(@NotNull Slot slot, @Nullable Button button);

    /**
     * Removes the button at the given slot.
     */
    void remove(@NotNull Slot slot);

    /**
     * Replaces the button at the given slot using the updater result.
     */
    void update(@NotNull Slot slot, @NotNull UnaryOperator<Button> updater);

    /**
     * Fills all slots in the mask with the same button.
     */
    void fill(@NotNull SlotMask mask, @NotNull Button button);

    /**
     * Places the same spanning button into every slot in its footprint.
     */
    default void placeSpanning(@NotNull SpanningButton button) {
        button.footprint().forEach(slot -> set(slot, button));
    }

    /**
     * Removes all buttons.
     */
    void clear();
}
