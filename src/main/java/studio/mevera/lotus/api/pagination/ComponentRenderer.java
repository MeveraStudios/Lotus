package studio.mevera.lotus.api.pagination;

import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.button.Button;

/**
 * Maps a domain object to a {@link Button} for placement within a page. Replaces the legacy
 * {@code PageComponent} pair of {@code toItem()} / {@code onClick()}.
 */
@FunctionalInterface
public interface ComponentRenderer<T> {

    @NotNull Button render(@NotNull T item, @NotNull PageContext context);
}
