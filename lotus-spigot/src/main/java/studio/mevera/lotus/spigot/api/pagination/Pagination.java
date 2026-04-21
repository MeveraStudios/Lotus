package studio.mevera.lotus.spigot.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.pagination.AbstractPagination;
import studio.mevera.lotus.api.pagination.ComponentRenderer;
import studio.mevera.lotus.api.pagination.ContentSource;
import studio.mevera.lotus.api.pagination.PageLayout;
import studio.mevera.lotus.spigot.internal.SpigotPaginationSession;

/**
 * Spigot-facing pagination definition with string titles and Spigot page contexts.
 */
public final class Pagination<T> extends AbstractPagination<String, T, SpigotPageContext<T>> {

    private Pagination(
        @NotNull PageLayout<String, SpigotPageContext<T>> layout,
        @NotNull ContentSource<T> source,
        @NotNull ComponentRenderer<T, SpigotPageContext<T>> renderer,
        boolean trimOverflow
    ) {
        super(layout, source, renderer, trimOverflow);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull SpigotPaginationSession<T> open(@NotNull Lotus<String> lotus, @NotNull Player viewer) {
        return (SpigotPaginationSession<T>) super.open(lotus, viewer);
    }

    public static <T> @NotNull Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T>
        extends AbstractPagination.Builder<String, T, SpigotPageContext<T>, Pagination<T>> {

        private Builder() {
        }

        @Override
        protected @NotNull Pagination<T> create(
            @NotNull PageLayout<String, SpigotPageContext<T>> layout,
            @NotNull ContentSource<T> source,
            @NotNull ComponentRenderer<T, SpigotPageContext<T>> renderer,
            boolean trimOverflow
        ) {
            return new Pagination<>(layout, source, renderer, trimOverflow);
        }
    }
}
