package javelin;

import javelin.content.UnitTypes;
import javelin.net.Call;
import mindustry.mod.Mod;

public class JavelinMod extends Mod
{
    public static String name(String name) {
        return "javelin-unit-mod-" + name;
    }

    @Override
    public void loadContent(){
        // Network initialization.
        Call.registerPackets();

        // Loads entities and blocks.
        // Modifies original blocks, research tree, etc.
        UnitTypes.load();
        JaveLib.init();
    }
}