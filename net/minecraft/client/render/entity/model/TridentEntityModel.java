package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class TridentEntityModel extends Model
{
    public static final Identifier TEXTURE;
    private final Cuboid b;
    
    public TridentEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        (this.b = new Cuboid(this, 0, 0)).addBox(-0.5f, -4.0f, -0.5f, 1, 31, 1, 0.0f);
        final Cuboid cuboid1 = new Cuboid(this, 4, 0);
        cuboid1.addBox(-1.5f, 0.0f, -0.5f, 3, 2, 1);
        this.b.addChild(cuboid1);
        final Cuboid cuboid2 = new Cuboid(this, 4, 3);
        cuboid2.addBox(-2.5f, -3.0f, -0.5f, 1, 4, 1);
        this.b.addChild(cuboid2);
        final Cuboid cuboid3 = new Cuboid(this, 4, 3);
        cuboid3.mirror = true;
        cuboid3.addBox(1.5f, -3.0f, -0.5f, 1, 4, 1);
        this.b.addChild(cuboid3);
    }
    
    public void renderItem() {
        this.b.render(0.0625f);
    }
    
    static {
        TEXTURE = new Identifier("textures/entity/trident.png");
    }
}
