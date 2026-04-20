package studio.mevera.lotus.spi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.pagination.Pagination;
import studio.mevera.lotus.api.pagination.PaginationSession;

/**
 * Strategy that constructs per-player {@link PaginationSession} instances.
 * <p>
 * Register a custom factory on {@link Lotus} to provide platform-specific sessions
 * (e.g. {@code PaperPaginationSession} for Adventure Component titles on Paper).
 * The default factory produces {@code DefaultPaginationSession} with plain String titles.
 */
@FunctionalInterface
public interface PaginationSessionFactory {

    /**
     * Creates a new {@link PaginationSession} for the given definition and viewer.
     * Implementations must NOT call {@link PaginationSession#goTo(int)} — that is
     * handled by {@link Pagination#open(Lotus, Player)}.
     */
    @NotNull <T> PaginationSession<T> create(
        @NotNull Pagination<T> definition,
        @NotNull Lotus lotus,
        @NotNull Player viewer
    );
}
