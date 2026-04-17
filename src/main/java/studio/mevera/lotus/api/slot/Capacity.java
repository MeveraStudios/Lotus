package studio.mevera.lotus.api.slot;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Immutable description of a menu's grid dimensions.
 * <p>
 * Chest-style menus use 9 columns; other inventory types follow their fixed layout.
 */
public record Capacity(int rows, int columns) {

    public Capacity {
        if (rows <= 0) throw new IllegalArgumentException("rows must be positive: " + rows);
        if (columns <= 0) throw new IllegalArgumentException("columns must be positive: " + columns);
    }

    public int totalSize() {
        return rows * columns;
    }

    public boolean contains(int index) {
        return index >= 0 && index < totalSize();
    }

    public static @NotNull Capacity ofRows(int rows) {
        return new Capacity(rows, 9);
    }

    public static @NotNull Capacity of(@NotNull InventoryType type) {
        Objects.requireNonNull(type, "type");
        return switch (type) {
            case CHEST, ENDER_CHEST -> ofRows(3);
            case HOPPER -> new Capacity(1, 5);
            case DROPPER, DISPENSER -> new Capacity(3, 3);
            case WORKBENCH -> new Capacity(2, 5);
            case FURNACE, BLAST_FURNACE, SMOKER -> new Capacity(1, 3);
            default -> throw new IllegalArgumentException(
                "InventoryType " + type + " has no fixed grid; specify rows explicitly via ofRows(int)");
        };
    }
}
