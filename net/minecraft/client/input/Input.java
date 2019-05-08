package net.minecraft.client.input;

import net.minecraft.util.math.Vec2f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Input
{
    public float movementSideways;
    public float movementForward;
    public boolean pressingForward;
    public boolean pressingBack;
    public boolean pressingLeft;
    public boolean pressingRight;
    public boolean jumping;
    public boolean sneaking;
    
    public void tick(final boolean boolean1, final boolean boolean2) {
    }
    
    public Vec2f getMovementInput() {
        return new Vec2f(this.movementSideways, this.movementForward);
    }
}
