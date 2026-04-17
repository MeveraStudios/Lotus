package studio.mevera.lotus.api.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.slot.Capacity;

/**
 * Defines the layout of a menu.
 * <p>
 * A menu describes its title, size, and content for a specific {@link MenuView}.
 */
public interface Menu {

    /**
     * Returns the title for the current view.
     */
    @NotNull Component title(@NotNull MenuView<?> view);

    /**
     * Returns the capacity for the current view.
     */
    @NotNull Capacity capacity(@NotNull MenuView<?> view);

    /**
     * Builds the content for the current view.
     */
    @NotNull Content content(@NotNull MenuView<?> view);

    /**
     * Returns the menu name used for registration and logging.
     */
    default @NotNull String name() {
        return getClass().getSimpleName();
    }

    /**
     * Returns the Bukkit inventory type used by this menu.
     */
    default @NotNull InventoryType type() {
        return InventoryType.CHEST;
    }
}
