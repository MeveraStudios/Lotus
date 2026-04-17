package studio.mevera.lotus.api.content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.SpanningButton;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

import java.util.function.UnaryOperator;

/**
 * Mutating operations on a menu's button layout. Distinct from {@link ContentView} so handlers
 * that should not mutate (e.g. read-only observers, serializers) cannot.
 */
public interface ContentEditor {

    void set(@NotNull Slot slot, @Nullable Button button);

    void remove(@NotNull Slot slot);

    void update(@NotNull Slot slot, @NotNull UnaryOperator<Button> updater);

    void fill(@NotNull SlotMask mask, @NotNull Button button);

    /**
     * Places the same {@link SpanningButton} instance into every slot in its footprint so any of
     * those slots resolves back to the same logical button on click.
     */
    default void placeSpanning(@NotNull SpanningButton button) {
        button.footprint().forEach(slot -> set(slot, button));
    }

    void clear();
}
