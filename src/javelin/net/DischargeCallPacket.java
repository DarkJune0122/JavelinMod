package javelin.net;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import javelin.units.JavelinEntity;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

public class DischargeCallPacket extends Packet {
    private byte[] DATA;
    /** Javelin unit ID. If not Javelin's - discharging won't happen. */
    public int uid;
    public float x;
    public float y;
    public float rotation;
    /** Amount of charges to be discharged. */
    public int charges;


    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                  Constructors
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public DischargeCallPacket() {
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
        }

        WRITE.i(charges);
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
        }

        charges = READ.i();
    }

    @Override
    public void handleClient() {
        if (Groups.unit.getByID(uid) instanceof JavelinEntity javelin) {
            javelin.release(charges);
        }
    }

    @Override
    public void handleServer(NetConnection con) {
        if (Groups.unit.getByID(uid) instanceof JavelinEntity javelin) {
            // Here, we can limit amount of charges to the server's actual value... but maybe next time - have fun.
            javelin.release(charges, x, y, rotation);
            Call.discharge(con, javelin, charges);
        }
    }
}