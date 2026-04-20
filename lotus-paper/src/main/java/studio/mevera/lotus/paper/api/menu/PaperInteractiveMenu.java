package studio.mevera.lotus.paper.api.menu;

import net.kyori.adventure.text.Component;
import studio.mevera.lotus.api.menu.InteractiveMenu;

/**
 * Paper-specific {@link InteractiveMenu} alias where the title type is Adventure {@link Component}.
 * <p>
 * Implement this interface when your menu needs both Adventure Component titles and lifecycle
 * hooks ({@code onOpen}, {@code onClose}, {@code onPreClick}, etc.).
 */
public interface PaperInteractiveMenu extends InteractiveMenu<Component> {
}
