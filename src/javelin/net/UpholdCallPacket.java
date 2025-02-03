package javelin.net;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import javelin.JaveLib;
import javelin.units.JavelinEntity;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

public class UpholdCallPacket extends Packet {
    private byte[] DATA;
    /** Javelin unit ID. If not Javelin's - discharging won't happen. */
    public int uid;
    public float x;
    public float y;
    public float rotation;
    /** Amount of charges to be discharged. */
    public int charges;
    /** Whether given Javelin unit should uphold the charge.
     * This is automatically set to 'false' if player no longer controls the Javelin unit. */
    public boolean uphold;


    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                  Constructors
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public UpholdCallPacket() {
        this.DATA = NODATA;
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                    Packing
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    @Override
    public void write(Writes WRITE) {
        WRITE.i(uid);
        if (Vars.net.client()) {
            WRITE.f(x);
            WRITE.f(y);
            WRITE.f(rotation);
            WRITE.i(charges);
        }

        WRITE.bool(uphold);
    }

    @Override
    public void read(Reads READ, int LENGTH) {
        this.DATA = READ.b(LENGTH);
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                    Handling
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    @Override
    public int getPriority() {
        return priorityHigh;
    }

    @Override
    public void handled() {
        BAIS.setBytes(this.DATA);
        uid = READ.i();
        if (Vars.net.server()) {
            x = READ.f();
            y = READ.f();
            rotation = READ.f();
            charges = READ.i();
        }

        uphold = READ.bool();
    }

    @Override
    public void handleClient() {
        JaveLib.uphold(uid, uphold);
    }

    @Override
    public void handleServer(NetConnection con) {
        if (Groups.unit.getByID(uid) instanceof JavelinEntity javelin) {
            int charges = javelin.charges; // This is here, because 'releaseAll' resets 'charges' variable.
            if (uphold && !javelin.uphold)
                javelin.release(charges, x, y, rotation);

            javelin.uphold = uphold;
            Call.uphold(con, javelin, uphold, charges);
        }
    }
}