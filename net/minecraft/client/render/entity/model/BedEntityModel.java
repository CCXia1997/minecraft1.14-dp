package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BedEntityModel extends Model
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid[] c;
    
    public BedEntityModel() {
        this.c = new Cuboid[4];
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.a = new Cuboid(this, 0, 0)).addBox(0.0f, 0.0f, 0.0f, 16, 16, 6, 0.0f);
        (this.b = new Cuboid(this, 0, 22)).addBox(0.0f, 0.0f, 0.0f, 16, 16, 6, 0.0f);
        this.c[0] = new Cuboid(this, 50, 0);
        this.c[1] = new Cuboid(this, 50, 6);
        this.c[2] = new Cuboid(this, 50, 12);
        this.c[3] = new Cuboid(this, 50, 18);
        this.c[0].addBox(0.0f, 6.0f, -16.0f, 3, 3, 3);
        this.c[1].addBox(0.0f, 6.0f, 0.0f, 3, 3, 3);
        this.c[2].addBox(-16.0f, 6.0f, -16.0f, 3, 3, 3);
        this.c[3].addBox(-16.0f, 6.0f, 0.0f, 3, 3, 3);
        this.c[0].pitch = 1.5707964f;
        this.c[1].pitch = 1.5707964f;
        this.c[2].pitch = 1.5707964f;
        this.c[3].pitch = 1.5707964f;
        this.c[0].roll = 0.0f;
        this.c[1].roll = 1.5707964f;
        this.c[2].roll = 4.712389f;
        this.c[3].roll = 3.1415927f;
    }
    
    public void render() {
        this.a.render(0.0625f);
        this.b.render(0.0625f);
        this.c[0].render(0.0625f);
        this.c[1].render(0.0625f);
        this.c[2].render(0.0625f);
        this.c[3].render(0.0625f);
    }
    
    public void setVisible(final boolean boolean1) {
        this.a.visible = boolean1;
        this.b.visible = !boolean1;
        this.c[0].visible = !boolean1;
        this.c[1].visible = boolean1;
        this.c[2].visible = !boolean1;
        this.c[3].visible = boolean1;
    }
}
