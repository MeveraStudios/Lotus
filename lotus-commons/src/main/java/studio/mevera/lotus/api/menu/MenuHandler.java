package studio.mevera.lotus.api.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Optional event hooks for a {@link Menu}.
 * <p>
 * Implement this when a menu needs to react to open, close, click, or drag events.
 */
public interface MenuHandler {

    /**
     * Runs before button dispatch.
     *
     * @return {@code true} to continue handling the click
     */
    default boolean onPreClick(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
        return true;
    }

    /**
     * Runs after button dispatch.
     */
    default void onPostClick(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
    }

    /**
     * Runs when the menu is opened.
     */
    default void onOpen(@NotNull MenuView<?> view, @NotNull InventoryOpenEvent event) {
    }

    /**
     * Runs when the menu is closed.
     */
    default void onClose(@NotNull MenuView<?> view, @NotNull InventoryCloseEvent event) {
    }

    /**
     * Runs when the menu receives a drag event.
     */
    default void onDrag(@NotNull MenuView<?> view, @NotNull InventoryDragEvent event) {
    }
}
