package studio.mevera.lotus.api.content;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Direction;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.api.slot.SlotIterator;
import studio.mevera.lotus.api.slot.SlotMask;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link Content}. All mutation lives here so the runtime {@link ContentEditor}
 * surface stays small; user-facing layout DSL is contained to construction time.
 */
public final class ContentBuilder {

    private final Content content;

    ContentBuilder(@NotNull Capacity capacity) {
        this.content = Content.empty(capacity);
    }

    public @NotNull Capacity capacity() {
        return content.capacity();
    }

    public @NotNull ContentBuilder set(@NotNull Slot slot, @NotNull Button button) {
        content.set(slot, Objects.requireNonNull(button));
        return this;
    }

    public @NotNull ContentBuilder set(int row, int column, @NotNull Button button) {
        return set(Slot.at(row, column, content.capacity()), button);
    }

    public @NotNull ContentBuilder fill(@NotNull SlotMask mask, @NotNull Button button) {
        content.fill(mask, button);
        return this;
    }

    public @NotNull ContentBuilder fillAll(@NotNull Button button) {
        return fill(SlotMask.full(content.capacity()), button);
    }

    public @NotNull ContentBuilder fillBorder(@NotNull Button button) {
        var capacity = content.capacity();
        for (int r = 0; r < capacity.rows(); r++) {
            for (int c = 0; c < capacity.columns(); c++) {
                if (r == 0 || r == capacity.rows() - 1 || c == 0 || c == capacity.columns() - 1) {
                    content.set(Slot.at(r, c, capacity), button);
                }
            }
        }
        return this;
    }

    public @NotNull ContentBuilder draw(@NotNull Slot start, @NotNull Direction direction, @NotNull Button button) {
        var iterator = SlotIterator.of(content.capacity(), start, direction);
        while (iterator.hasNext()) content.set(iterator.next(), button);
        return this;
    }

    public @NotNull ContentBuilder draw(@NotNull Slot start, @NotNull Slot end,
                                        @NotNull Direction direction, @NotNull Button button) {
        var iterator = SlotIterator.bounded(content.capacity(), start, end, direction);
        while (iterator.hasNext()) content.set(iterator.next(), button);
        return this;
    }

    public @NotNull ContentBuilder buttons(@NotNull Map<Slot, Button> buttons) {
        buttons.forEach(content::set);
        return this;
    }

    public @NotNull ContentBuilder apply(@NotNull Consumer<Content> mutator) {
        mutator.accept(content);
        return this;
    }

    public @NotNull Content build() {
        return content;
    }
}
