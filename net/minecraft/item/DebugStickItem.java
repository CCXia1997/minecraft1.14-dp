package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.text.ChatMessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.SystemUtil;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.Collection;
import net.minecraft.state.StateFactory;
import net.minecraft.block.Block;
import net.minecraft.state.property.Property;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.world.IWorld;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DebugStickItem extends Item
{
    public DebugStickItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return true;
    }
    
    @Override
    public boolean beforeBlockBreak(final BlockState blockState, final World world, final BlockPos position, final PlayerEntity player) {
        if (!world.isClient) {
            this.use(player, blockState, world, position, false, player.getStackInHand(Hand.a));
        }
        return false;
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final PlayerEntity playerEntity2 = usageContext.getPlayer();
        final World world3 = usageContext.getWorld();
        if (!world3.isClient && playerEntity2 != null) {
            final BlockPos blockPos4 = usageContext.getBlockPos();
            this.use(playerEntity2, world3.getBlockState(blockPos4), world3, blockPos4, true, usageContext.getItemStack());
        }
        return ActionResult.a;
    }
    
    private void use(final PlayerEntity player, final BlockState state, final IWorld world, final BlockPos pos, final boolean update, final ItemStack stack) {
        if (!player.isCreativeLevelTwoOp()) {
            return;
        }
        final Block block7 = state.getBlock();
        final StateFactory<Block, BlockState> stateFactory8 = block7.getStateFactory();
        final Collection<Property<?>> collection9 = stateFactory8.getProperties();
        final String string10 = Registry.BLOCK.getId(block7).toString();
        if (collection9.isEmpty()) {
            sendMessage(player, new TranslatableTextComponent(this.getTranslationKey() + ".empty", new Object[] { string10 }));
            return;
        }
        final CompoundTag compoundTag11 = stack.getOrCreateSubCompoundTag("DebugProperty");
        final String string11 = compoundTag11.getString(string10);
        Property<?> property13 = stateFactory8.getProperty(string11);
        if (update) {
            if (property13 == null) {
                property13 = collection9.iterator().next();
            }
            final BlockState blockState14 = DebugStickItem.cycle(state, property13, player.isSneaking());
            world.setBlockState(pos, blockState14, 18);
            sendMessage(player, new TranslatableTextComponent(this.getTranslationKey() + ".update", new Object[] { property13.getName(), DebugStickItem.getPropertyString(blockState14, property13) }));
        }
        else {
            property13 = DebugStickItem.<Property<?>>cycle(collection9, property13, player.isSneaking());
            final String string12 = property13.getName();
            compoundTag11.putString(string10, string12);
            sendMessage(player, new TranslatableTextComponent(this.getTranslationKey() + ".select", new Object[] { string12, DebugStickItem.getPropertyString(state, property13) }));
        }
    }
    
    private static <T extends Comparable<T>> BlockState cycle(final BlockState state, final Property<T> property, final boolean reverse) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<T, Comparable>with(property, (Comparable)DebugStickItem.<V>cycle((Iterable<V>)property.getValues(), (V)state.<T>get((Property<T>)property), reverse));
    }
    
    private static <T> T cycle(final Iterable<T> iterable, @Nullable final T current, final boolean reverse) {
        return reverse ? SystemUtil.<T>previous(iterable, current) : SystemUtil.<T>next(iterable, current);
    }
    
    private static void sendMessage(final PlayerEntity player, final TextComponent component) {
        ((ServerPlayerEntity)player).sendChatMessage(component, ChatMessageType.c);
    }
    
    private static <T extends Comparable<T>> String getPropertyString(final BlockState state, final Property<T> property) {
        return property.getValueAsString(state.<T>get(property));
    }
}
