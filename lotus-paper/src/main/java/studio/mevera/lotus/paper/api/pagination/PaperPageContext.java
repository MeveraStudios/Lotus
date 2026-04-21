package studio.mevera.lotus.paper.api.pagination;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.pagination.AbstractPageContext;
import studio.mevera.lotus.paper.internal.pagination.PaperPaginationSession;

public final class PaperPageContext<T> extends AbstractPageContext<Component, T, PaperPaginationSession<T>> {

    public PaperPageContext(
        int pageIndex,
        int totalPages,
        @NotNull Player viewer,
        @NotNull PaperPaginationSession<T> session
    ) {
        super(pageIndex, totalPages, viewer, session);
    }
}
