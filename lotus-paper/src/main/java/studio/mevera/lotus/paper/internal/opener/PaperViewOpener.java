package studio.mevera.lotus.paper.internal.opener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.internal.menu.BaseMenuView;
import studio.mevera.lotus.paper.api.menu.PaperMenu;
import studio.mevera.lotus.spi.opener.ViewOpener;

/**
 * Paper-specific {@link ViewOpener}. Uses the Paper overload
 * {@code Bukkit.createInventory(holder, size, Component)} for full Adventure fidelity.
 * <p>
 * If the menu implements {@link PaperMenu}, {@code title(view)} is called directly and
 * returns an Adventure {@link Component} (no serialization). Otherwise the String fallback
 * is wrapped via {@link Component#text(String)}.
 */
public final class PaperViewOpener implements ViewOpener<Component> {

    @Override
    public @NotNull Inventory open(@NotNull Lotus<Component> lotus, @NotNull MenuView<Component, ?> view) {
        Component title = resolveTitle(view);
        Inventory inventory = createInventory(view, title);
        if (view instanceof BaseMenuView<?, ?> base) {
            base.renderInto(inventory);
        } else {
            view.content().forEach((slot, button) -> inventory.setItem(slot.index(), button.item()));
        }
        view.viewer().openInventory(inventory);
        return inventory;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static @NotNull Component resolveTitle(@NotNull MenuView<Component, ?> view) {
        // instanceof PaperMenu → title() returns Component directly (no serialization, full fidelity)
        return view.title();
    }

    private static @NotNull Inventory createInventory(@NotNull MenuView<Component, ?> view, @NotNull Component title) {
        InventoryType type = view.type();
        if (type == InventoryType.CHEST) {
            return Bukkit.createInventory(view, view.capacity().totalSize(), title);
        }
        return Bukkit.createInventory(view, type, title);
    }
}
