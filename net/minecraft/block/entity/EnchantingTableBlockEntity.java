package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import java.util.Random;
import net.minecraft.util.Tickable;
import net.minecraft.util.Nameable;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable, Tickable
{
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float g;
    public float h;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float k;
    public float l;
    public float m;
    private static final Random RANDOM;
    private TextComponent customName;
    
    public EnchantingTableBlockEntity() {
        super(BlockEntityType.ENCHANTING_TABLE);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (this.hasCustomName()) {
            compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
        }
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
    }
    
    @Override
    public void tick() {
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.l = this.k;
        final PlayerEntity playerEntity1 = this.world.getClosestPlayer(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, 3.0, false);
        if (playerEntity1 != null) {
            final double double2 = playerEntity1.x - (this.pos.getX() + 0.5f);
            final double double3 = playerEntity1.z - (this.pos.getZ() + 0.5f);
            this.m = (float)MathHelper.atan2(double3, double2);
            this.nextPageTurningSpeed += 0.1f;
            if (this.nextPageTurningSpeed < 0.5f || EnchantingTableBlockEntity.RANDOM.nextInt(40) == 0) {
                final float float6 = this.g;
                do {
                    this.g += EnchantingTableBlockEntity.RANDOM.nextInt(4) - EnchantingTableBlockEntity.RANDOM.nextInt(4);
                } while (float6 == this.g);
            }
        }
        else {
            this.m += 0.02f;
            this.nextPageTurningSpeed -= 0.1f;
        }
        while (this.k >= 3.1415927f) {
            this.k -= 6.2831855f;
        }
        while (this.k < -3.1415927f) {
            this.k += 6.2831855f;
        }
        while (this.m >= 3.1415927f) {
            this.m -= 6.2831855f;
        }
        while (this.m < -3.1415927f) {
            this.m += 6.2831855f;
        }
        float float7;
        for (float7 = this.m - this.k; float7 >= 3.1415927f; float7 -= 6.2831855f) {}
        while (float7 < -3.1415927f) {
            float7 += 6.2831855f;
        }
        this.k += float7 * 0.4f;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        float float8 = (this.g - this.nextPageAngle) * 0.4f;
        final float float9 = 0.2f;
        float8 = MathHelper.clamp(float8, -0.2f, 0.2f);
        this.h += (float8 - this.h) * 0.9f;
        this.nextPageAngle += this.h;
    }
    
    @Override
    public TextComponent getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableTextComponent("container.enchant", new Object[0]);
    }
    
    public void setCustomName(@Nullable final TextComponent value) {
        this.customName = value;
    }
    
    @Nullable
    @Override
    public TextComponent getCustomName() {
        return this.customName;
    }
    
    static {
        RANDOM = new Random();
    }
}
