package studio.mevera.lotus.api.slot;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Immutable description of a menu's grid dimensions.
 * <p>
 * Chest-style menus use 9 columns; other inventory types follow their fixed layout.
 */
public class Capacity {

    private static final int CHEST_COLUMNS = 9;
    private static final int MAX_CHEST_ROWS = 6;
    private static final int MAX_CHEST_SLOT = (MAX_CHEST_ROWS * CHEST_COLUMNS) - 1;

    private final int rows;
    private final int columns;

    public Capacity(int rows, int columns) {
        if (rows <= 0) throw new IllegalArgumentException("rows must be positive: " + rows);
        if (columns <= 0) throw new IllegalArgumentException("columns must be positive: " + columns);
        this.rows = rows;
        this.columns = columns;
    }

    private Capacity(int furthestSlot) {
        if (furthestSlot < 0) {
            throw new IllegalArgumentException("furthestSlot must be non-negative: " + furthestSlot);
        }
        if (furthestSlot > MAX_CHEST_SLOT) {
            throw new IllegalArgumentException("furthestSlot exceeds chest capacity: " + furthestSlot);
        }
        this.rows = Math.max(1, (furthestSlot / CHEST_COLUMNS) + 1);
        this.columns = CHEST_COLUMNS;
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }

    public int totalSize() {
        return rows * columns;
    }

    public boolean contains(int index) {
        return index >= 0 && index < totalSize();
    }

    public static @NotNull Capacity ofRows(int rows) {
        if (rows > MAX_CHEST_ROWS) {
            throw new IllegalArgumentException("rows must not exceed chest capacity: " + rows);
        }
        return new Capacity(rows, CHEST_COLUMNS);
    }

    public static @NotNull Capacity of(@NotNull InventoryType type) {
        Objects.requireNonNull(type, "type");
        return switch (type) {
            case CHEST, ENDER_CHEST -> ofRows(3);
            case HOPPER -> new Capacity(1, 5);
            case DROPPER, DISPENSER -> new Capacity(3, 3);
            case WORKBENCH -> new Capacity(2, 5);
            case FURNACE -> new Capacity(1, 3);
            default -> throw new IllegalArgumentException(
                "InventoryType " + type + " has no fixed grid; specify rows explicitly via ofRows(int)");
        };
    }

    public static @NotNull Capacity growing(int furthestSlot) {
        return new Capacity(furthestSlot);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Capacity other)) return false;
        return rows == other.rows && columns == other.columns;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, columns);
    }

    @Override
    public String toString() {
        return "Capacity[rows=" + rows + ", columns=" + columns + "]";
    }
}
