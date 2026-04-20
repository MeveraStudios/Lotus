package studio.mevera.lotus.api.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Type-safe heterogeneous container keyed by {@link Key}. Replaces the legacy
 * {@code String -> Object} bag — values are validated against their key's declared type at
 * insert and at read.
 * <p>
 * Not thread-safe. Lotus mutators run on the server main thread.
 */
public final class DataRegistry {

    private final Map<Key<?>, Object> values;

    private DataRegistry(Map<Key<?>, Object> values) {
        this.values = values;
    }

    public static @NotNull DataRegistry empty() {
        return new DataRegistry(new HashMap<>());
    }

    public <T> @NotNull DataRegistry put(@NotNull Key<T> key, @NotNull T value) {
        if (!key.type().isInstance(value)) {
            throw new ClassCastException(
                "value of type " + value.getClass().getName() + " is not assignable to " + key);
        }
        values.put(key, value);
        return this;
    }

    public <T> @NotNull Optional<T> get(@NotNull Key<T> key) {
        return Optional.ofNullable(getOrNull(key));
    }

    public <T> @NotNull T require(@NotNull Key<T> key) {
        T value = getOrNull(key);
        if (value == null) throw new IllegalStateException("missing required key: " + key);
        return value;
    }

    public <T> @Nullable T getOrNull(@NotNull Key<T> key) {
        Object raw = values.get(key);
        if (raw == null) return null;
        if (!key.type().isInstance(raw)) {
            throw new ClassCastException(
                "stored value " + raw.getClass().getName() + " is not assignable to " + key);
        }
        return key.type().cast(raw);
    }

    public boolean contains(@NotNull Key<?> key) {
        return values.containsKey(key);
    }

    public void remove(@NotNull Key<?> key) {
        values.remove(key);
    }

    public int size() {
        return values.size();
    }
}
