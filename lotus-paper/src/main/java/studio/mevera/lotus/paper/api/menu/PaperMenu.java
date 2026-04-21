package studio.mevera.lotus.paper.api.menu;

import net.kyori.adventure.text.Component;
import studio.mevera.lotus.api.menu.Menu;

/**
 * Paper-specific {@link Menu} alias where the title type is Adventure {@link Component}.
 * <p>
 * Implement this interface on Paper 1.21+ plugins instead of {@link Menu} directly.
 * The single method to implement is:
 * <pre>{@code
 *   Component title(MenuView<Component, ?> view);
 * }</pre>
 * Full Adventure fidelity is preserved — hover events, click events, gradients, etc. all work.
 * No serialization round-trip is performed.
 */
public interface PaperMenu extends Menu<Component> {
}
