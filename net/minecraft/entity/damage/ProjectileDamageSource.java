package net.minecraft.entity.damage;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

public class ProjectileDamageSource extends EntityDamageSource
{
    private final Entity attacker;
    
    public ProjectileDamageSource(final String name, final Entity projectile, @Nullable final Entity entity3) {
        super(name, projectile);
        this.attacker = entity3;
    }
    
    @Nullable
    @Override
    public Entity getSource() {
        return this.source;
    }
    
    @Nullable
    @Override
    public Entity getAttacker() {
        return this.attacker;
    }
    
    @Override
    public TextComponent getDeathMessage(final LivingEntity livingEntity) {
        final TextComponent textComponent2 = (this.attacker == null) ? this.source.getDisplayName() : this.attacker.getDisplayName();
        final ItemStack itemStack3 = (this.attacker instanceof LivingEntity) ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
        final String string4 = "death.attack." + this.name;
        final String string5 = string4 + ".item";
        if (!itemStack3.isEmpty() && itemStack3.hasDisplayName()) {
            return new TranslatableTextComponent(string5, new Object[] { livingEntity.getDisplayName(), textComponent2, itemStack3.toTextComponent() });
        }
        return new TranslatableTextComponent(string4, new Object[] { livingEntity.getDisplayName(), textComponent2 });
    }
}
