package studio.mevera.lotus.paper.api.pagination;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.api.pagination.AbstractPagination;
import studio.mevera.lotus.api.pagination.ComponentRenderer;
import studio.mevera.lotus.api.pagination.ContentSource;
import studio.mevera.lotus.api.pagination.PageLayout;
import studio.mevera.lotus.paper.internal.pagination.PaperPaginationSession;

/**
 * Paper-facing pagination definition with Adventure titles and Paper page contexts.
 */
public final class Pagination<T> extends AbstractPagination<Component, T, PaperPageContext<T>> {

    private Pagination(
        @NotNull String id,
        @NotNull PageLayout<Component, PaperPageContext<T>> layout,
        @NotNull ContentSource<T> source,
        @NotNull ComponentRenderer<T, PaperPageContext<T>> renderer,
        boolean trimOverflow
    ) {
        super(id, layout, source, renderer, trimOverflow);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull PaperPaginationSession<T> open(
        @NotNull studio.mevera.lotus.Lotus<Component> lotus,
        @NotNull org.bukkit.entity.Player viewer
    ) {
        return (PaperPaginationSession<T>) super.open(lotus, viewer);
    }

    public static <T> @NotNull Builder<T> builder(String id) {
        return new Builder<>(id);
    }

    public static final class Builder<T>
        extends AbstractPagination.Builder<Component, T, PaperPageContext<T>, Pagination<T>> {

        private Builder(String id) {
            super(id);
        }

        @Override
        protected @NotNull Pagination<T> create(
            @NotNull PageLayout<Component, PaperPageContext<T>> layout,
            @NotNull ContentSource<T> source,
            @NotNull ComponentRenderer<T, PaperPageContext<T>> renderer,
            boolean trimOverflow
        ) {
            return new Pagination<>(id, layout, source, renderer, trimOverflow);
        }
    }
}
