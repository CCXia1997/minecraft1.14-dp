package net.minecraft.client.model;

import java.util.Random;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Model
{
    public final List<Cuboid> cuboidList;
    public int textureWidth;
    public int textureHeight;
    
    public Model() {
        this.cuboidList = Lists.newArrayList();
        this.textureWidth = 64;
        this.textureHeight = 32;
    }
    
    public Cuboid getRandomCuboid(final Random rand) {
        return this.cuboidList.get(rand.nextInt(this.cuboidList.size()));
    }
}
