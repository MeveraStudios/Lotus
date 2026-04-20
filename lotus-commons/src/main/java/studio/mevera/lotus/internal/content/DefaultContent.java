package studio.mevera.lotus.internal.content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.SpanningButton;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.content.ContentView;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotMask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Default {@link Content} implementation backed by a {@code HashMap<Slot, Button>}. Not thread-safe;
 * mutated only on the server main thread.
 */
public final class DefaultContent implements Content {

    private final Capacity capacity;
    private final Map<Slot, Button> buttons = new HashMap<>();

    public DefaultContent(@NotNull Capacity capacity) {
        this.capacity = capacity;
    }

    @Override public @NotNull Capacity capacity() { return capacity; }

    @Override public @NotNull Optional<Button> get(@NotNull Slot slot) {
        return Optional.ofNullable(buttons.get(slot));
    }

    @Override public int size() { return buttons.size(); }

    @Override public boolean isEmpty() { return buttons.isEmpty(); }

    @Override
    public @NotNull Optional<Slot> nextEmpty(@NotNull Slot from) {
        for (int i = from.index(); i < capacity.totalSize(); i++) {
            Slot slot = Slot.of(i);
            if (!buttons.containsKey(slot)) return Optional.of(slot);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Stream<Map.Entry<Slot, Button>> entries() {
        return buttons.entrySet().stream();
    }

    @Override public void forEach(@NotNull BiConsumer<Slot, Button> consumer) {
        buttons.forEach(consumer);
    }

    @Override
    public void set(@NotNull Slot slot, @Nullable Button button) {
        if (!capacity.contains(slot.index()))
            throw new IndexOutOfBoundsException("slot " + slot + " out of capacity " + capacity);
        if (button == null) buttons.remove(slot);
        else buttons.put(slot, button);
    }

    @Override public void remove(@NotNull Slot slot) {
        buttons.remove(slot);
    }

    @Override
    public void update(@NotNull Slot slot, @NotNull UnaryOperator<Button> updater) {
        Button current = buttons.get(slot);
        if (current == null) return;
        Button updated = updater.apply(current);
        if (updated == null) buttons.remove(slot);
        else buttons.put(slot, updated);
    }

    @Override
    public void fill(@NotNull SlotMask mask, @NotNull Button button) {
        mask.stream().forEach(slot -> set(slot, button));
    }

    @Override public void clear() {
        buttons.clear();
    }

    @Override
    public @NotNull Content mergeWith(@NotNull ContentView other) {
        var merged = new DefaultContent(capacity);
        merged.buttons.putAll(this.buttons);
        other.forEach(merged.buttons::put);
        return merged;
    }

    @Override
    public void trimTo(int maxButtons) {
        if (buttons.size() <= maxButtons) return;
        var iterator = buttons.entrySet().iterator();
        int kept = 0;
        while (iterator.hasNext()) {
            iterator.next();
            if (kept++ >= maxButtons) iterator.remove();
        }
    }
}
