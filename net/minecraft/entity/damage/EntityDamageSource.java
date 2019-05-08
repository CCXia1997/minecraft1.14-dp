package net.minecraft.entity.damage;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

public class EntityDamageSource extends DamageSource
{
    @Nullable
    protected final Entity source;
    private boolean y;
    
    public EntityDamageSource(final String name, @Nullable final Entity entity) {
        super(name);
        this.source = entity;
    }
    
    public EntityDamageSource x() {
        this.y = true;
        return this;
    }
    
    public boolean y() {
        return this.y;
    }
    
    @Nullable
    @Override
    public Entity getAttacker() {
        return this.source;
    }
    
    @Override
    public TextComponent getDeathMessage(final LivingEntity livingEntity) {
        final ItemStack itemStack2 = (this.source instanceof LivingEntity) ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        final String string3 = "death.attack." + this.name;
        if (!itemStack2.isEmpty() && itemStack2.hasDisplayName()) {
            return new TranslatableTextComponent(string3 + ".item", new Object[] { livingEntity.getDisplayName(), this.source.getDisplayName(), itemStack2.toTextComponent() });
        }
        return new TranslatableTextComponent(string3, new Object[] { livingEntity.getDisplayName(), this.source.getDisplayName() });
    }
    
    @Override
    public boolean isScaledWithDifficulty() {
        return this.source != null && this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }
    
    @Nullable
    @Override
    public Vec3d w() {
        return new Vec3d(this.source.x, this.source.y, this.source.z);
    }
}
