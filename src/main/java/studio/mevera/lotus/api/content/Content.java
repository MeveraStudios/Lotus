package studio.mevera.lotus.api.content;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.internal.content.DefaultContent;

/**
 * Read-write content. Composes {@link ContentView} and {@link ContentEditor}; obtain a new
 * instance via {@link #empty(Capacity)} or {@link #builder(Capacity)}.
 */
public interface Content extends ContentView, ContentEditor {

    @NotNull Content mergeWith(@NotNull ContentView other);

    void trimTo(int maxButtons);

    static @NotNull Content empty(@NotNull Capacity capacity) {
        return new DefaultContent(capacity);
    }

    static @NotNull ContentBuilder builder(@NotNull Capacity capacity) {
        return new ContentBuilder(capacity);
    }
}
