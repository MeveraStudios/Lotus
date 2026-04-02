package studio.mevera.menus;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.mevera.menus.base.Content;
import studio.mevera.menus.base.Menu;
import studio.mevera.menus.misc.Capacity;
import studio.mevera.menus.misc.DataRegistry;
import studio.mevera.menus.misc.button.Button;
import studio.mevera.menus.misc.button.actions.ButtonClickAction;
import studio.mevera.menus.misc.itembuilder.ItemBuilder;
import studio.mevera.menus.titles.MenuTitle;
import studio.mevera.menus.titles.MenuTitles;

public class TutorialMenu implements Menu {

    @Override
    public @NotNull MenuTitle getTitle(DataRegistry extraData, Player opener) {
        return MenuTitles.createModern("<blue><bold> Welcome " + opener.getName());
    }

    @Override
    public @NotNull Capacity getCapacity(DataRegistry extraData, Player opener) {
        return Capacity.ofRows(3);
    }

    @Override
    public @NotNull Content getContent(DataRegistry extraData, Player opener, Capacity capacity) {
        ItemStack fancyStoneItem = ItemBuilder.modern(MiniMessage.miniMessage(), Material.STONE)
                                      .setDisplay("<green> The Fancy Stone")
                                      .setRichLore(
                                              "<gray>-----------",
                                              "<yellow>Just a fancy stone!",
                                              "<gray>-----------"
                                      )
                                      .build();
        Button fancyStoneButton = Button.clickable(
                fancyStoneItem,
                ButtonClickAction.plain((menuView, clickEvent) -> {
                    //handle click
                    //give the player the fancy stone item
                    opener.getInventory().addItem(fancyStoneItem);
                    opener.sendMessage("You have been given " + fancyStoneItem.getItemMeta().getDisplayName());

                    //cancel the
                    clickEvent.setCancelled(true);

                    //close the player's inventory after the click
                    opener.closeInventory();
                })
        );

        Content content = Content.builder(capacity)
                                  .setButton(0, fancyStoneButton)
                                  .build();
        return content;
    }
}
