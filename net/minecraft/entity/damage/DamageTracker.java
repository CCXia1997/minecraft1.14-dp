package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import java.util.List;

public class DamageTracker
{
    private final List<DamageRecord> recentDamage;
    private final LivingEntity entity;
    private int ageOnLastDamage;
    private int ageOnLastAttacked;
    private int ageOnLastUpdate;
    private boolean recentlyAttacked;
    private boolean hasDamage;
    private String fallDeathSuffix;
    
    public DamageTracker(final LivingEntity livingEntity) {
        this.recentDamage = Lists.newArrayList();
        this.entity = livingEntity;
    }
    
    public void setFallDeathSuffix() {
        this.clearFallDeathSuffix();
        if (this.entity.isClimbing()) {
            final Block block1 = this.entity.world.getBlockState(new BlockPos(this.entity.x, this.entity.getBoundingBox().minY, this.entity.z)).getBlock();
            if (block1 == Blocks.ce) {
                this.fallDeathSuffix = "ladder";
            }
            else if (block1 == Blocks.dH) {
                this.fallDeathSuffix = "vines";
            }
        }
        else if (this.entity.isInsideWater()) {
            this.fallDeathSuffix = "water";
        }
    }
    
    public void onDamage(final DamageSource damageSource, final float originalHealth, final float float3) {
        this.update();
        this.setFallDeathSuffix();
        final DamageRecord damageRecord4 = new DamageRecord(damageSource, this.entity.age, originalHealth, float3, this.fallDeathSuffix, this.entity.fallDistance);
        this.recentDamage.add(damageRecord4);
        this.ageOnLastDamage = this.entity.age;
        this.hasDamage = true;
        if (damageRecord4.isAttackerLiving() && !this.recentlyAttacked && this.entity.isAlive()) {
            this.recentlyAttacked = true;
            this.ageOnLastAttacked = this.entity.age;
            this.ageOnLastUpdate = this.ageOnLastAttacked;
            this.entity.e();
        }
    }
    
    public TextComponent getDeathMessage() {
        if (this.recentDamage.isEmpty()) {
            return new TranslatableTextComponent("death.attack.generic", new Object[] { this.entity.getDisplayName() });
        }
        final DamageRecord damageRecord1 = this.getBiggestFall();
        final DamageRecord damageRecord2 = this.recentDamage.get(this.recentDamage.size() - 1);
        final TextComponent textComponent4 = damageRecord2.getAttackerName();
        final Entity entity5 = damageRecord2.getDamageSource().getAttacker();
        TextComponent textComponent6;
        if (damageRecord1 != null && damageRecord2.getDamageSource() == DamageSource.FALL) {
            final TextComponent textComponent5 = damageRecord1.getAttackerName();
            if (damageRecord1.getDamageSource() == DamageSource.FALL || damageRecord1.getDamageSource() == DamageSource.OUT_OF_WORLD) {
                textComponent6 = new TranslatableTextComponent("death.fell.accident." + this.getFallDeathSuffix(damageRecord1), new Object[] { this.entity.getDisplayName() });
            }
            else if (textComponent5 != null && (textComponent4 == null || !textComponent5.equals(textComponent4))) {
                final Entity entity6 = damageRecord1.getDamageSource().getAttacker();
                final ItemStack itemStack8 = (entity6 instanceof LivingEntity) ? ((LivingEntity)entity6).getMainHandStack() : ItemStack.EMPTY;
                if (!itemStack8.isEmpty() && itemStack8.hasDisplayName()) {
                    textComponent6 = new TranslatableTextComponent("death.fell.assist.item", new Object[] { this.entity.getDisplayName(), textComponent5, itemStack8.toTextComponent() });
                }
                else {
                    textComponent6 = new TranslatableTextComponent("death.fell.assist", new Object[] { this.entity.getDisplayName(), textComponent5 });
                }
            }
            else if (textComponent4 != null) {
                final ItemStack itemStack9 = (entity5 instanceof LivingEntity) ? ((LivingEntity)entity5).getMainHandStack() : ItemStack.EMPTY;
                if (!itemStack9.isEmpty() && itemStack9.hasDisplayName()) {
                    textComponent6 = new TranslatableTextComponent("death.fell.finish.item", new Object[] { this.entity.getDisplayName(), textComponent4, itemStack9.toTextComponent() });
                }
                else {
                    textComponent6 = new TranslatableTextComponent("death.fell.finish", new Object[] { this.entity.getDisplayName(), textComponent4 });
                }
            }
            else {
                textComponent6 = new TranslatableTextComponent("death.fell.killer", new Object[] { this.entity.getDisplayName() });
            }
        }
        else {
            textComponent6 = damageRecord2.getDamageSource().getDeathMessage(this.entity);
        }
        return textComponent6;
    }
    
