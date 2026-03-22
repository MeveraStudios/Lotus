package studio.mevera.menus;

import org.bukkit.plugin.Plugin;
import studio.mevera.menus.base.serialization.MenuSerializer;
import studio.mevera.menus.base.serialization.SerializedMenuIO;

public class LotusBuilder {

    private SerializedMenuIO<?> menuIO;
    private MenuSerializer menuSerializer;
    private boolean allowOutsideClick = true, dynamicButtonAction = false;
    private long updateTicks = 15L;

    private final Plugin plugin;
    LotusBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    public LotusBuilder setMenuIO(SerializedMenuIO<?> menuIO) {
        this.menuIO = menuIO;
        return this;
    }

    public LotusBuilder setMenuSerializer(MenuSerializer menuSerializer) {
        this.menuSerializer = menuSerializer;
        return this;
    }

    public LotusBuilder setAllowOutsideClick(boolean allowOutsideClick) {
        this.allowOutsideClick = allowOutsideClick;
        return this;
    }

    public LotusBuilder setUpdateTicks(long updateTicks) {
        this.updateTicks = updateTicks;
        return this;
    }

    public LotusBuilder setDynamicButtonAction(boolean dynamicButtonAction) {
        this.dynamicButtonAction = dynamicButtonAction;
        return this;
    }

    public Lotus build() {
        return new Lotus(
                plugin,
                Lotus.Options.of(menuIO, menuSerializer, allowOutsideClick, dynamicButtonAction, updateTicks)
        );
    }

}
