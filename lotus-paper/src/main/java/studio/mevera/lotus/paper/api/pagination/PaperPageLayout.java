package studio.mevera.lotus.paper.api.pagination;

import net.kyori.adventure.text.Component;
import studio.mevera.lotus.api.pagination.PageLayout;

/**
 * Paper-specific {@link PageLayout} alias where the title type is Adventure {@link Component}.
 * <p>
 * Build instances using {@link PaperPageLayoutBuilder}.
 */
public interface PaperPageLayout extends PageLayout<Component> {
}
