package studio.mevera.lotus.api.menu;

/**
 * Convenience composition of {@link Menu} + {@link MenuHandler} for menus that want both layout
 * and lifecycle hooks in a single type.
 */
public interface InteractiveMenu extends Menu, MenuHandler {
}
