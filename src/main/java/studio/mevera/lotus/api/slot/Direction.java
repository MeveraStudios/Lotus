package studio.mevera.lotus.api.slot;

import org.jetbrains.annotations.NotNull;

/**
 * Traversal direction across a menu grid. Each direction defines a row and column step.
 */
public enum Direction {
    RIGHT(0, 1),
    LEFT(0, -1),
    DOWN(1, 0),
    UP(-1, 0),
    DOWN_RIGHT(1, 1),
    DOWN_LEFT(1, -1),
    UP_RIGHT(-1, 1),
    UP_LEFT(-1, -1);

    private final int rowDelta;
    private final int columnDelta;

    Direction(int rowDelta, int columnDelta) {
        this.rowDelta = rowDelta;
        this.columnDelta = columnDelta;
    }

    public int rowDelta() {
        return rowDelta;
    }

    public int columnDelta() {
        return columnDelta;
    }

    public @NotNull Direction inverse() {
        return switch (this) {
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
            case DOWN -> UP;
            case UP -> DOWN;
            case DOWN_RIGHT -> UP_LEFT;
            case DOWN_LEFT -> UP_RIGHT;
            case UP_RIGHT -> DOWN_LEFT;
            case UP_LEFT -> DOWN_RIGHT;
        };
    }
}
