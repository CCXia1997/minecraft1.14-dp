package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityModel extends Model
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid c;
    
    public BannerBlockEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.a = new Cuboid(this, 0, 0)).addBox(-10.0f, 0.0f, -2.0f, 20, 40, 1, 0.0f);
        (this.b = new Cuboid(this, 44, 0)).addBox(-1.0f, -30.0f, -1.0f, 2, 42, 2, 0.0f);
        (this.c = new Cuboid(this, 0, 42)).addBox(-10.0f, -32.0f, -1.0f, 20, 2, 2, 0.0f);
    }
    
    public void a() {
        this.a.rotationPointY = -32.0f;
        this.a.render(0.0625f);
        this.b.render(0.0625f);
        this.c.render(0.0625f);
    }
    
    public Cuboid b() {
        return this.b;
    }
    
    public Cuboid c() {
        return this.a;
    }
}
