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
 * Paper-specific static factory for {@link Lotus}. Returns a {@link Lotus} instance pre-configured
 * with:
 * <ul>
 *   <li>{@link PaperViewOpener} as the default {@link studio.mevera.lotus.spi.opener.ViewOpener} —
 *       uses Paper's {@code Bukkit.createInventory(holder, size, Component)} overload for
 *       full Adventure fidelity.</li>
 *   <li>{@link PaperPaginationSession}{@code ::new} as the
 *       {@link studio.mevera.lotus.spi.PaginationSessionFactory} — produces
 *       {@link studio.mevera.lotus.paper.api.menu.PaperInteractiveMenu} pages whose titles
 *       return Adventure {@link net.kyori.adventure.text.Component} directly.</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * Lotus lotus = PaperLotus.create(this); // 'this' is your JavaPlugin
 *
 * // Or with builder customization:
 * Lotus lotus = PaperLotus.create(this, b -> b.debug(true).allowBottomInventoryClick(false));
 * }</pre>
 */
public final class PaperLotus {

    private PaperLotus() {}

    /**
     * Creates a {@link Lotus} facade pre-configured for Paper with default options.
     */
    public static @NotNull Lotus<Component> create(@NotNull Plugin plugin) {
        return create(plugin, b -> {});
    }


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
     * Creates a {@link Lotus} facade pre-configured for Paper. The {@code customizer} receives the
     * {@link LotusBuilder} after Paper defaults ({@link PaperViewOpener}) have been applied, so you
     * may override any setting except the session factory (which is applied post-build).
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

    public static void syncOpenPagination(Lotus<Component> lotus, String paginationId) {
        for(var view : lotus.openViews()) {
            syncOpenPagination(lotus, paginationId, view.viewer());
        }
    }
}
