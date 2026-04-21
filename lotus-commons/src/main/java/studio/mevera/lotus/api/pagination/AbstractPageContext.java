package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Read-only snapshot of a page's position within its session, passed to layout/renderer hooks.
 */
public abstract class AbstractPageContext<C, T, P extends PaginationSession<C, T, ?>> {

    private final int pageIndex;
    private final int totalPages;
    private final Player viewer;
    private final P session;

    protected AbstractPageContext(
        int pageIndex,
        int totalPages,
        @NotNull Player viewer,
        @NotNull P session
    ) {
        this.pageIndex = pageIndex;
        this.totalPages = totalPages;
        this.viewer = Objects.requireNonNull(viewer);
        this.session = Objects.requireNonNull(session);
    }

    public int pageIndex() {
        return pageIndex;
    }

    public int totalPages() {
        return totalPages;
    }

    public @NotNull Player viewer() {
        return viewer;
    }

    public @NotNull P session() {
        return session;
    }

    public boolean isFirst() {
        return pageIndex == 0;
    }

    public boolean isLast() {
        return pageIndex == totalPages - 1;
    }
}
