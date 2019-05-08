package net.minecraft.item;

import net.minecraft.world.IWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.world.biome.Biome;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import com.google.common.collect.Multiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.MaterialColor;
import net.minecraft.world.Heightmap;
import net.minecraft.world.BlockView;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.LinkedHashMultiset;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;

public class FilledMapItem extends MapItem
{
    public FilledMapItem(final Settings settings) {
        super(settings);
    }
    
    public static ItemStack createMap(final World world, final int integer2, final int integer3, final byte scale, final boolean showIcons, final boolean unlimitedTracking) {
        final ItemStack itemStack7 = new ItemStack(Items.lV);
        createMapState(itemStack7, world, integer2, integer3, scale, showIcons, unlimitedTracking, world.dimension.getType());
        return itemStack7;
    }
    
    @Nullable
    public static MapState getMapState(final ItemStack itemStack, final World world) {
        return world.getMapState(getMapStorageName(getMapId(itemStack)));
    }
    
    @Nullable
    public static MapState getOrCreateMapState(final ItemStack map, final World world) {
        MapState mapState3 = getMapState(map, world);
        if (mapState3 == null && !world.isClient) {
            mapState3 = createMapState(map, world, world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnZ(), 3, false, false, world.dimension.getType());
        }
        return mapState3;
    }
    
    public static int getMapId(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getTag();
        return (compoundTag2 != null && compoundTag2.containsKey("map", 99)) ? compoundTag2.getInt("map") : 0;
    }
    
    private static MapState createMapState(final ItemStack itemStack, final World world, final int integer3, final int integer4, final int scale, final boolean showIcons, final boolean unlimitedTracking, final DimensionType dimensionType) {
        final int integer5 = world.getNextMapId();
        final MapState mapState10 = new MapState(getMapStorageName(integer5));
        mapState10.init(integer3, integer4, scale, showIcons, unlimitedTracking, dimensionType);
        world.putMapState(mapState10);
        itemStack.getOrCreateTag().putInt("map", integer5);
        return mapState10;
    }
    
    public static String getMapStorageName(final int mapId) {
        return "map_" + mapId;
    }
    
