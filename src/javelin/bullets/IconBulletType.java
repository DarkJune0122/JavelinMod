package javelin.bullets;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.entities.bullet.BulletType;

public class IconBulletType extends BulletType {
    public final String name;
    public TextureRegion icon;

    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    //
    //                 Constructors
    //
    //  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ==
    public IconBulletType(float speed, float damage, String name){
        this(name);
        this.speed = speed;
        this.damage = damage;
    }

    public IconBulletType(String name) {
        this.name = name;
    }

    @Override
    public void loadIcon(){
        icon = Core.atlas.find(getContentType().name() + "-" + name + "-icon");
    }
}