    @Nullable
    public LivingEntity getBiggestAttacker() {
        LivingEntity livingEntity1 = null;
        PlayerEntity playerEntity2 = null;
        float float3 = 0.0f;
        float float4 = 0.0f;
        for (final DamageRecord damageRecord6 : this.recentDamage) {
            if (damageRecord6.getDamageSource().getAttacker() instanceof PlayerEntity && (playerEntity2 == null || damageRecord6.getDamage() > float4)) {
                float4 = damageRecord6.getDamage();
                playerEntity2 = (PlayerEntity)damageRecord6.getDamageSource().getAttacker();
            }
            if (damageRecord6.getDamageSource().getAttacker() instanceof LivingEntity && (livingEntity1 == null || damageRecord6.getDamage() > float3)) {
                float3 = damageRecord6.getDamage();
                livingEntity1 = (LivingEntity)damageRecord6.getDamageSource().getAttacker();
            }
        }
        if (playerEntity2 != null && float4 >= float3 / 3.0f) {
            return playerEntity2;
        }
        return livingEntity1;
    }
    
    @Nullable
    private DamageRecord getBiggestFall() {
        DamageRecord damageRecord1 = null;
        DamageRecord damageRecord2 = null;
        float float3 = 0.0f;
        float float4 = 0.0f;
        for (int integer5 = 0; integer5 < this.recentDamage.size(); ++integer5) {
            final DamageRecord damageRecord3 = this.recentDamage.get(integer5);
            final DamageRecord damageRecord4 = (integer5 > 0) ? this.recentDamage.get(integer5 - 1) : null;
            if ((damageRecord3.getDamageSource() == DamageSource.FALL || damageRecord3.getDamageSource() == DamageSource.OUT_OF_WORLD) && damageRecord3.getFallDistance() > 0.0f && (damageRecord1 == null || damageRecord3.getFallDistance() > float4)) {
                if (integer5 > 0) {
                    damageRecord1 = damageRecord4;
                }
                else {
                    damageRecord1 = damageRecord3;
                }
                float4 = damageRecord3.getFallDistance();
            }
            if (damageRecord3.getFallDeathSuffix() != null && (damageRecord2 == null || damageRecord3.getDamage() > float3)) {
                damageRecord2 = damageRecord3;
                float3 = damageRecord3.getDamage();
            }
        }
        if (float4 > 5.0f && damageRecord1 != null) {
            return damageRecord1;
        }
        if (float3 > 5.0f && damageRecord2 != null) {
            return damageRecord2;
        }
        return null;
    }
    
    private String getFallDeathSuffix(final DamageRecord damageRecord) {
        return (damageRecord.getFallDeathSuffix() == null) ? "generic" : damageRecord.getFallDeathSuffix();
    }
    
    public int getTimeSinceLastAttack() {
        if (this.recentlyAttacked) {
            return this.entity.age - this.ageOnLastAttacked;
        }
        return this.ageOnLastUpdate - this.ageOnLastAttacked;
    }
    
    private void clearFallDeathSuffix() {
        this.fallDeathSuffix = null;
    }
    
    public void update() {
        final int integer1 = this.recentlyAttacked ? 300 : 100;
        if (this.hasDamage && (!this.entity.isAlive() || this.entity.age - this.ageOnLastDamage > integer1)) {
            final boolean boolean2 = this.recentlyAttacked;
            this.hasDamage = false;
            this.recentlyAttacked = false;
            this.ageOnLastUpdate = this.entity.age;
            if (boolean2) {
                this.entity.f();
            }
            this.recentDamage.clear();
        }
    }
    
    public LivingEntity getEntity() {
        return this.entity;
    }
}
