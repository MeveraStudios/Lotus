package studio.mevera.lotus.api.content;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Read-only view of a menu's buttons.
 * <p>
 * Use this when code should inspect content but not change it.
 */
public interface ContentView {

    /**
     * Returns the capacity this content was created for.
     */
    @NotNull Capacity capacity();

    /**
     * Returns the button at the given slot, if one exists.
     */
    @NotNull Optional<Button> get(@NotNull Slot slot);

    /**
     * Returns the number of occupied slots.
     */
    int size();

    /**
     * Returns whether no buttons are present.
     */
    boolean isEmpty();

    /**
     * Returns the next empty slot starting from {@code from}.
     */
    @NotNull Optional<Slot> nextEmpty(@NotNull Slot from);

    /**
     * Returns the stored entries as a stream.
     */
    @NotNull Stream<Map.Entry<Slot, Button>> entries();

    /**
     * Visits every stored button.
     */
    void forEach(@NotNull BiConsumer<Slot, Button> consumer);
}
