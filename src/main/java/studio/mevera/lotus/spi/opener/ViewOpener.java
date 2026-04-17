package studio.mevera.lotus.spi.opener;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.menu.MenuView;

/**
 * Strategy for materialising a {@link MenuView} into a Bukkit {@link Inventory}. Registered per
 * {@link org.bukkit.event.inventory.InventoryType} on the {@link Lotus} facade; the default
 * implementation handles chest-style and fixed-layout types.
 */
public interface ViewOpener {

    @NotNull Inventory open(@NotNull Lotus lotus, @NotNull MenuView<?> view);
}
