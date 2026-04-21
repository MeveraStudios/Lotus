package studio.mevera.lotus.paper.api.pagination;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.pagination.PageLayout;
import studio.mevera.lotus.api.slot.Capacity;

/**
 * Paper-specific {@link PageLayout} alias where the title type is Adventure {@link Component}.
 * <p>
 * Build instances using {@link PaperPageLayoutBuilder}.
 */
public interface PaperPageLayout<T> extends PageLayout<Component, PaperPageContext<T>> {

    static <T> @NotNull PaperPageLayoutBuilder<T> builder(@NotNull Capacity capacity) {
        return new PaperPageLayoutBuilder<>(capacity);
    }
}
