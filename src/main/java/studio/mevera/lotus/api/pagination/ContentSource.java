package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Provides the full list of items to be paginated, evaluated once per session at open time.
 */
@FunctionalInterface
public interface ContentSource<T> {

    @NotNull List<T> provide(@NotNull Player viewer);

    static <T> @NotNull ContentSource<T> of(@NotNull List<T> items) {
        var copy = List.copyOf(items);
        return viewer -> copy;
    }

    static <T> @NotNull ContentSource<T> dynamic(@NotNull Function<Player, List<T>> source) {
        return source::apply;
    }
}
