package net.minecraft.world;

import net.minecraft.util.math.MathHelper;
import javax.annotation.concurrent.Immutable;

@Immutable
public class LocalDifficulty
{
    private final Difficulty globalDifficulty;
    private final float localDifficulty;
    
    public LocalDifficulty(final Difficulty difficulty, final long timeOfDay, final long long4, final float float6) {
        this.globalDifficulty = difficulty;
        this.localDifficulty = this.setLocalDifficulty(difficulty, timeOfDay, long4, float6);
    }
    
    public Difficulty getGlobalDifficulty() {
        return this.globalDifficulty;
    }
    
    public float getLocalDifficulty() {
        return this.localDifficulty;
    }
    
    public boolean a(final float float1) {
        return this.localDifficulty > float1;
    }
    
    public float getClampedLocalDifficulty() {
        if (this.localDifficulty < 2.0f) {
            return 0.0f;
        }
        if (this.localDifficulty > 4.0f) {
            return 1.0f;
        }
        return (this.localDifficulty - 2.0f) / 2.0f;
    }
    
    private float setLocalDifficulty(final Difficulty difficulty, final long long2, final long long4, final float float6) {
        if (difficulty == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        final boolean boolean7 = difficulty == Difficulty.HARD;
        float float7 = 0.75f;
        final float float8 = MathHelper.clamp((long2 - 72000.0f) / 1440000.0f, 0.0f, 1.0f) * 0.25f;
        float7 += float8;
        float float9 = 0.0f;
        float9 += MathHelper.clamp(long4 / 3600000.0f, 0.0f, 1.0f) * (boolean7 ? 1.0f : 0.75f);
        float9 += MathHelper.clamp(float6 * 0.25f, 0.0f, float8);
        if (difficulty == Difficulty.EASY) {
            float9 *= 0.5f;
        }
        float7 += float9;
        return difficulty.getId() * float7;
    }
}
