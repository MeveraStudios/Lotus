package studio.mevera.lotus.api.slot;

import org.jetbrains.annotations.NotNull;

/**
 * Position within a menu, identified by its raw inventory index.
 * <p>
 * Row/column derivation is capacity-relative — see {@link #row(Capacity)} and {@link #column(Capacity)}.
 */
public record Slot(int index) implements Comparable<Slot> {

    public Slot {
        if (index < 0) throw new IllegalArgumentException("slot index must be non-negative: " + index);
    }

    public static @NotNull Slot of(int index) {
        return new Slot(index);
    }

    public static @NotNull Slot at(int row, int column, @NotNull Capacity capacity) {
        if (row < 0 || row >= capacity.rows())
            throw new IllegalArgumentException("row out of bounds: " + row);
        if (column < 0 || column >= capacity.columns())
            throw new IllegalArgumentException("column out of bounds: " + column);
        return new Slot(row * capacity.columns() + column);
    }

    public static @NotNull Slot first() {
        return new Slot(0);
    }

    public static @NotNull Slot last(@NotNull Capacity capacity) {
        return new Slot(capacity.totalSize() - 1);
    }

    public int row(@NotNull Capacity capacity) {
        return index / capacity.columns();
    }

    public int column(@NotNull Capacity capacity) {
        return index % capacity.columns();
    }

    public @NotNull Slot translate(int delta) {
        return new Slot(index + delta);
    }

    @Override
    public int compareTo(@NotNull Slot other) {
        return Integer.compare(index, other.index);
    }
}
