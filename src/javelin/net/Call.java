package javelin.net;

import javelin.units.JavelinEntity;
import mindustry.Vars;
import mindustry.net.Net;
import mindustry.net.NetConnection;

public class Call {
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                Initialization
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public static void registerPackets() {
        Net.registerPacket(DischargeCallPacket::new);
        Net.registerPacket(UpholdCallPacket::new);
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                  Discharging
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    /** [server and client] Sends package about discharge being made. */
    public static void discharge(JavelinEntity javelin, int charges) {
        float x = javelin.x;
        float y = javelin.y;
        float rotation = javelin.rotation;
        javelin.release(charges, x, y, rotation);

        if (Vars.net.active()) {
            DischargeCallPacket packet = new DischargeCallPacket();
            packet.uid = javelin.id;
            packet.x = x;
            packet.y = y;
            packet.rotation = rotation;
            packet.charges = charges;

            Vars.net.send(packet, false);
        }
    }

    /** [server only] Sends package about discharge being made. */
    public static void discharge(NetConnection avoid, JavelinEntity javelin, int charges) {
        if (Vars.net.server()) {
            DischargeCallPacket packet = new DischargeCallPacket();
            packet.uid = javelin.id;
            packet.x = javelin.x;
            packet.y = javelin.y;
            packet.rotation = javelin.rotation;
            packet.charges = charges;

            Vars.net.sendExcept(avoid, packet, false);
        }
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                Charge holding
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    /** [server and client] Notifies whether a specific javelin is holding up on charge. */
    public static void uphold(JavelinEntity javelin, boolean state) {
        int charges = javelin.charges; // This is here, because 'releaseAll' resets 'charges' variable.
        if (state && !javelin.uphold) javelin.releaseAll();
        javelin.uphold = state;

        if (Vars.net.active()) {
            UpholdCallPacket packet = new UpholdCallPacket();
            packet.uid = javelin.id;
            packet.x = javelin.x;
            packet.y = javelin.y;
            packet.rotation = javelin.rotation;
            packet.charges = charges;
            packet.uphold = state;

            Vars.net.send(packet, false);
        }
    }

    /** [server only] Notifies whether a specific javelin is holding up on charge. */
    public static void uphold(NetConnection avoid, JavelinEntity javelin, boolean state, int charges) {
        if (Vars.net.server()) {
            UpholdCallPacket packet = new UpholdCallPacket();
            packet.uid = javelin.id;
            packet.x = javelin.x;
            packet.y = javelin.y;
            packet.rotation = javelin.rotation;
            packet.charges = charges;
            packet.uphold = state;

            Vars.net.sendExcept(avoid, packet, false);
        }
    }
}
