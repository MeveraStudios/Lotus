package studio.mevera.lotus.api.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Optional lifecycle hooks for a {@link Menu}. Split from {@code Menu} so a pure layout template
 * carries no event-handling surface; menus that want hooks implement this alongside.
 */
public interface MenuHandler {

    default boolean onPreClick(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
        return true;
    }

    default void onPostClick(@NotNull MenuView<?> view, @NotNull InventoryClickEvent event) {
    }

    default void onOpen(@NotNull MenuView<?> view, @NotNull InventoryOpenEvent event) {
    }

    default void onClose(@NotNull MenuView<?> view, @NotNull InventoryCloseEvent event) {
    }

    default void onDrag(@NotNull MenuView<?> view, @NotNull InventoryDragEvent event) {
    }
}
