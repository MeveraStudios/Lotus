package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Per-player runtime of a {@link Pagination}. Owns the snapshot of source items, current page
 * index, and navigation methods. Created via {@link Pagination#open(studio.mevera.lotus.Lotus, Player)}.
 */
public interface PaginationSession<T> {

    @NotNull Pagination<T> definition();

    @NotNull Player viewer();

    int currentIndex();

    int totalPages();

    void next();

    void previous();

    void goTo(int pageIndex);

    void close();

    default boolean isFirst() {
        return currentIndex() == 0;
    }

    default boolean isLast() {
        return currentIndex() == totalPages() - 1;
    }
}
