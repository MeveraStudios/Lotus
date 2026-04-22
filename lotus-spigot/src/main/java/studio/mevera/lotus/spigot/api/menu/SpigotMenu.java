package studio.mevera.lotus.spigot.api.menu;

import studio.mevera.lotus.api.menu.Menu;

/**
 * Spigot-specific {@link Menu} specialization that uses plain {@link String}
 * values for inventory titles.
 * <p>
 * Implement this interface when defining a menu for the Spigot platform where
 * title rendering is string-based rather than component-based.
 */
public interface SpigotMenu extends Menu<String>  {

}
