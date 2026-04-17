package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Supplies the items that a pagination session will show.
 */
@FunctionalInterface
public interface ContentSource<T> {

    /**
     * Returns the full item list for the viewer.
     */
    @NotNull List<T> provide(@NotNull Player viewer);

    /**
     * Returns a source backed by a fixed list.
     */
    static <T> @NotNull ContentSource<T> of(@NotNull List<T> items) {
        var copy = List.copyOf(items);
        return viewer -> copy;
    }

    /**
     * Returns a source backed by a function.
     */
    static <T> @NotNull ContentSource<T> dynamic(@NotNull Function<Player, List<T>> source) {
        return source::apply;
    }
}
