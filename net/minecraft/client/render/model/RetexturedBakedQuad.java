package net.minecraft.client.render.model;

import java.util.Arrays;
import net.minecraft.client.texture.Sprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RetexturedBakedQuad extends BakedQuad
{
    private final Sprite spriteRetextured;
    
    public RetexturedBakedQuad(final BakedQuad parent, final Sprite sprite) {
        super(Arrays.copyOf(parent.getVertexData(), parent.getVertexData().length), parent.colorIndex, BakedQuadFactory.a(parent.getVertexData()), parent.getSprite());
        this.spriteRetextured = sprite;
        this.recalculateUvs();
    }
    
    private void recalculateUvs() {
        for (int integer1 = 0; integer1 < 4; ++integer1) {
            final int integer2 = 7 * integer1;
            this.vertexData[integer2 + 4] = Float.floatToRawIntBits(this.spriteRetextured.getU(this.sprite.getXFromU(Float.intBitsToFloat(this.vertexData[integer2 + 4]))));
            this.vertexData[integer2 + 4 + 1] = Float.floatToRawIntBits(this.spriteRetextured.getV(this.sprite.getYFromV(Float.intBitsToFloat(this.vertexData[integer2 + 4 + 1]))));
        }
    }
}
