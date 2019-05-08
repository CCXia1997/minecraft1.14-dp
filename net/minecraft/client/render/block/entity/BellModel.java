package net.minecraft.client.render.block.entity;

import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BellModel extends Model
{
    private final Cuboid a;
    private final Cuboid b;
    
    public BellModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        (this.a = new Cuboid(this, 0, 0)).addBox(-3.0f, -6.0f, -3.0f, 6, 7, 6);
        this.a.setRotationPoint(8.0f, 12.0f, 8.0f);
        (this.b = new Cuboid(this, 0, 13)).addBox(4.0f, 4.0f, 4.0f, 8, 2, 8);
        this.b.setRotationPoint(-8.0f, -12.0f, -8.0f);
        this.a.addChild(this.b);
    }
    
    public void a(final float float1, final float float2, final float float3) {
        this.a.pitch = float1;
        this.a.roll = float2;
        this.a.render(float3);
    }
}
