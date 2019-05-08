package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.item.FoodItemSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

public class HungerManager
{
    private int foodLevel;
    private float foodSaturationLevel;
    private float exhaustion;
    private int foodStarvationTimer;
    private int prevFoodLevel;
    
    public HungerManager() {
        this.foodLevel = 20;
        this.prevFoodLevel = 20;
        this.foodSaturationLevel = 5.0f;
    }
    
    public void add(final int food, final float float2) {
        this.foodLevel = Math.min(food + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + food * float2 * 2.0f, (float)this.foodLevel);
    }
    
    public void eat(final Item item, final ItemStack itemStack) {
        if (item.isFood()) {
            final FoodItemSetting foodItemSetting3 = item.getFoodSetting();
            this.add(foodItemSetting3.getHunger(), foodItemSetting3.getSaturationModifier());
        }
    }
    
    public void update(final PlayerEntity playerEntity) {
        final Difficulty difficulty2 = playerEntity.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.exhaustion > 4.0f) {
            this.exhaustion -= 4.0f;
            if (this.foodSaturationLevel > 0.0f) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0f, 0.0f);
            }
            else if (difficulty2 != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        final boolean boolean3 = playerEntity.world.getGameRules().getBoolean("naturalRegeneration");
        if (boolean3 && this.foodSaturationLevel > 0.0f && playerEntity.canFoodHeal() && this.foodLevel >= 20) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 10) {
                final float float4 = Math.min(this.foodSaturationLevel, 6.0f);
                playerEntity.heal(float4 / 6.0f);
                this.addExhaustion(float4);
                this.foodStarvationTimer = 0;
            }
        }
        else if (boolean3 && this.foodLevel >= 18 && playerEntity.canFoodHeal()) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 80) {
                playerEntity.heal(1.0f);
                this.addExhaustion(6.0f);
                this.foodStarvationTimer = 0;
            }
        }
        else if (this.foodLevel <= 0) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 80) {
                if (playerEntity.getHealth() > 10.0f || difficulty2 == Difficulty.HARD || (playerEntity.getHealth() > 1.0f && difficulty2 == Difficulty.NORMAL)) {
                    playerEntity.damage(DamageSource.STARVE, 1.0f);
                }
                this.foodStarvationTimer = 0;
            }
        }
        else {
            this.foodStarvationTimer = 0;
        }
    }
    
    public void deserialize(final CompoundTag compoundTag) {
        if (compoundTag.containsKey("foodLevel", 99)) {
            this.foodLevel = compoundTag.getInt("foodLevel");
            this.foodStarvationTimer = compoundTag.getInt("foodTickTimer");
            this.foodSaturationLevel = compoundTag.getFloat("foodSaturationLevel");
            this.exhaustion = compoundTag.getFloat("foodExhaustionLevel");
        }
    }
    
    public void serialize(final CompoundTag compoundTag) {
        compoundTag.putInt("foodLevel", this.foodLevel);
        compoundTag.putInt("foodTickTimer", this.foodStarvationTimer);
        compoundTag.putFloat("foodSaturationLevel", this.foodSaturationLevel);
        compoundTag.putFloat("foodExhaustionLevel", this.exhaustion);
    }
    
    public int getFoodLevel() {
        return this.foodLevel;
    }
    
    public boolean isNotFull() {
        return this.foodLevel < 20;
    }
    
    public void addExhaustion(final float float1) {
        this.exhaustion = Math.min(this.exhaustion + float1, 40.0f);
    }
    
    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }
    
    public void setFoodLevel(final int integer) {
        this.foodLevel = integer;
    }
    
    @Environment(EnvType.CLIENT)
    public void setSaturationLevelClient(final float float1) {
        this.foodSaturationLevel = float1;
    }
}
