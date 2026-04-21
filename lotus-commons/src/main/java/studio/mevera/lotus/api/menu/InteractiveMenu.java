package studio.mevera.lotus.api.menu;

/**
 * Menu type that includes both layout and event hooks.
 *
 * @param <C> the title component type — {@code Component} on Paper, {@code String} on Spigot
 */
public interface InteractiveMenu<C> extends Menu<C>, MenuHandler<C> {
}
