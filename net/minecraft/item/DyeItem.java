package net.minecraft.item;

import com.google.common.collect.Maps;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import java.util.Map;

public class DyeItem extends Item
{
    private static final Map<DyeColor, DyeItem> dyes;
    private final DyeColor color;
    
    public DyeItem(final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
        DyeItem.dyes.put(dyeColor, this);
    }
    
    @Override
    public boolean interactWithEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity target, final Hand hand) {
        if (target instanceof SheepEntity) {
            final SheepEntity sheepEntity5 = (SheepEntity)target;
            if (sheepEntity5.isAlive() && !sheepEntity5.isSheared() && sheepEntity5.getColor() != this.color) {
                sheepEntity5.setColor(this.color);
                stack.subtractAmount(1);
            }
            return true;
        }
        return false;
    }
    
    public DyeColor getColor() {
        return this.color;
    }
    
    public static DyeItem fromColor(final DyeColor dyeColor) {
        return DyeItem.dyes.get(dyeColor);
    }
    
    static {
        dyes = Maps.<DyeColor, Object>newEnumMap(DyeColor.class);
    }
}
