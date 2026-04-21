package studio.mevera.lotus.paper.internal.pagination;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.mevera.lotus.Lotus;
import studio.mevera.lotus.api.button.Button;
import studio.mevera.lotus.api.button.ClickAction;
import studio.mevera.lotus.api.content.Content;
import studio.mevera.lotus.api.menu.InteractiveMenu;
import studio.mevera.lotus.api.menu.MenuView;
import studio.mevera.lotus.api.slot.Capacity;
import studio.mevera.lotus.api.slot.Slot;
import studio.mevera.lotus.internal.pagination.DefaultPaginationSession;
import studio.mevera.lotus.paper.api.menu.PaperInteractiveMenu;
import studio.mevera.lotus.paper.api.pagination.PaperPageContext;
import studio.mevera.lotus.paper.api.pagination.Pagination;
import studio.mevera.lotus.paper.api.pagination.PaperPageLayout;

import java.util.Iterator;
import java.util.List;

/**
 * Paper-specific pagination session. Extends {@link DefaultPaginationSession} and overrides
 * {@link #buildPageMenu(int)} to synthesise a {@link PaperPageMenu} — a {@link PaperInteractiveMenu}
 * whose {@code title()} returns an Adventure {@link Component} directly, preserving full fidelity.
 */
public class PaperPaginationSession<T> extends DefaultPaginationSession<Component, T, PaperPageContext<T>> {

    public PaperPaginationSession(
        @NotNull Pagination<T> definition,
        @NotNull Lotus<Component> lotus,
        @NotNull Player viewer
    ) {
        super(definition, lotus, viewer);
    }

    @Override
    public @NotNull Pagination<T> definition() {
        return (Pagination<T>) super.definition();
    }

    @Override
    protected @NotNull InteractiveMenu<Component> buildPageMenu(int pageIndex) {
        return new PaperPageMenu<>(this, pageIndex);
    }

    @Override
    public List<T> sliceFor(int pageIndex) {
        return super.sliceFor(pageIndex);
    }

}
