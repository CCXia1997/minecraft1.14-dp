package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity> extends HorseEntityModel<T>
{
    private final Cuboid f;
    private final Cuboid g;
    
    public DonkeyEntityModel(final float float1) {
        super(float1);
        (this.f = new Cuboid(this, 26, 21)).addBox(-4.0f, 0.0f, -2.0f, 8, 8, 3);
        (this.g = new Cuboid(this, 26, 21)).addBox(-4.0f, 0.0f, -2.0f, 8, 8, 3);
        this.f.yaw = -1.5707964f;
        this.g.yaw = 1.5707964f;
        this.f.setRotationPoint(6.0f, -8.0f, 0.0f);
        this.g.setRotationPoint(-6.0f, -8.0f, 0.0f);
        this.a.addChild(this.f);
        this.a.addChild(this.g);
    }
    
    @Override
    protected void a(final Cuboid cuboid) {
        final Cuboid cuboid2 = new Cuboid(this, 0, 12);
        cuboid2.addBox(-1.0f, -7.0f, 0.0f, 2, 7, 1);
        cuboid2.setRotationPoint(1.25f, -10.0f, 4.0f);
        final Cuboid cuboid3 = new Cuboid(this, 0, 12);
        cuboid3.addBox(-1.0f, -7.0f, 0.0f, 2, 7, 1);
        cuboid3.setRotationPoint(-1.25f, -10.0f, 4.0f);
        cuboid2.pitch = 0.2617994f;
        cuboid2.roll = 0.2617994f;
        cuboid3.pitch = 0.2617994f;
        cuboid3.roll = -0.2617994f;
        cuboid.addChild(cuboid2);
        cuboid.addChild(cuboid3);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        if (entity.hasChest()) {
            this.f.visible = true;
            this.g.visible = true;
        }
        else {
            this.f.visible = false;
            this.g.visible = false;
        }
        super.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
    }
}
