package studio.mevera.lotus.spi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.pagination.AbstractPagination;
import studio.mevera.lotus.api.pagination.AbstractPageContext;
import studio.mevera.lotus.api.pagination.PaginationSession;

/**
 * Strategy that constructs per-player {@link PaginationSession} instances.
 * <p>
 * Register a custom factory on {@link Lotus} to provide platform-specific sessions
 * (e.g. {@code PaperPaginationSession} for Adventure Component titles on Paper).
 * The default factory produces {@code DefaultPaginationSession} with plain String titles.
 */
public interface PaginationSessionFactory<C> {

    /**
     * Creates a new {@link PaginationSession} for the given definition and viewer.
     * Implementations must NOT call {@link PaginationSession#goTo(int)} — that is
     * handled by {@link AbstractPagination#open(Lotus, Player)}.
     */
    @NotNull <T, X extends AbstractPageContext<C, T, ?>> PaginationSession<C, T, ? extends X> create(
        @NotNull AbstractPagination<C, T, X> definition,
        @NotNull Lotus<C> lotus,
        @NotNull Player viewer
    );
}
