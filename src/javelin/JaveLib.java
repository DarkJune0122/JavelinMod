package javelin;

import arc.ApplicationListener;
import arc.Core;
import arc.KeyBinds;
import arc.util.Time;
import javelin.net.Call;
import javelin.units.JavelinEntity;
import mindustry.Vars;
import mindustry.gen.Groups;

public class JaveLib {
    public static final float maxDoubleClickDelay = 60.0F * 0.4F;
    /** Discharges continuously, whenever this variable is 'true'. */
    public static boolean isDischarging;

    // Discharge handling:
    protected static DischargeHandler handler;

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                Public Methods
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public static void init() {
        if (handler == null) {
            handler = new DischargeHandler();
            Core.app.addListener(handler);
        }

        // TODO: Make mod compatible with other mods by only extending bindings - not fully replacing them.
        // Modifies settings, so they contain new keybind.
        KeyBinds.KeyBind[] original = mindustry.input.Binding.values();
        KeyBinds.KeyBind[] additional = Binding.values();
        KeyBinds.KeyBind[] combined = new KeyBinds.KeyBind[original.length + additional.length];
        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(additional, 0, combined, original.length, additional.length);
        Core.keybinds.setDefaults(combined);
    }

    /** Sets whether ship should uphold charge after the 'split second' discharge. */
    public static void uphold(int uid, boolean state) {
        if (Groups.unit.getByID(uid) instanceof JavelinEntity entity) {
            entity.uphold = state;
        }
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //               Input Processor
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public static class DischargeHandler implements ApplicationListener {
        public float lastTime;
        public JavelinEntity lastJavelin;

        //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
        //
        //                 Public Methods
        //
        //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
        @Override
        public void update() {
            // Key tap handling.
            if (Core.input.keyTap(Core.keybinds.get(Binding.discharge).key)) {
                JaveLib.isDischarging = true;
            } else if (Core.input.keyTap(Core.keybinds.get(Binding.discharge_double_click).key)) {
                float delta = Time.globalTime - lastTime;
                if (delta <= maxDoubleClickDelay) {
                    uphold(true);
                }

                lastTime = Time.globalTime;
            }

            // Key release handling.
            if (Core.input.keyRelease(Core.keybinds.get(Binding.discharge).key)) {
                JaveLib.isDischarging = false;
            } else if (Core.input.keyRelease(Core.keybinds.get(Binding.discharge_double_click).key)) {
                uphold(false);
            }

            // Resets 'uphold' status on previously controlled javelin.
            if (Vars.player == null) return;
            if (Vars.player.unit() instanceof JavelinEntity entity) {
                if (lastJavelin != entity) {
                    if (lastJavelin != null)
                        lastJavelin.uphold = false;
                    lastJavelin = entity;
                }
            } else if (lastJavelin != null) {
                lastJavelin.uphold = false;
            }
        }

        //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
        //
        //                    Helpers
        //
        //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
        private static void uphold(boolean state) {
            if (Vars.player != null && Vars.player.unit() instanceof JavelinEntity javelin) {
                Call.uphold(javelin, state);
            }
        }
    }
}
