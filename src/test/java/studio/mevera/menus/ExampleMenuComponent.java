package studio.mevera.menus;

import studio.mevera.menus.base.pagination.PageComponent;
import studio.mevera.menus.base.pagination.PageView;
import studio.mevera.menus.misc.itembuilder.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExampleMenuComponent implements PageComponent {

    private final String name;
    public ExampleMenuComponent(String name) {
        this.name = name;
    }
    @Override
    public ItemStack toItem() {
        return ItemBuilder.modern(Material.GRASS)
                .setDisplay(Component.text(name, NamedTextColor.GRAY)).build();
    }
 
    @Override
    public void onClick(PageView pageView, InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        player.sendMessage("Clicking on component '" + name + "'");
    }
}