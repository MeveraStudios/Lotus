package studio.mevera.lotus.api.pagination;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;

/**
 * Converts one page item into a {@link Button}.
 */
@FunctionalInterface
public interface ComponentRenderer<T> {

    /**
     * Creates the button to show for the given item.
     */
    @NotNull Button render(@NotNull T item, @NotNull PageContext context);
}
