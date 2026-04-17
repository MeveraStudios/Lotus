package studio.mevera.lotus.api.menu;

import net.kyori.adventure.text.Component;
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
 * @param <M> the menu type
 */
public interface MenuView<M extends Menu> extends InventoryHolder {

    /**
     * Returns the Lotus runtime that owns this view.
     */
    @NotNull Lotus lotus();

    /**
     * Returns the menu template behind this view.
     */
    @NotNull M menu();

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
     * Returns the resolved title.
     */
    @NotNull Component title();

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
     * Rebuilds the content from the menu and repaints the inventory.
     */
    void refresh();

    /**
     * Returns the inventory type used by this view.
     */
    default @NotNull InventoryType type() {
        return menu().type();
    }
}
