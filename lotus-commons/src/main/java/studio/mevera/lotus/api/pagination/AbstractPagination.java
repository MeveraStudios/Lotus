package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;

import java.util.Objects;

/**
 * Immutable, viewer-agnostic definition of a paginated menu. Composes a {@link PageLayout},
 * {@link ContentSource} and {@link ComponentRenderer}; instances are safe to share and reuse
 * across players and threads (definitions only — runtime state lives on the {@link PaginationSession}).
 */
public abstract class AbstractPagination<C, T, X extends AbstractPageContext<C, T, ?>> {

    private final String id;
    private final PageLayout<C, X> layout;
    private final ContentSource<T> source;
    private final ComponentRenderer<T, X> renderer;
    private final boolean trimOverflow;

    protected AbstractPagination(
        String id,
        PageLayout<C, X> layout,
        ContentSource<T> source,
        ComponentRenderer<T, X> renderer,
        boolean trimOverflow
    ) {
        this.id = id;
        this.layout = Objects.requireNonNull(layout);
        this.source = Objects.requireNonNull(source);
        this.renderer = Objects.requireNonNull(renderer);
        this.trimOverflow = trimOverflow;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull PageLayout<C, X> layout() {
        return layout;
    }

    public @NotNull ContentSource<T> source() {
        return source;
    }

    public @NotNull ComponentRenderer<T, X> renderer() {
        return renderer;
    }

    public boolean trimOverflow() {
        return trimOverflow;
    }

    /**
     * Resolves source items for this viewer, creates a per-player session via the
     * {@link studio.mevera.lotus.spi.PaginationSessionFactory} registered on {@link Lotus},
     * and opens page 0.
     */
    public @NotNull PaginationSession<C, T, ? extends X> open(@NotNull Lotus<C> lotus, @NotNull Player viewer) {
        PaginationSession<C, T, ? extends X> session = lotus.sessionFactory().create(this, lotus, viewer);
        session.goTo(0);
        return session;
    }

    public abstract static class Builder<
        C,
        T,
        X extends AbstractPageContext<C, T, ?>,
        P extends AbstractPagination<C, T, X>
    > {
        protected final String id;
        private PageLayout<C, X> layout;
        private ContentSource<T> source;
        private ComponentRenderer<T, X> renderer;
        private boolean trimOverflow = true;

        protected Builder(String id) {
            this.id = id;
        }

        public @NotNull Builder<C, T, X, P> layout(@NotNull PageLayout<C, X> layout) {
            this.layout = layout;
            return this;
        }

        public @NotNull Builder<C, T, X, P> source(@NotNull ContentSource<T> source) {
            this.source = source;
            return this;
        }

        public @NotNull Builder<C, T, X, P> renderer(@NotNull ComponentRenderer<T, X> renderer) {
            this.renderer = renderer;
            return this;
        }

        public @NotNull Builder<C, T, X, P> trimOverflow(boolean trim) {
            this.trimOverflow = trim;
            return this;
        }

        public @NotNull P build() {
            if (layout == null) throw new IllegalStateException("layout missing");
            if (source == null) throw new IllegalStateException("source missing");
            if (renderer == null) throw new IllegalStateException("renderer missing");
            return create(layout, source, renderer, trimOverflow);
        }

        protected abstract @NotNull P create(
            @NotNull PageLayout<C, X> layout,
            @NotNull ContentSource<T> source,
            @NotNull ComponentRenderer<T, X> renderer,
            boolean trimOverflow
        );
    }
}
