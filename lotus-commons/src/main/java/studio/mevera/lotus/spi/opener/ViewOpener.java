package studio.mevera.lotus.spi.opener;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.menu.MenuView;

/**
 * Opens a {@link MenuView} as a Bukkit {@link Inventory}.
 * <p>
 * Different inventory types may use different opener implementations.
 */
public interface ViewOpener<C> {

    /**
     * Creates and opens the inventory for the given view.
     */
    @NotNull Inventory open(@NotNull Lotus<C> lotus, @NotNull MenuView<C, ?> view);
}
