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
 * Read-only projection of a menu's button layout. Held by {@link studio.mevera.lotus.api.menu.MenuView}
 * for callers that should observe but not mutate.
 */
public interface ContentView {

    @NotNull Capacity capacity();

    @NotNull Optional<Button> get(@NotNull Slot slot);

    int size();

    boolean isEmpty();

    @NotNull Optional<Slot> nextEmpty(@NotNull Slot from);

    @NotNull Stream<Map.Entry<Slot, Button>> entries();

    void forEach(@NotNull BiConsumer<Slot, Button> consumer);
}
