package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Read-only snapshot of a page's position within its session, passed to layout/renderer hooks.
 */
public record PageContext(
    int pageIndex,
    int totalPages,
    @NotNull Player viewer,
    @NotNull PaginationSession<?> session
) {
    public boolean isFirst() {
        return pageIndex == 0;
    }

    public boolean isLast() {
        return pageIndex == totalPages - 1;
    }
}