    public void updateColors(final World world, final Entity entity, final MapState state) {
        if (world.dimension.getType() != state.dimension || !(entity instanceof PlayerEntity)) {
            return;
        }
        final int integer4 = 1 << state.scale;
        final int integer5 = state.xCenter;
        final int integer6 = state.zCenter;
        final int integer7 = MathHelper.floor(entity.x - integer5) / integer4 + 64;
        final int integer8 = MathHelper.floor(entity.z - integer6) / integer4 + 64;
        int integer9 = 128 / integer4;
        if (world.dimension.isNether()) {
            integer9 /= 2;
        }
        final MapState.PlayerUpdateTracker playerSyncData;
        final MapState.PlayerUpdateTracker playerUpdateTracker10 = playerSyncData = state.getPlayerSyncData((PlayerEntity)entity);
        ++playerSyncData.b;
        boolean boolean11 = false;
        for (int integer10 = integer7 - integer9 + 1; integer10 < integer7 + integer9; ++integer10) {
            if ((integer10 & 0xF) == (playerUpdateTracker10.b & 0xF) || boolean11) {
                boolean11 = false;
                double double13 = 0.0;
                for (int integer11 = integer8 - integer9 - 1; integer11 < integer8 + integer9; ++integer11) {
                    if (integer10 >= 0 && integer11 >= -1 && integer10 < 128) {
                        if (integer11 < 128) {
                            final int integer12 = integer10 - integer7;
                            final int integer13 = integer11 - integer8;
                            final boolean boolean12 = integer12 * integer12 + integer13 * integer13 > (integer9 - 2) * (integer9 - 2);
                            final int integer14 = (integer5 / integer4 + integer10 - 64) * integer4;
                            final int integer15 = (integer6 / integer4 + integer11 - 64) * integer4;
                            final Multiset<MaterialColor> multiset21 = LinkedHashMultiset.create();
                            final WorldChunk worldChunk22 = world.getWorldChunk(new BlockPos(integer14, 0, integer15));
                            if (!worldChunk22.isEmpty()) {
                                final ChunkPos chunkPos23 = worldChunk22.getPos();
                                final int integer16 = integer14 & 0xF;
                                final int integer17 = integer15 & 0xF;
                                int integer18 = 0;
                                double double14 = 0.0;
                                if (world.dimension.isNether()) {
                                    int integer19 = integer14 + integer15 * 231871;
                                    integer19 = integer19 * integer19 * 31287121 + integer19 * 11;
                                    if ((integer19 >> 20 & 0x1) == 0x0) {
                                        multiset21.add(Blocks.j.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 10);
                                    }
                                    else {
                                        multiset21.add(Blocks.b.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 100);
                                    }
                                    double14 = 100.0;
                                }
                                else {
                                    final BlockPos.Mutable mutable29 = new BlockPos.Mutable();
                                    final BlockPos.Mutable mutable30 = new BlockPos.Mutable();
                                    for (int integer20 = 0; integer20 < integer4; ++integer20) {
                                        for (int integer21 = 0; integer21 < integer4; ++integer21) {
                                            int integer22 = worldChunk22.sampleHeightmap(Heightmap.Type.b, integer20 + integer16, integer21 + integer17) + 1;
                                            BlockState blockState34;
                                            if (integer22 > 1) {
                                                do {
                                                    --integer22;
                                                    mutable29.set(chunkPos23.getStartX() + integer20 + integer16, integer22, chunkPos23.getStartZ() + integer21 + integer17);
                                                    blockState34 = worldChunk22.getBlockState(mutable29);
                                                } while (blockState34.getTopMaterialColor(world, mutable29) == MaterialColor.AIR && integer22 > 0);
                                                if (integer22 > 0 && !blockState34.getFluidState().isEmpty()) {
                                                    int integer23 = integer22 - 1;
                                                    mutable30.set(mutable29);
                                                    BlockState blockState35;
                                                    do {
                                                        mutable30.setY(integer23--);
                                                        blockState35 = worldChunk22.getBlockState(mutable30);
                                                        ++integer18;
                                                    } while (integer23 > 0 && !blockState35.getFluidState().isEmpty());
                                                    blockState34 = this.getTopFaceBlockState(world, blockState34, mutable29);
                                                }
                                            }
                                            else {
                                                blockState34 = Blocks.z.getDefaultState();
                                            }
                                            state.removeBanner(world, chunkPos23.getStartX() + integer20 + integer16, chunkPos23.getStartZ() + integer21 + integer17);
                                            double14 += integer22 / (double)(integer4 * integer4);
                                            multiset21.add(blockState34.getTopMaterialColor(world, mutable29));
                                        }
                                    }
                                }
                                integer18 /= integer4 * integer4;
                                double double15 = (double14 - double13) * 4.0 / (integer4 + 4) + ((integer10 + integer11 & 0x1) - 0.5) * 0.4;
                                int integer20 = 1;
                                if (double15 > 0.6) {
                                    integer20 = 2;
                                }
                                if (double15 < -0.6) {
                                    integer20 = 0;
                                }
                                final MaterialColor materialColor32 = Iterables.<MaterialColor>getFirst(Multisets.<MaterialColor>copyHighestCountFirst(multiset21), MaterialColor.AIR);
                                if (materialColor32 == MaterialColor.WATER) {
                                    double15 = integer18 * 0.1 + (integer10 + integer11 & 0x1) * 0.2;
                                    integer20 = 1;
                                    if (double15 < 0.5) {
                                        integer20 = 2;
                                    }
                                    if (double15 > 0.9) {
                                        integer20 = 0;
                                    }
                                }
                                double13 = double14;
                                if (integer11 >= 0) {
                                    if (integer12 * integer12 + integer13 * integer13 < integer9 * integer9) {
                                        if (!boolean12 || (integer10 + integer11 & 0x1) != 0x0) {
                                            final byte byte33 = state.colors[integer10 + integer11 * 128];
                                            final byte byte34 = (byte)(materialColor32.id * 4 + integer20);
                                            if (byte33 != byte34) {
                                                state.colors[integer10 + integer11 * 128] = byte34;
                                                state.markDirty(integer10, integer11);
                                                boolean11 = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private BlockState getTopFaceBlockState(final World world, final BlockState state, final BlockPos pos) {
        final FluidState fluidState4 = state.getFluidState();
        if (!fluidState4.isEmpty() && !Block.isSolidFullSquare(state, world, pos, Direction.UP)) {
            return fluidState4.getBlockState();
        }
        return state;
    }
    
    private static boolean hasPositiveDepth(final Biome[] biomes, final int scale, final int x, final int z) {
        return biomes[x * scale + z * scale * 128 * scale].getDepth() >= 0.0f;
    }
    
    public static void fillExplorationMap(final World world, final ItemStack map) {
        final MapState mapState3 = getOrCreateMapState(map, world);
        if (mapState3 == null) {
            return;
        }
        if (world.dimension.getType() != mapState3.dimension) {
            return;
        }
        final int integer4 = 1 << mapState3.scale;
        final int integer5 = mapState3.xCenter;
        final int integer6 = mapState3.zCenter;
        final Biome[] arr7 = world.getChunkManager().getChunkGenerator().getBiomeSource().sampleBiomes((integer5 / integer4 - 64) * integer4, (integer6 / integer4 - 64) * integer4, 128 * integer4, 128 * integer4, false);
        for (int integer7 = 0; integer7 < 128; ++integer7) {
            for (int integer8 = 0; integer8 < 128; ++integer8) {
                if (integer7 > 0 && integer8 > 0 && integer7 < 127 && integer8 < 127) {
                    final Biome biome10 = arr7[integer7 * integer4 + integer8 * integer4 * 128 * integer4];
                    int integer9 = 8;
                    if (hasPositiveDepth(arr7, integer4, integer7 - 1, integer8 - 1)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7 - 1, integer8 + 1)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7 - 1, integer8)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7 + 1, integer8 - 1)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7 + 1, integer8 + 1)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7 + 1, integer8)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7, integer8 - 1)) {
                        --integer9;
                    }
                    if (hasPositiveDepth(arr7, integer4, integer7, integer8 + 1)) {
                        --integer9;
                    }
                    int integer10 = 3;
                    MaterialColor materialColor13 = MaterialColor.AIR;
                    if (biome10.getDepth() < 0.0f) {
                        materialColor13 = MaterialColor.ORANGE;
                        if (integer9 > 7 && integer8 % 2 == 0) {
                            integer10 = (integer7 + (int)(MathHelper.sin(integer8 + 0.0f) * 7.0f)) / 8 % 5;
                            if (integer10 == 3) {
                                integer10 = 1;
                            }
                            else if (integer10 == 4) {
                                integer10 = 0;
                            }
                        }
                        else if (integer9 > 7) {
                            materialColor13 = MaterialColor.AIR;
                        }
                        else if (integer9 > 5) {
                            integer10 = 1;
                        }
                        else if (integer9 > 3) {
                            integer10 = 0;
                        }
                        else if (integer9 > 1) {
                            integer10 = 0;
                        }
                    }
                    else if (integer9 > 0) {
                        materialColor13 = MaterialColor.BROWN;
                        if (integer9 > 3) {
                            integer10 = 1;
                        }
                        else {
                            integer10 = 3;
                        }
                    }
                    if (materialColor13 != MaterialColor.AIR) {
                        mapState3.colors[integer7 + integer8 * 128] = (byte)(materialColor13.id * 4 + integer10);
                        mapState3.markDirty(integer7, integer8);
                    }
                }
            }
        }
    }
    
    @Override
    public void onEntityTick(final ItemStack stack, final World world, final Entity entity, final int invSlot, final boolean selected) {
        if (world.isClient) {
            return;
        }
        final MapState mapState6 = getOrCreateMapState(stack, world);
        if (mapState6 == null) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            final PlayerEntity playerEntity7 = (PlayerEntity)entity;
            mapState6.update(playerEntity7, stack);
        }
        if (!mapState6.locked && (selected || (entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == stack))) {
            this.updateColors(world, entity, mapState6);
        }
    }
    
    @Nullable
    @Override
    public Packet<?> createMapPacket(final ItemStack stack, final World world, final PlayerEntity playerEntity) {
        return getOrCreateMapState(stack, world).getPlayerMarkerPacket(stack, world, playerEntity);
    }
    
    @Override
    public void onCrafted(final ItemStack stack, final World world, final PlayerEntity player) {
        final CompoundTag compoundTag4 = stack.getTag();
        if (compoundTag4 != null && compoundTag4.containsKey("map_scale_direction", 99)) {
            scale(stack, world, compoundTag4.getInt("map_scale_direction"));
            compoundTag4.remove("map_scale_direction");
        }
    }
    
    protected static void scale(final ItemStack map, final World world, final int integer) {
        final MapState mapState4 = getOrCreateMapState(map, world);
        if (mapState4 != null) {
            createMapState(map, world, mapState4.xCenter, mapState4.zCenter, MathHelper.clamp(mapState4.scale + integer, 0, 4), mapState4.showIcons, mapState4.unlimitedTracking, mapState4.dimension);
        }
    }
    
    @Nullable
    public static ItemStack createCopy(final World world, final ItemStack map) {
        final MapState mapState3 = getOrCreateMapState(map, world);
        if (mapState3 != null) {
            final ItemStack itemStack4 = map.copy();
            final MapState mapState4 = createMapState(itemStack4, world, 0, 0, mapState3.scale, mapState3.showIcons, mapState3.unlimitedTracking, mapState3.dimension);
            mapState4.copyFrom(mapState3);
            return itemStack4;
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        final MapState mapState5 = (world == null) ? null : getOrCreateMapState(stack, world);
        if (mapState5 != null && mapState5.locked) {
            tooltip.add(new TranslatableTextComponent("filled_map.locked", new Object[] { getMapId(stack) }).applyFormat(TextFormat.h));
        }
        if (options.isAdvanced()) {
            if (mapState5 != null) {
                tooltip.add(new TranslatableTextComponent("filled_map.id", new Object[] { getMapId(stack) }).applyFormat(TextFormat.h));
                tooltip.add(new TranslatableTextComponent("filled_map.scale", new Object[] { 1 << mapState5.scale }).applyFormat(TextFormat.h));
                tooltip.add(new TranslatableTextComponent("filled_map.level", new Object[] { mapState5.scale, 4 }).applyFormat(TextFormat.h));
            }
            else {
                tooltip.add(new TranslatableTextComponent("filled_map.unknown", new Object[0]).applyFormat(TextFormat.h));
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static int j(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getSubCompoundTag("display");
        if (compoundTag2 != null && compoundTag2.containsKey("MapColor", 99)) {
            final int integer3 = compoundTag2.getInt("MapColor");
            return 0xFF000000 | (integer3 & 0xFFFFFF);
        }
        return -12173266;
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final BlockState blockState2 = usageContext.getWorld().getBlockState(usageContext.getBlockPos());
        if (blockState2.matches(BlockTags.v)) {
            if (!usageContext.world.isClient) {
                final MapState mapState3 = getOrCreateMapState(usageContext.getItemStack(), usageContext.getWorld());
                mapState3.addBanner(usageContext.getWorld(), usageContext.getBlockPos());
            }
            return ActionResult.a;
        }
        return super.useOnBlock(usageContext);
    }
}
