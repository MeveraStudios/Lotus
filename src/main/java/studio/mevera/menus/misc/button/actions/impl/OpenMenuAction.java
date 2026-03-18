package studio.mevera.menus.misc.button.actions.impl;

import studio.mevera.menus.Lotus;
import studio.mevera.menus.base.MenuView;
import studio.mevera.menus.misc.button.actions.ButtonClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Defines an action to open menus based on a registered menu name
 * through the main api class {@link Lotus}
 */
public final class OpenMenuAction implements ButtonClickAction {
    
    private final String toOpen;
    
    public OpenMenuAction(String toOpen) {
        this.toOpen = toOpen;
    }
    
    @Override
    public String tag() {
        return "OPEN";
    }
    
    @Override
    public void execute(MenuView<?> view, InventoryClickEvent event) {
        view.getAPI().getRegisteredMenu(toOpen)
                .ifPresent((menu)-> view.getAPI().openMenu((Player) event.getWhoClicked(), menu));
    }
}
