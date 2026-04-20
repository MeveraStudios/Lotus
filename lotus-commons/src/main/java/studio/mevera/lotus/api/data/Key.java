package studio.mevera.lotus.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Strongly-typed identifier for values stored in a {@link DataRegistry}.
 * <p>
 * Two keys are equal iff their names match — type is metadata, not identity. This permits a
 * single registry slot to evolve type while preserving identity, but makes type mismatch a
 * runtime error caught at lookup ({@link DataRegistry#get(Key)}).
 */
public final class Key<T> {

    private final String name;
    private final Class<T> type;

    private Key(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T> @NotNull Key<T> of(@NotNull String name, @NotNull Class<T> type) {
        return new Key<>(Objects.requireNonNull(name), Objects.requireNonNull(type));
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull Class<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Key<?> k && name.equals(k.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Key[" + name + " : " + type.getSimpleName() + "]";
    }
}
