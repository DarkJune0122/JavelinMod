package javelin.units;

import arc.Core;
import arc.audio.Sound;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Position;
import arc.util.Log;
import javelin.ui.StatValues;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.TimedKillc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;

public class JavelinType extends UnitType
{
    /** Charging speed per second per each velocity point - discharge delay. (max velocity equals to 5.7 at 14.6F speed) */
    public float chargeSpeed = 2.4F;
    /** Limit to the charge amount. */
    public int chargeLimit = 8;
    /** Max spread of the lightnings in degrees. */
    public float lightingCone = 12F;
    /** Minimal velocity required to start charging up the capacitors. */
    public float chargingVelocity = 1.6F;
    /** Delay between discharges when velocity is too low for charging. */
    public float dischargeDelay = 4.2F;
    /** Sounds that is played on each discharging. */
    public Sound dischargeSound;
    /** Bullet that is being produced on discharge. */
    public BulletType dischargeBullet;
    /** Texture for the frontal shield, visible on high speeds. */
    public TextureRegion frontShield;

    // Colouring:
    public Color capacitorChargingColor = Pal.lancerLaser;
    public Color capacitorChargedColor = Pal.redLight;
    public Color shieldColor = Pal.lancerLaser;
    /** Do note that shield is being rendered additively. */
    public float maxShieldOpacity = 0.25F;
    public float shieldShake = 0.2f;

    // Stat visualization:
    protected Stat dischargeStat = new Stat("discharge");

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                 Constructors
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public JavelinType(String name) {
        super(name);
        constructor = JavelinEntity::new;
    }

    @Override
    public void load() {
        super.load();
        frontShield = Core.atlas.find(name + "-shield");
    }

    @Override
    public void setStats() {
        super.setStats();

        // Adds discharge description:
        if (dischargeBullet != null) {
            stats.add(dischargeStat, StatValues.charge(dischargeBullet));
        }
    }

    @Override
    public Unit create(Team team) {
        Unit unit = constructor.get();
        Log.info(constructor);
        Log.info(unit);

        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if(unit instanceof TimedKillc u){
            u.lifetime(lifetime);
        }
        return unit;
    }

    @Override
    public Unit spawn(Team team, Position pos) {
        return super.spawn(team, pos);
    }
}
