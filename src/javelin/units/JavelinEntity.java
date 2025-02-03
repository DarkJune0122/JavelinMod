package javelin.units;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import javelin.JaveLib;
import javelin.content.UnitTypes;
import javelin.net.Call;
import mindustry.Vars;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Drawf;
import mindustry.logic.GlobalVars;

public class JavelinEntity extends UnitEntity {
    protected transient float effectRotation = Mathf.random();
    protected transient float radius;
    protected transient float dischargeTime;
    protected transient float lightingTime;
    /** Whether this entity should hold the charge in capacitors after slowing down. (Updated over network: 'Call.uphold(args)') */
    public transient boolean uphold; // No reason to serialize - state will be lost upon map loading anyway.
    public int charges;

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                (De)serialization
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    @Override
    public int classId() {
        return UnitTypes.javelinClassID;
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        this.charges = read.i();
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        write.i(charges);
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                 Public Methods
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    @Override
    public void update() {
        super.update();
        JavelinType type = (JavelinType) super.type;

        // Makes sure only this specific player can discharge lightnings.
        if (charges > 0 && Vars.player.unit() == this) {
            if (JaveLib.isDischarging) {
                Call.discharge(this, charges);
            }
        }

        float velocity = vel.len();
        if (velocity > type.chargingVelocity) {
            velocity -= type.chargingVelocity;
            lightingTime = Math.min(type.chargeLimit * 60.0F, lightingTime + Time.delta * type.chargeSpeed * velocity);
            while (lightingTime >= 60.0F) {
                lightingTime -= 60.0F;
                if (charges < type.chargeLimit)
                    charges++;
            }
        } else if (!uphold) {
            if (charges > 0) {
                dischargeTime += Time.delta;
                if (dischargeTime > type.dischargeDelay) {
                    dischargeTime -= type.dischargeDelay;
                    release(1); // Should by relatively in sync both on client and a host side.
                }

                if (charges == 0) dischargeTime = 0;
            }
        }

        // Updates effect params
        radius -= (radius - charges) * Time.delta * 0.166667f;
        effectRotation += Time.delta * 2;
        if (effectRotation > 360)
            effectRotation -= 360;
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                Charge handling
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public void release(int amount) {
        release(amount, x, y, rotation);
    }
    public void release(int amount, float x, float y, float rotation) {
        JavelinType type = (JavelinType) super.type;
        type.dischargeSound.play(0.08f * amount);
        for (int i = 0; i < amount; i++) {
            type.dischargeBullet.create(this, team, x, y,rotation + GlobalVars.rand.random(-type.lightingCone, type.lightingCone));
        }

        charges = Math.max(0, charges - amount);
    }

    public void releaseAll() {
        release(charges, x, y, rotation);
    }
    public void releaseAll(float x, float y, float rotation) {
        if (charges <= 0) return;

        JavelinType type = (JavelinType) super.type;
        type.dischargeSound.play(0.08f * charges);
        for (int i = 0; i < charges; i++) {
            type.dischargeBullet.create(this, team, x, y,rotation + GlobalVars.rand.random(-type.lightingCone, type.lightingCone));
        }

        charges = 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (charges > 0) {
            releaseAll();
        }
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                    Drawing
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    @Override
    public void draw() {
        super.draw();
        if (charges <= 0) return;
        JavelinType type = (JavelinType) super.type;

        // Capacitor charge rendering.
        Drawf.square(x, y, (hitSize + 4.0F) * (radius / type.chargeLimit), effectRotation, charges < type.chargeLimit ? type.capacitorChargingColor : type.capacitorChargedColor);

        // Frontal (decorative) shield rendering.
        Draw.color(type.shieldColor);
        float chargePercentage = (float) charges / type.chargeLimit;
        Draw.alpha(chargePercentage * type.maxShieldOpacity);
        Draw.blend(Blending.additive);
        if (type.shieldShake != 0) {
            float normalizedShake = type.shieldShake * chargePercentage;
            Draw.rect(type.frontShield, x + GlobalVars.rand.random(-normalizedShake, normalizedShake), y + GlobalVars.rand.random(-normalizedShake, normalizedShake), rotation - 90);
        }
        // Resets drawing data.
        Draw.blend();
        Draw.color();
    }
}
