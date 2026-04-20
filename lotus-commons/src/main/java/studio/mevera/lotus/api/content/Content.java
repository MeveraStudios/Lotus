package studio.mevera.lotus.api.content;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.internal.content.DefaultContent;

import java.util.Optional;

/**
 * Full read-write content for a menu.
 * <p>
 * This combines the read-only view from {@link ContentView} with the write operations from
 * {@link ContentEditor}.
 */
public interface Content extends ContentView, ContentEditor {

    /**
     * Returns a new content object with entries from both sources.
     * <p>
     * Entries from {@code other} overwrite entries from this content when both use the same slot.
     */
    @NotNull Content mergeWith(@NotNull ContentView other);

    /**
     * Removes extra buttons after the given limit is reached.
     */
    void trimTo(int maxButtons);

    /**
     * Places the button into the first empty slot, returning that slot if one was found.
     */
    default @NotNull Optional<Slot> add(@NotNull Button button) {
        Optional<Slot> target = nextEmpty(Slot.of(0));
        target.ifPresent(slot -> set(slot, button));
        return target;
    }

    /**
     * Creates an empty content object for the given capacity.
     */
    static @NotNull Content empty(@NotNull Capacity capacity) {
        return new DefaultContent(capacity);
    }

    /**
     * Creates a builder for the given capacity.
     */
    static @NotNull ContentBuilder builder(@NotNull Capacity capacity) {
        return new ContentBuilder(capacity);
    }
}
