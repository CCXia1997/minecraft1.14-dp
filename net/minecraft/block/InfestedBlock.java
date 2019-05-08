package net.minecraft.block;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Map;

public class InfestedBlock extends Block
{
    private final Block regularBlock;
    private static final Map<Block, Block> INFESTED_TO_REGULAR;
    
    public InfestedBlock(final Block block, final Settings settings) {
        super(settings);
        this.regularBlock = block;
        InfestedBlock.INFESTED_TO_REGULAR.put(block, this);
    }
    
    public Block getRegularBlock() {
        return this.regularBlock;
    }
    
    public static boolean hasRegularBlock(final BlockState infestedBlockState) {
        return InfestedBlock.INFESTED_TO_REGULAR.containsKey(infestedBlockState.getBlock());
    }
    
    @Override
    public void onStacksDropped(final BlockState state, final World world, final BlockPos pos, final ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        if (!world.isClient && world.getGameRules().getBoolean("doTileDrops") && EnchantmentHelper.getLevel(Enchantments.t, stack) == 0) {
            final SilverfishEntity silverfishEntity5 = EntityType.SILVERFISH.create(world);
            silverfishEntity5.setPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
            world.spawnEntity(silverfishEntity5);
            silverfishEntity5.playSpawnEffects();
        }
    }
    
    public static BlockState getRegularBlock(final Block infestedBlockState) {
        return InfestedBlock.INFESTED_TO_REGULAR.get(infestedBlockState).getDefaultState();
    }
    
    static {
        INFESTED_TO_REGULAR = Maps.newIdentityHashMap();
    }
}
