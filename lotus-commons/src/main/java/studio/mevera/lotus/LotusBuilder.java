package studio.mevera.lotus;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.spi.PaginationSessionFactory;
import studio.mevera.lotus.spi.opener.ViewOpener;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for {@link Lotus}. Required: {@link Plugin}. All other fields default to
 * {@link Lotus.Options#defaults()}.
 * <p>
 * Use {@link #defaultViewOpener(ViewOpener)} to register the platform-specific opener —
 * e.g. {@code PaperViewOpener} from {@code lotus-paper} or {@code SpigotViewOpener} from
 * {@code lotus-spigot}.
 */
public final class LotusBuilder<C> {

    private final Plugin plugin;
    private final Consumer<Lotus<C>> bootstrap;
    private boolean allowBottomInventoryClick = true;
    private boolean dynamicButtonAction = false;
    private boolean debug = false;
    private @NotNull ViewOpener<C> defaultViewOpener;

    private PaginationSessionFactory<C> paginationSessionFactory;

    public LotusBuilder(@NotNull Plugin plugin, @NotNull Consumer<Lotus<C>> bootstrap) {
        this.plugin = Objects.requireNonNull(plugin);
        this.bootstrap = Objects.requireNonNull(bootstrap);
        // Placeholder: replaced at build() if not explicitly set.
        // Platform factories (PaperLotus, SpigotLotus) always set this.
        this.defaultViewOpener = (lotus, view) -> {
            throw new IllegalStateException(
                "No defaultViewOpener configured. Use PaperLotus.create(plugin) or " +
                "SpigotLotus.create(plugin) instead of Lotus.builder() directly.");
        };
    }

    public @NotNull LotusBuilder<C> allowBottomInventoryClick(boolean allow) {
        this.allowBottomInventoryClick = allow;
        return this;
    }

    public @NotNull LotusBuilder<C> dynamicButtonAction(boolean dynamic) {
        this.dynamicButtonAction = dynamic;
        return this;
    }

    public @NotNull LotusBuilder<C> debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public @NotNull LotusBuilder<C> paginationSessionFactory(PaginationSessionFactory<C> factory) {
        this.paginationSessionFactory = Objects.requireNonNull(factory);
        return this;
    }

    /**
     * Sets the fallback {@link ViewOpener} used for all inventory types not explicitly
     * registered via {@link Lotus#registerOpener(org.bukkit.event.inventory.InventoryType, ViewOpener)}.
     * <p>
     * This must be a platform-specific implementation — {@code PaperViewOpener} on Paper or
     * {@code SpigotViewOpener} on Spigot.
     */
    public @NotNull LotusBuilder<C> defaultViewOpener(@NotNull ViewOpener<C> opener) {
        this.defaultViewOpener = Objects.requireNonNull(opener);
        return this;
    }

    public @NotNull Lotus<C> build() {
        if (paginationSessionFactory == null) {
            throw new IllegalStateException(
                "No paginationSessionFactory configured. Use PaperLotus.create(plugin) or " +
                "SpigotLotus.create(plugin) instead of constructing LotusBuilder directly."
            );
        }
        return new Lotus<>(
                plugin,
                new Lotus.Options(allowBottomInventoryClick, dynamicButtonAction, debug),
                defaultViewOpener,
                paginationSessionFactory,
                bootstrap
        );
    }
}
