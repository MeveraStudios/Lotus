package studio.mevera.lotus.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.data.DataRegistry;
import studio.mevera.lotus.api.slot.Capacity;

/**
 * Live view of a {@link Menu} for one player.
 * <p>
 * A menu view holds the resolved inventory state, content, and per-view data.
 *
 * @param <C> the type representing the title.
 * @param <M> the menu type
 */
public interface MenuView<C, M extends Menu<C>> extends InventoryHolder {

    /**
     * Returns the Lotus runtime that owns this view.
     */
    @NotNull Lotus lotus();

    /**
     * Returns the menu template behind this view.
     */
    @NotNull M menu();

    /**
     * Returns the title of the view.
     * @return the title of the view.
     */
    @NotNull C title();

    /**
     * Returns the player viewing this menu.
     */
    @NotNull Player viewer();

    /**
     * Returns the per-view data registry.
     */
    @NotNull DataRegistry data();

    /**
     * Returns the current content.
     */
    @NotNull Content content();

    /**
     * Returns the resolved capacity.
     */
    @NotNull Capacity capacity();

    @Override
    @Nullable Inventory getInventory();

    /**
     * Returns whether the view is currently open.
     */
    boolean isOpen();

    /**
     * Rebuilds the resolved menu state and reapplies it to the live inventory.
     * <p>
     * Implementations may recreate the inventory when structural properties such
     * as title, type, or capacity change.
     */
    void refresh();

    /**
     * Returns the inventory type used by this view.
     */
    default @NotNull InventoryType type() {
        return menu().type();
    }
}
