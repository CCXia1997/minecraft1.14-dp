package net.minecraft.item;

import java.util.Comparator;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import java.util.Collection;
import net.minecraft.text.StringTextComponent;
import com.google.common.collect.Lists;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.ActionResult;

public class FireworkItem extends Item
{
    public FireworkItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        if (!world2.isClient) {
            final ItemStack itemStack3 = usageContext.getItemStack();
            final Vec3d vec3d4 = usageContext.getPos();
            final FireworkEntity fireworkEntity5 = new FireworkEntity(world2, vec3d4.x, vec3d4.y, vec3d4.z, itemStack3);
            world2.spawnEntity(fireworkEntity5);
            itemStack3.subtractAmount(1);
        }
        return ActionResult.a;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        if (player.isFallFlying()) {
            final ItemStack itemStack4 = player.getStackInHand(hand);
            if (!world.isClient) {
                world.spawnEntity(new FireworkEntity(world, itemStack4, player));
                if (!player.abilities.creativeMode) {
                    itemStack4.subtractAmount(1);
                }
            }
            return new TypedActionResult<ItemStack>(ActionResult.a, player.getStackInHand(hand));
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, player.getStackInHand(hand));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        final CompoundTag compoundTag5 = stack.getSubCompoundTag("Fireworks");
        if (compoundTag5 == null) {
            return;
        }
        if (compoundTag5.containsKey("Flight", 99)) {
            tooltip.add(new TranslatableTextComponent("item.minecraft.firework_rocket.flight", new Object[0]).append(" ").append(String.valueOf(compoundTag5.getByte("Flight"))).applyFormat(TextFormat.h));
        }
        final ListTag listTag6 = compoundTag5.getList("Explosions", 10);
        if (!listTag6.isEmpty()) {
            for (int integer7 = 0; integer7 < listTag6.size(); ++integer7) {
                final CompoundTag compoundTag6 = listTag6.getCompoundTag(integer7);
                final List<TextComponent> list9 = Lists.newArrayList();
                FireworkChargeItem.buildTooltip(compoundTag6, list9);
                if (!list9.isEmpty()) {
                    for (int integer8 = 1; integer8 < list9.size(); ++integer8) {
                        list9.set(integer8, new StringTextComponent("  ").append(list9.get(integer8)).applyFormat(TextFormat.h));
                    }
                    tooltip.addAll(list9);
                }
            }
        }
    }
    
    public enum Type
    {
        a(0, "small_ball"), 
        b(1, "large_ball"), 
        c(2, "star"), 
        d(3, "creeper"), 
        e(4, "burst");
        
        private static final Type[] TYPES;
        private final int id;
        private final String name;
        
        private Type(final int id, final String name) {
            this.id = id;
            this.name = name;
        }
        
        public int getId() {
            return this.id;
        }
        
        @Environment(EnvType.CLIENT)
        public String getName() {
            return this.name;
        }
        
        @Environment(EnvType.CLIENT)
        public static Type fromId(final int id) {
            if (id < 0 || id >= Type.TYPES.length) {
                return Type.a;
            }
            return Type.TYPES[id];
        }
        
        static {
            TYPES = Arrays.<Type>stream(values()).sorted(Comparator.comparingInt(type -> type.id)).<Type>toArray(Type[]::new);
        }
    }
}
