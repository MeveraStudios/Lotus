package studio.mevera.lotus.internal.opener;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.internal.menu.BaseMenuView;
import studio.mevera.lotus.spi.opener.ViewOpener;

/**
 * Default opener — uses Paper's Adventure-native {@code createInventory} overloads for chest-style
 * and fixed-layout types. Delegates rendering to {@link BaseMenuView#renderInto(Inventory)}.
 */
public final class DefaultViewOpener implements ViewOpener {

    @Override
    public @NotNull Inventory open(@NotNull Lotus lotus, @NotNull MenuView<?> view) {
        Inventory inventory = createInventory(view);
        if (view instanceof BaseMenuView<?> base) {
            base.renderInto(inventory);
        } else {
            view.content().forEach((slot, button) -> inventory.setItem(slot.index(), button.item()));
        }
        view.viewer().openInventory(inventory);
        return inventory;
    }

    private static @NotNull Inventory createInventory(@NotNull MenuView<?> view) {
        InventoryType type = view.type();
        if (type == InventoryType.CHEST) {
            return Bukkit.createInventory(view, view.capacity().totalSize(), view.title());
        }
        return Bukkit.createInventory(view, type, view.title());
    }
}
