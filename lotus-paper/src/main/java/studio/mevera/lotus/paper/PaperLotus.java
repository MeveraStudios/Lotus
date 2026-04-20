package studio.mevera.lotus.paper;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.LotusBuilder;
import studio.mevera.lotus.paper.internal.opener.PaperViewOpener;
import studio.mevera.lotus.paper.internal.pagination.PaperPaginationSession;

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
    public static @NotNull Lotus create(@NotNull Plugin plugin) {
        return create(plugin, b -> {});
    }

    /**
     * Creates a {@link Lotus} facade pre-configured for Paper. The {@code customizer} receives the
     * {@link LotusBuilder} after Paper defaults ({@link PaperViewOpener}) have been applied, so you
     * may override any setting except the session factory (which is applied post-build).
     */
    public static @NotNull Lotus create(@NotNull Plugin plugin, @NotNull Consumer<LotusBuilder> customizer) {
        LotusBuilder builder = Lotus.builder(plugin).defaultViewOpener(new PaperViewOpener());
        customizer.accept(builder);
        Lotus lotus = builder.build();
        lotus.sessionFactory(PaperPaginationSession::new);
        return lotus;
    }
}
