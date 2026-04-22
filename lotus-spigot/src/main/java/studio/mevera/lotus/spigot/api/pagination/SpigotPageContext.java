package studio.mevera.lotus.spigot.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.pagination.AbstractPageContext;
import studio.mevera.lotus.spigot.internal.pagination.SpigotPaginationSession;

public final class SpigotPageContext<T> extends AbstractPageContext<String, T, SpigotPaginationSession<T>> {

    public SpigotPageContext(
        int pageIndex,
        int totalPages,
        @NotNull Player viewer,
        @NotNull SpigotPaginationSession<T> session
    ) {
        super(pageIndex, totalPages, viewer, session);
    }
}
