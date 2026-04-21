package studio.mevera.lotus.spigot.api.pagination;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.pagination.PageLayout;
import studio.mevera.lotus.api.slot.Capacity;

/**
 * Spigot-specific {@link PageLayout} alias where the title type is {@link String}.
 */
public interface SpigotPageLayout<T> extends PageLayout<String, SpigotPageContext<T>> {

    static <T> @NotNull SpigotPageLayoutBuilder<T> builder(@NotNull Capacity capacity) {
        return new SpigotPageLayoutBuilder<>(capacity);
    }
}
