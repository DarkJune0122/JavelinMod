package javelin.content;

import arc.func.Prov;
import arc.graphics.Color;
import javelin.JavelinMod;
import javelin.units.JavelinEntity;
import javelin.units.JavelinType;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.EntityMapping;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.blocks.storage.CoreBlock;

public class UnitTypes
{
    // Units:
    public static UnitType javelin;

    // Type IDs:
    public static final int javelinClassID;

    // ID initialization.
    static {
        javelinClassID = map(JavelinEntity::new);
        EntityMapping.nameMap.put("javelin-ship", JavelinEntity::new);
        EntityMapping.customIdMap.put(javelinClassID, "javelin-ship");
    }

    private static int map(Prov<? extends Unit> provider) {
        var array = EntityMapping.idMap;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == null) {
                array[i] = provider;
                return i;
            }
        }

        // Returns 'UnitEntity' unit constructor ID by default.
        return 3;
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                 Content loading
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public static void load()
    {
        javelin = new JavelinType("javelin-ship") {{
            aiController = BuilderAI::new;
            isEnemy = false;

            health = 640f;
            hitSize = 11f;

            flying = true;
            mineSpeed = 7.2f;
            mineTier = 2;
            buildSpeed = 0.75f;
            drag = 0.05f;
            speed = 14.6f;
            rotateSpeed = 2.4f;
            accel = 0.02f;
            itemCapacity = 60;
            engineOffset = 6f;
            faceTarget = true;
            lowAltitude = true;

            dischargeSound = Sounds.spark;
            dischargeBullet = new LightningBulletType()
            {{
                damage = 16f;
                legCount = 6;
                buildingDamageMultiplier = 0.01f;
            }};

            weapons.add(new Weapon("javelin-ship-launcher"){{
                top = false;
                reload = 20f;
                x = 3f;
                y = 0.5f;
                rotate = false;
                shoot.shotDelay = 2.4f;
                ejectEffect = Fx.casing1;

                shootSound = Sounds.missile;

                bullet = new MissileBulletType(3.6f, 20f){{
                    width = 8f;
                    height = 11f;
                    lifetime = 96f;
                    homingRange = 52f;
                    homingPower = 0.3f;
                    splashDamage = 40f;
                    splashDamageRadius = 14.8f;
                    shootEffect = Fx.shootSmall;
                    buildingDamageMultiplier = 0.01f;
                    backColor = Pal.lancerLaser;
                    frontColor = Color.white;
                    trailColor = Pal.lancerLaser;
                    engineColor = Pal.lancerLaser;
                }};
            }});
        }};

        ((CoreBlock)Blocks.coreNucleus).unitType = javelin;
    }
}
