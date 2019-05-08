package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class ShieldEntityModel extends Model
{
    private final Cuboid a;
    private final Cuboid b;
    
    public ShieldEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.a = new Cuboid(this, 0, 0)).addBox(-6.0f, -11.0f, -2.0f, 12, 22, 1, 0.0f);
        (this.b = new Cuboid(this, 26, 0)).addBox(-1.0f, -3.0f, -1.0f, 2, 6, 6, 0.0f);
    }
    
    public void renderItem() {
        this.a.render(0.0625f);
        this.b.render(0.0625f);
    }
}
