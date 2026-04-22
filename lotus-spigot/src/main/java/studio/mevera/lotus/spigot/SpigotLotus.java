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
 * Spigot 1.8.8 static factory for {@link Lotus}. Returns a {@link Lotus} instance pre-configured
 * with {@link SpigotViewOpener} as the default {@link studio.mevera.lotus.spi.opener.ViewOpener}.
 * <p>
 * The default {@link studio.mevera.lotus.spi.PaginationSessionFactory}
 * ({@code SpigotPaginationSession::new}) already produces {@code String}-titled page menus —
 * no override is needed on Spigot.
 * <p>
 * Usage:
 * <pre>{@code
 * Lotus lotus = SpigotLotus.create(this); // 'this' is your JavaPlugin
 * }</pre>
 */
public final class SpigotLotus {

    private SpigotLotus() {}

    /**
     * Creates a {@link Lotus} facade pre-configured for Spigot 1.8.8 with default options.
     */
    public static @NotNull Lotus<String> create(@NotNull Plugin plugin) {
        return create(plugin, b -> {});
    }

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
     * Creates a {@link Lotus} facade pre-configured for Spigot 1.8.8. The {@code customizer}
     * receives the {@link LotusBuilder} after Spigot defaults have been applied.
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

    public static void syncOpenPagination(Lotus<String> lotus, String paginationId) {
        for(var view : lotus.openViews()) {
            syncOpenPagination(lotus, paginationId, view.viewer());
        }
    }
}
