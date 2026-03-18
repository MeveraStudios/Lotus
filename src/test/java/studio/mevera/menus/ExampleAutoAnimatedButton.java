package studio.mevera.menus;

import studio.mevera.menus.base.MenuView;
import studio.mevera.menus.base.animation.AnimatedButton;
import studio.mevera.menus.base.animation.AnimationTaskData;
import studio.mevera.menus.misc.DataRegistry;
import studio.mevera.menus.misc.Slot;
import studio.mevera.menus.misc.button.actions.ButtonClickAction;
import studio.mevera.menus.misc.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;

public final class ExampleAutoAnimatedButton extends AnimatedButton {

    public ExampleAutoAnimatedButton() {
        super(
                AnimationTaskData.builder()
                        .delay(0L)
                        .ticks(1L)
                        .async(true)
                        .build(),
                
                ItemBuilder.legacy(Material.EMERALD)
                        .setDisplay(frames[0])
                        .setLore(Collections.singletonList(loreFrames[0]))
                        .build()
        );
    }

    public ExampleAutoAnimatedButton(@Nullable ItemStack item, @Nullable ButtonClickAction action, DataRegistry data) {
        super(AnimationTaskData.defaultData(), item, action, data);
    }

    private final static String[] frames = {
            "§a❈ §b§lMAGIC §a❈",
            "§b❈ §a§lMAGIC §b❈",
            "§e❈ §6§lMAGIC §e❈",
            "§6❈ §e§lMAGIC §6❈"
    };

    private final static String[] loreFrames = {
            "§7✧ Click to activate",
            "§7✧ Click to enchant",
            "§7✧ Click to cast",
            "§7✧ Click to conjure"
    };
    
    private int frame = 0;


    public static ItemStack newItem(String[] frames, int frame) {
        return ItemBuilder.legacy(Material.EMERALD)
                .setDisplay(frames[frame])
                .setLore(Collections.singletonList(loreFrames[frame])).build();
    }

    @Override
    public void animate(Slot slot, @NotNull MenuView<?> view) {
        frame = (frame + 1) % frames.length;
        this.setItem(newItem(frames, frame));
        
        view.replaceButton(slot, this);
        System.out.println("EXAMPLE animated BUTTON FOR PLAYER " + view.getPlayer().map(Player::getName).orElse("N/A") + ", Running !");
    }
    
}
