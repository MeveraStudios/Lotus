package studio.mevera.lotus.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.LotusBuilder;
import studio.mevera.lotus.api.pagination.AbstractPagination;
import studio.mevera.lotus.api.pagination.AbstractPageContext;
import studio.mevera.lotus.api.pagination.PaginationSession;
import studio.mevera.lotus.paper.internal.opener.PaperViewOpener;
import studio.mevera.lotus.paper.api.pagination.Pagination;
import studio.mevera.lotus.paper.internal.PaperLotusListener;
import studio.mevera.lotus.paper.internal.pagination.PaperPageMenu;
import studio.mevera.lotus.paper.internal.pagination.PaperPaginationSession;
import studio.mevera.lotus.spi.PaginationSessionFactory;

import java.util.function.Consumer;

/**
 * Entry point for creating {@link Lotus} instances configured for the Paper
 * platform.
 * <p>
 * The factory applies Paper-specific defaults, including a
 * {@link PaperViewOpener} for Adventure {@link Component}-based inventory
 * titles and a {@link PaperPaginationSessionFactory} for Paper pagination
 * sessions.
 * <p>
 * Usage:
 * <pre>{@code
 * Lotus<Component> lotus = PaperLotus.create(this);
 *
 * Lotus<Component> customized = PaperLotus.create(
 *     this,
 *     builder -> builder.debug(true).allowBottomInventoryClick(false)
 * );
 * }</pre>
 */
public final class PaperLotus {

    private PaperLotus() {}

    /**
     * Creates a {@link Lotus} instance with the default Paper configuration.
     *
     * @param plugin the owning plugin used for listener registration
     * @return a Paper-configured Lotus facade
     */
    public static @NotNull Lotus<Component> create(@NotNull Plugin plugin) {
        return create(plugin, b -> {});
    }


    /**
     * {@link PaginationSessionFactory} implementation that creates
     * {@link PaperPaginationSession} instances for Paper pagination definitions.
     */
    public static final class PaperPaginationSessionFactory implements PaginationSessionFactory<Component> {

        @Override
        @SuppressWarnings("unchecked")
        public <T, X extends AbstractPageContext<Component, T, ?>> @NotNull PaginationSession<Component, T, ? extends X> create(
            @NotNull AbstractPagination<Component, T, X> definition,
            @NotNull Lotus<Component> lotus,
            @NotNull Player viewer
        ) {
            return (PaginationSession<Component, T, ? extends X>)
                new PaperPaginationSession<>((Pagination<T>) definition, lotus, viewer);
        }
    }

    /**
     * Creates a {@link Lotus} instance with Paper defaults applied and then
     * exposes the builder for additional customization.
     * <p>
     * The {@code customizer} runs after Paper defaults have been registered, so
     * callers can override builder settings before the facade is built.
     *
     * @param plugin the owning plugin used for listener registration
     * @param customizer callback used to customize the prepared builder
     * @return a configured Lotus facade for Paper
     */
    public static @NotNull Lotus<Component> create(
        @NotNull Plugin plugin,
        @NotNull Consumer<LotusBuilder<Component>> customizer
    ) {
        LotusBuilder<Component> builder = new LotusBuilder<Component>(
            plugin,
            lotus -> plugin.getServer().getPluginManager().registerEvents(new PaperLotusListener<>(lotus), plugin)
        )
            .defaultViewOpener(new PaperViewOpener())
            .paginationSessionFactory(new PaperPaginationSessionFactory());
        customizer.accept(builder);
        return builder.build();
    }

    /**
     * Reloads an open Paper pagination view for the given player when the
     * currently displayed pagination definition matches {@code paginationId}.
     *
     * @param lotus the Lotus instance managing open views
     * @param paginationId the pagination definition identifier to refresh
     * @param player the player whose current pagination view should be checked
     */
    public static void syncOpenPagination(Lotus<Component> lotus, String paginationId, Player player) {
        var view = lotus.resolveView(player);
        if(view == null) {
            return;
        }
        if(!(view.menu() instanceof PaperPageMenu<?> paperPageMenu)) {
            return;
        }

        PaperPaginationSession<?> paginationSession = paperPageMenu.session();
        if (paginationSession.definition().getId().equals(paginationId)) {
            paginationSession.reload();
        }
    }

    /**
     * Reloads all open Paper pagination views whose backing pagination
     * definition matches {@code paginationId}.
     *
     * @param lotus the Lotus instance managing open views
     * @param paginationId the pagination definition identifier to refresh
     */
    public static void syncOpenPagination(Lotus<Component> lotus, String paginationId) {
        for(var view : lotus.openViews()) {
            syncOpenPagination(lotus, paginationId, view.viewer());
        }
    }
}
