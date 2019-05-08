package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CowEntityModel<T extends Entity> extends QuadrupedEntityModel<T>
{
    public CowEntityModel() {
        super(12, 0.0f);
        (this.head = new Cuboid(this, 0, 0)).addBox(-4.0f, -4.0f, -6.0f, 8, 8, 6, 0.0f);
        this.head.setRotationPoint(0.0f, 4.0f, -8.0f);
        this.head.setTextureOffset(22, 0).addBox(-5.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f);
        this.head.setTextureOffset(22, 0).addBox(4.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f);
        (this.body = new Cuboid(this, 18, 4)).addBox(-6.0f, -10.0f, -7.0f, 12, 18, 10, 0.0f);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        this.body.setTextureOffset(52, 0).addBox(-2.0f, 2.0f, -8.0f, 4, 6, 1);
        final Cuboid leg1 = this.leg1;
        --leg1.rotationPointX;
        final Cuboid leg2 = this.leg2;
        ++leg2.rotationPointX;
        final Cuboid leg3 = this.leg1;
        leg3.rotationPointZ += 0.0f;
        final Cuboid leg4 = this.leg2;
        leg4.rotationPointZ += 0.0f;
        final Cuboid leg5 = this.leg3;
        --leg5.rotationPointX;
        final Cuboid leg6 = this.leg4;
        ++leg6.rotationPointX;
        final Cuboid leg7 = this.leg3;
        --leg7.rotationPointZ;
        final Cuboid leg8 = this.leg4;
        --leg8.rotationPointZ;
        this.k += 2.0f;
    }
    
    public Cuboid a() {
        return this.head;
    }
}
