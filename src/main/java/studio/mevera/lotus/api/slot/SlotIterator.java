package studio.mevera.lotus.api.slot;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * Stateful iterator that walks a menu grid in a given {@link Direction}, optionally bounded by
 * an end slot. Wraps within capacity bounds — callers must check {@link #hasNext()} before each step.
 */
public final class SlotIterator {

    private final Capacity capacity;
    private final Direction direction;
    private final Slot end;

    private Slot cursor;
    private boolean exhausted;

    private SlotIterator(Capacity capacity, Slot start, Slot end, Direction direction) {
        this.capacity = capacity;
        this.direction = direction;
        this.end = end;
        this.cursor = start;
    }

    public static @NotNull SlotIterator of(@NotNull Capacity capacity,
                                            @NotNull Slot start,
                                            @NotNull Direction direction) {
        return new SlotIterator(capacity, start, null, direction);
    }

    public static @NotNull SlotIterator bounded(@NotNull Capacity capacity,
                                                 @NotNull Slot start,
                                                 @NotNull Slot end,
                                                 @NotNull Direction direction) {
        return new SlotIterator(capacity, start, end, direction);
    }

    public boolean hasNext() {
        return !exhausted && cursor != null;
    }

    public @NotNull Slot next() {
        if (!hasNext()) throw new NoSuchElementException("iterator exhausted");
        Slot current = cursor;
        advance();
        return current;
    }

    private void advance() {
        if (end != null && cursor.equals(end)) {
            exhausted = true;
            return;
        }
        int nextRow = cursor.row(capacity) + direction.rowDelta();
        int nextCol = cursor.column(capacity) + direction.columnDelta();
        if (nextRow < 0 || nextRow >= capacity.rows()
            || nextCol < 0 || nextCol >= capacity.columns()) {
            exhausted = true;
            return;
        }
        cursor = Slot.at(nextRow, nextCol, capacity);
    }
}
