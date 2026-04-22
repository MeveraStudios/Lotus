package studio.mevera.lotus.spigot.api.menu;

import studio.mevera.lotus.api.menu.InteractiveMenu;

/**
 * Spigot-specific {@link InteractiveMenu} specialization that combines menu
 * layout and interaction handling using plain {@link String} titles.
 * <p>
 * Implement this interface for menus that both render on Spigot and respond to
 * menu events through the interactive menu contract.
 */
public interface SpigotInteractiveMenu extends InteractiveMenu<String> {

}
