package studio.mevera.lotus.internal.pagination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.menu.InteractiveMenu;
import studio.mevera.lotus.api.pagination.AbstractPagination;
import studio.mevera.lotus.api.pagination.AbstractPageContext;
import studio.mevera.lotus.api.pagination.PaginationSession;

import java.util.List;
import java.util.Objects;

/**
 * Per-player pagination runtime. Captures a snapshot of source items at construction so the page
 * count is stable for the session's lifetime, then synthesises a PageMenu per navigation.
 * <p>
 * Subclass and override {@link #buildPageMenu(int)} to supply a platform-specific menu
 * (e.g. {@code PaperPaginationSession} in lotus-paper uses Adventure Component titles).
 */
public abstract class DefaultPaginationSession<C, T, X extends AbstractPageContext<C, T, ?>>
    implements PaginationSession<C, T, X> {

    protected final Lotus<C> lotus;
    protected final AbstractPagination<C, T, X> definition;
    protected final Player viewer;
    protected List<T> snapshot;
    protected int totalPages;
    protected int itemsPerPage;

    private int currentIndex;
    private boolean closed;

    public DefaultPaginationSession(
        @NotNull AbstractPagination<C, T, X> definition,
        @NotNull Lotus<C> lotus,
        @NotNull Player viewer
    ) {
        this.lotus = Objects.requireNonNull(lotus);
        this.definition = Objects.requireNonNull(definition);
        this.viewer = Objects.requireNonNull(viewer);
        rebuildState();
    }

    @Override public @NotNull AbstractPagination<C, T, ? super X> definition() { return definition; }
    @Override public @NotNull Player viewer() { return viewer; }
    @Override public int currentIndex() { return currentIndex; }
    @Override public int totalPages() { return totalPages; }

    @Override
    public void next() {
        if (currentIndex + 1 >= totalPages) return;
        goTo(currentIndex + 1);
    }

    @Override
    public void previous() {
        if (currentIndex == 0) return;
        goTo(currentIndex - 1);
    }

    @Override
    public void goTo(int pageIndex) {
        Objects.checkIndex(pageIndex, totalPages);
        if (closed) throw new IllegalStateException("session closed");
        this.currentIndex = pageIndex;
        InteractiveMenu<C> menu = buildPageMenu(pageIndex);
        lotus.openMenu(viewer, menu);
    }

    @Override
    public void reload() {
        if (closed) throw new IllegalStateException("session closed");
        rebuildState();
        int reloadedIndex = Math.min(currentIndex, totalPages - 1);
        goTo(reloadedIndex);
    }

    @Override
    public void close() {
        if (closed) return;
        this.closed = true;
        viewer.closeInventory();
    }

    /**
     * Factory method for constructing the page menu for a given index.
     * Override in subclasses to produce platform-specific menus with richer title types.
     */
    protected @NotNull abstract InteractiveMenu<C> buildPageMenu(int pageIndex);

    protected List<T> sliceFor(int pageIndex) {
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, snapshot.size());
        if (start >= snapshot.size()) return List.of();
        return snapshot.subList(start, end);
    }

    protected void rebuildState() {
        this.snapshot = List.copyOf(definition.source().provide(viewer));
        this.itemsPerPage = definition.layout().fillMask().size();
        this.totalPages = Math.max(1, (int) Math.ceil(snapshot.size() / (double) itemsPerPage));
    }

}
