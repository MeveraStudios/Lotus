package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Live pagination state for one player.
 * <p>
 * A session tracks the current page and opens page menus as the player navigates.
 */
public interface PaginationSession<C, T, X extends AbstractPageContext<C, T, ?>> {

    /**
     * Returns the shared pagination definition.
     */
    @NotNull AbstractPagination<C, T, ? super X> definition();

    /**
     * Returns the player viewing this session.
     */
    @NotNull Player viewer();

    /**
     * Returns the current page index.
     */
    int currentIndex();

    /**
     * Returns the total number of pages.
     */
    int totalPages();

    /**
     * Opens the next page when possible.
     */
    void next();

    /**
     * Opens the previous page when possible.
     */
    void previous();

    /**
     * Opens the given page index.
     */
    void goTo(int pageIndex);

    /**
     * Rebuilds the session snapshot and reopens the current page from scratch.
     * This recalculates page count and re-renders title, capacity, and content.
     */
    void reload();

    /**
     * Closes the session and the viewer's inventory.
     */
    void close();

    /**
     * Returns whether the current page is the first page.
     */
    default boolean isFirst() {
        return currentIndex() == 0;
    }

    /**
     * Returns whether the current page is the last page.
     */
    default boolean isLast() {
        return currentIndex() == totalPages() - 1;
    }
}
