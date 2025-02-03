package javelin.ui;

import arc.Core;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import javelin.bullets.IconBulletType;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;

import static mindustry.Vars.tilesize;

public class StatValues {
    public static StatValue charge(BulletType bullet) {
        return charge(bullet, 0);
    }

    public static StatValue charge(BulletType bullet, int indent) {
        return table -> {
            table.row();

            BulletType type = getRootType(bullet);
            boolean compact = indent > 0;

            // UI setup:
            table.table(Styles.grayPanel, bt -> {
                bt.left().top().defaults().padRight(3).left();

                if (indent == 0 && bullet instanceof IconBulletType iButton) {
                    table.image(iButton.icon).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top();
                    // table.add(Core.bundle.get("javelin.charge")).padRight(10).left().top();
                    bt.row();
                }

                if(type.damage > 0 && (type.collides || type.splashDamage <= 0)){
                    if(type.continuousDamage() > 0){
                        bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                    }else{
                        bt.add(Core.bundle.format("bullet.damage", type.damage));
                    }
                }

                if(type.buildingDamageMultiplier != 1){
                    int val = (int)(type.buildingDamageMultiplier * 100 - 100);
                    sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat(val)));
                }

                if(type.splashDamage > 0){
                    sep(bt, Core.bundle.format("bullet.splashdamage", (int)type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                }

                if(type.knockback > 0){
                    sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                }

                if(type.healPercent > 0f){
                    sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                }

                if(type.healAmount > 0f){
                    sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                }

                if(type.pierce || type.pierceCap != -1){
                    sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                }

                if(type.incendAmount > 0){
                    sep(bt, "@bullet.incendiary");
                }

                if(type.homingPower > 0.01f){
                    sep(bt, "@bullet.homing");
                }

                if(type.lightning > 0){
                    sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
                }

                if(type.pierceArmor){
                    sep(bt, "@bullet.armorpierce");
                }

                if(type.suppressionRange > 0){
                    sep(bt, Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / tilesize, 1)));
                }

                if(type.status != StatusEffects.none){
                    sep(bt, (type.status.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + (type.status.reactive ? "" : "[lightgray] ~ [stat]" + ((int)(type.statusDuration / 60f)) + "[lightgray] " + Core.bundle.get("unit.seconds")));
                }

                if(type.intervalBullet != null){
                    bt.row();

                    Table ic = new Table();
                    charge(type.intervalBullet, indent + 1).display(ic);
                    Collapser coll = new Collapser(ic, true);
                    coll.setDuration(0.1f);

                    bt.table(it -> {
                        it.left().defaults().left();

                        it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                        it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                    });
                    bt.row();
                    bt.add(coll);
                }

                if(type.fragBullet != null){
                    bt.row();

                    Table fc = new Table();
                    charge(type.fragBullet, indent + 1).display(fc);
                    Collapser coll = new Collapser(fc, true);
                    coll.setDuration(0.1f);

                    bt.table(ft -> {
                        ft.left().defaults().left();

                        ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                        ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                    });
                    bt.row();
                    bt.add(coll);
                }
            }).padLeft(indent * 5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
            table.row();
        };
    }

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                   Helpers
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    private static BulletType getRootType(BulletType type) {
        while (type.spawnUnit != null && type.spawnUnit.weapons.size > 0) {
            type = type.spawnUnit.weapons.first().bullet;
        }

        return type;
    }
    private static String ammoStat(float val){
        return (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }

    /** Used by charge info displaying. */
    private static void sep(Table table, String text){
        table.row();
        table.add(text);
    }
}
