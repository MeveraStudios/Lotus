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
 * A live, per-player projection of a {@link Menu}. Holds the inventory, content, and per-view
 * data; routes events to the underlying menu (and its {@link MenuHandler} if present).
 *
 * @param <M> the menu template type
 */
public interface MenuView<M extends Menu> extends InventoryHolder {

    @NotNull Lotus lotus();

    @NotNull M menu();

    @NotNull Player viewer();

    @NotNull DataRegistry data();

    @NotNull Content content();

    @NotNull Component title();

    @NotNull Capacity capacity();

    @Override
    @Nullable Inventory getInventory();

    boolean isOpen();

    /**
     * Re-renders the view from the menu template. Discards the current content, rebuilds it via
     * {@link Menu#content(MenuView)}, and re-paints the underlying inventory in place.
     */
    void refresh();

    default @NotNull InventoryType type() {
        return menu().type();
    }
}
