package studio.mevera.lotus.spigot;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.LotusBuilder;
import studio.mevera.lotus.api.pagination.AbstractPagination;
import studio.mevera.lotus.api.pagination.AbstractPageContext;
import studio.mevera.lotus.api.pagination.PaginationSession;
import studio.mevera.lotus.spi.PaginationSessionFactory;
import studio.mevera.lotus.spigot.api.pagination.Pagination;
import studio.mevera.lotus.spigot.internal.SpigotLotusListener;
import studio.mevera.lotus.spigot.internal.pagination.SpigotPageMenu;
import studio.mevera.lotus.spigot.internal.pagination.SpigotPaginationSession;
import studio.mevera.lotus.spigot.internal.opener.SpigotViewOpener;
import java.util.function.Consumer;

/**
 * Entry point for creating {@link Lotus} instances configured for the Spigot
 * platform.
 * <p>
 * The factory applies Spigot-specific defaults, including a
 * {@link SpigotViewOpener} for string-based inventory titles and a
 * {@link SpigotPaginationSessionFactory} for Spigot pagination sessions.
 * <p>
 * Usage:
 * <pre>{@code
 * Lotus<String> lotus = SpigotLotus.create(this);
 * }</pre>
 */
public final class SpigotLotus {

    private SpigotLotus() {}

    /**
     * Creates a {@link Lotus} instance with the default Spigot configuration.
     *
     * @param plugin the owning plugin used for listener registration
     * @return a Spigot-configured Lotus facade
     */
    public static @NotNull Lotus<String> create(@NotNull Plugin plugin) {
        return create(plugin, b -> {});
    }

    /**
     * {@link PaginationSessionFactory} implementation that creates
     * {@link SpigotPaginationSession} instances for Spigot pagination
     * definitions.
     */
    public static class SpigotPaginationSessionFactory implements PaginationSessionFactory<String> {

        @Override
        @SuppressWarnings("unchecked")
        public @NotNull <T, X extends AbstractPageContext<String, T, ?>> PaginationSession<String, T, ? extends X> create(
            @NotNull AbstractPagination<String, T, X> definition,
            @NotNull Lotus<String> lotus,
            @NotNull Player viewer
        ) {
            return (PaginationSession<String, T, ? extends X>)
                new SpigotPaginationSession<>((Pagination<T>) definition, lotus, viewer);
        }
    }

    /**
     * Creates a {@link Lotus} instance with Spigot defaults applied and then
     * exposes the builder for additional customization.
     *
     * @param plugin the owning plugin used for listener registration
     * @param customizer callback used to customize the prepared builder
     * @return a configured Lotus facade for Spigot
     */
    public static @NotNull Lotus<String> create(
        @NotNull Plugin plugin,
        @NotNull Consumer<LotusBuilder<String>> customizer
    ) {
        LotusBuilder<String> builder = new LotusBuilder<String>(
            plugin,
            lotus -> plugin.getServer().getPluginManager().registerEvents(new SpigotLotusListener<>(lotus), plugin)
        )
            .defaultViewOpener(new SpigotViewOpener())
            .paginationSessionFactory(new SpigotPaginationSessionFactory());
        customizer.accept(builder);
        return builder.build();
    }

    /**
     * Reloads an open Spigot pagination view for the given player when the
     * currently displayed pagination definition matches {@code paginationId}.
     *
     * @param lotus the Lotus instance managing open views
     * @param paginationId the pagination definition identifier to refresh
     * @param player the player whose current pagination view should be checked
     */
    public static void syncOpenPagination(Lotus<String> lotus, String paginationId, Player player) {
        var view = lotus.resolveView(player);
        if(view == null) {
            return;
        }
        if(!(view.menu() instanceof SpigotPageMenu<?> spigotPageMenu)) {
            return;
        }

        SpigotPaginationSession<?> paginationSession = spigotPageMenu.session();
        if (paginationSession.definition().getId().equals(paginationId)) {
            paginationSession.reload();
        }
    }

    /**
     * Reloads all open Spigot pagination views whose backing pagination
     * definition matches {@code paginationId}.
     *
     * @param lotus the Lotus instance managing open views
     * @param paginationId the pagination definition identifier to refresh
     */
    public static void syncOpenPagination(Lotus<String> lotus, String paginationId) {
        for(var view : lotus.openViews()) {
            syncOpenPagination(lotus, paginationId, view.viewer());
        }
    }
}
