package studio.mevera.lotus.spigot.internal.opener;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.internal.menu.BaseMenuView;
import studio.mevera.lotus.spi.opener.ViewOpener;

/**
 * Spigot 1.8.8 {@link ViewOpener}. Uses the legacy
 * {@code Bukkit.createInventory(holder, size, String)} overload — Spigot 1.8.8 predates Adventure.
 * <p>
 * Menus on Spigot implement {@code Menu<String>} (or {@code SpigotMenu}). The title string is
 * taken verbatim from {@code menu.title(view)} — users are expected to apply {@code ChatColor}
 * translation themselves (typically via the {@code ItemBuilder}).
 */
public final class SpigotViewOpener implements ViewOpener<String> {

    @Override
    public @NotNull Inventory open(@NotNull Lotus<String> lotus, @NotNull MenuView<String, ?> view) {
        String title = resolveTitle(view);
        Inventory inventory = createInventory(view, title);
        if (view instanceof BaseMenuView<?, ?> base) {
            base.renderInto(inventory);
        } else {
            view.content().forEach((slot, button) -> inventory.setItem(slot.index(), button.item()));
        }
        view.viewer().openInventory(inventory);
        return inventory;
    }

    private static @NotNull String resolveTitle(@NotNull MenuView<String, ?> view) {
        // On Spigot, menus implement Menu<String> — title() returns a String directly.
        return view.title();
    }

    private static @NotNull Inventory createInventory(@NotNull MenuView<String, ?> view, @NotNull String title) {
        InventoryType type = view.type();
        if (type == InventoryType.CHEST) {
            return Bukkit.createInventory(view, view.capacity().totalSize(), title);
        }
        return Bukkit.createInventory(view, type, title);
    }
}
