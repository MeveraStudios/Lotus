package studio.mevera.menus;

import studio.mevera.menus.base.Content;
import studio.mevera.menus.base.Menu;
import studio.mevera.menus.misc.Capacity;
import studio.mevera.menus.misc.DataRegistry;
import studio.mevera.menus.misc.button.Button;
import studio.mevera.menus.misc.button.actions.ButtonClickAction;
import studio.mevera.menus.misc.itembuilder.ItemBuilder;
import studio.mevera.menus.titles.MenuTitle;
import studio.mevera.menus.titles.MenuTitles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ExampleMenu2 implements Menu {
    @Override
    public String getName() {
        return "menu2";
    }

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry extraData, Player opener) {
        return MenuTitles.createLegacy("Hii");
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry extraData, Player opener) {
        return Capacity.ofRows(3);
    }

    @Override
    public @NotNull Content getContent(DataRegistry extraData, Player opener, Capacity capacity) {
        Content.Builder b = Content.builder(capacity);
        b.apply(content -> {
            for(ChatColor color : ChatColor.values()) {
                content.addButton(Button.clickable(ItemBuilder.legacy(Material.NAME_TAG).setDisplay(color.name()).build(), ButtonClickAction.plain((menu, event)-> {
                    event.setCancelled(true);
                    opener.closeInventory();
                    opener.sendMessage("Color clicked= " + color + color.name());
                })));
            }
        });
        return b.build();
    }
}
