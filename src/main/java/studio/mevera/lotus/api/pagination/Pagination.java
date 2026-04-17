package studio.mevera.lotus.api.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.internal.pagination.DefaultPaginationSession;

import java.util.Objects;

/**
 * Immutable, viewer-agnostic definition of a paginated menu. Composes a {@link PageLayout},
 * {@link ContentSource} and {@link ComponentRenderer}; instances are safe to share and reuse
 * across players and threads (definitions only — runtime state lives on the {@link PaginationSession}).
 */
public final class Pagination<T> {

    private final PageLayout layout;
    private final ContentSource<T> source;
    private final ComponentRenderer<T> renderer;
    private final boolean trimOverflow;

    private Pagination(PageLayout layout, ContentSource<T> source, ComponentRenderer<T> renderer, boolean trimOverflow) {
        this.layout = Objects.requireNonNull(layout);
        this.source = Objects.requireNonNull(source);
        this.renderer = Objects.requireNonNull(renderer);
        this.trimOverflow = trimOverflow;
    }

    public @NotNull PageLayout layout() {
        return layout;
    }

    public @NotNull ContentSource<T> source() {
        return source;
    }

    public @NotNull ComponentRenderer<T> renderer() {
        return renderer;
    }

    public boolean trimOverflow() {
        return trimOverflow;
    }

    /**
     * Resolves source items for this viewer and returns a new per-player session positioned at page 0.
     * The session must be opened (or moved with {@code goTo}) explicitly.
     */
    public @NotNull PaginationSession<T> open(@NotNull Lotus lotus, @NotNull Player viewer) {
        return new DefaultPaginationSession<>(lotus, this, viewer);
    }

    public static <T> @NotNull Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private PageLayout layout;
        private ContentSource<T> source;
        private ComponentRenderer<T> renderer;
        private boolean trimOverflow = true;

        private Builder() {
        }

        public @NotNull Builder<T> layout(@NotNull PageLayout layout) {
            this.layout = layout;
            return this;
        }

        public @NotNull Builder<T> source(@NotNull ContentSource<T> source) {
            this.source = source;
            return this;
        }

        public @NotNull Builder<T> renderer(@NotNull ComponentRenderer<T> renderer) {
            this.renderer = renderer;
            return this;
        }

        public @NotNull Builder<T> trimOverflow(boolean trim) {
            this.trimOverflow = trim;
            return this;
        }

        public @NotNull Pagination<T> build() {
            if (layout == null) throw new IllegalStateException("layout missing");
            if (source == null) throw new IllegalStateException("source missing");
            if (renderer == null) throw new IllegalStateException("renderer missing");
            return new Pagination<>(layout, source, renderer, trimOverflow);
        }
    }
}
