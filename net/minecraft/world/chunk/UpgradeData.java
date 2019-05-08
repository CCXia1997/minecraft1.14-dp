package net.minecraft.world.chunk;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.state.property.Properties;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.Blocks;
import com.google.common.collect.Sets;
import java.util.IdentityHashMap;
import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.Tag;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import java.util.Set;
import net.minecraft.block.Block;
import java.util.Map;
import java.util.EnumSet;
import net.minecraft.util.EightWayDirection;
import org.apache.logging.log4j.Logger;

public class UpgradeData
{
    private static final Logger LOGGER;
    public static final UpgradeData NO_UPGRADE_DATA;
    private static final EightWayDirection[] c;
    private final EnumSet<EightWayDirection> sides;
    private final int[][] indices;
    private static final Map<Block, a> f;
    private static final Set<a> g;
    
    private UpgradeData() {
        this.sides = EnumSet.<EightWayDirection>noneOf(EightWayDirection.class);
        this.indices = new int[16][];
    }
    
    public UpgradeData(final CompoundTag tag) {
        this();
        if (tag.containsKey("Indices", 10)) {
            final CompoundTag compoundTag2 = tag.getCompound("Indices");
            for (int integer3 = 0; integer3 < this.indices.length; ++integer3) {
                final String string4 = String.valueOf(integer3);
                if (compoundTag2.containsKey(string4, 11)) {
                    this.indices[integer3] = compoundTag2.getIntArray(string4);
                }
            }
        }
        final int integer4 = tag.getInt("Sides");
        for (final EightWayDirection eightWayDirection6 : EightWayDirection.values()) {
            if ((integer4 & 1 << eightWayDirection6.ordinal()) != 0x0) {
                this.sides.add(eightWayDirection6);
            }
        }
    }
    
    public void a(final WorldChunk worldChunk) {
        this.b(worldChunk);
        for (final EightWayDirection eightWayDirection5 : UpgradeData.c) {
            a(worldChunk, eightWayDirection5);
        }
        final World world2 = worldChunk.getWorld();
        UpgradeData.g.forEach(a -> a.a(world2));
    }
    
    private static void a(final WorldChunk worldChunk, final EightWayDirection eightWayDirection) {
        final World world3 = worldChunk.getWorld();
        if (!worldChunk.getUpgradeData().sides.remove(eightWayDirection)) {
            return;
        }
        final Set<Direction> set4 = eightWayDirection.getDirections();
        final int integer5 = 0;
        final int integer6 = 15;
        final boolean boolean7 = set4.contains(Direction.EAST);
        final boolean boolean8 = set4.contains(Direction.WEST);
        final boolean boolean9 = set4.contains(Direction.SOUTH);
        final boolean boolean10 = set4.contains(Direction.NORTH);
        final boolean boolean11 = set4.size() == 1;
        final ChunkPos chunkPos12 = worldChunk.getPos();
        final int integer7 = chunkPos12.getStartX() + ((boolean11 && (boolean10 || boolean9)) ? 1 : (boolean8 ? 0 : 15));
        final int integer8 = chunkPos12.getStartX() + ((boolean11 && (boolean10 || boolean9)) ? 14 : (boolean8 ? 0 : 15));
        final int integer9 = chunkPos12.getStartZ() + ((boolean11 && (boolean7 || boolean8)) ? 1 : (boolean10 ? 0 : 15));
        final int integer10 = chunkPos12.getStartZ() + ((boolean11 && (boolean7 || boolean8)) ? 14 : (boolean10 ? 0 : 15));
        final Direction[] arr17 = Direction.values();
        final BlockPos.Mutable mutable18 = new BlockPos.Mutable();
        for (final BlockPos blockPos20 : BlockPos.iterate(integer7, 0, integer9, integer8, world3.getHeight() - 1, integer10)) {
            BlockState blockState22;
            final BlockState blockState21 = blockState22 = world3.getBlockState(blockPos20);
            for (final Direction direction26 : arr17) {
                mutable18.set(blockPos20).setOffset(direction26);
                blockState22 = a(blockState22, direction26, world3, blockPos20, mutable18);
            }
            Block.replaceBlock(blockState21, blockState22, world3, blockPos20, 18);
        }
    }
    
    private static BlockState a(final BlockState blockState, final Direction direction, final IWorld iWorld, final BlockPos blockPos4, final BlockPos blockPos5) {
        return UpgradeData.f.getOrDefault(blockState.getBlock(), b.b).a(blockState, direction, iWorld.getBlockState(blockPos5), iWorld, blockPos4, blockPos5);
    }
    
    private void b(final WorldChunk worldChunk) {
        try (final BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get();
             final BlockPos.PooledMutable pooledMutable3 = BlockPos.PooledMutable.get()) {
            final ChunkPos chunkPos6 = worldChunk.getPos();
            final IWorld iWorld7 = worldChunk.getWorld();
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                final ChunkSection chunkSection9 = worldChunk.getSectionArray()[integer8];
                final int[] arr10 = this.indices[integer8];
                this.indices[integer8] = null;
                if (chunkSection9 != null && arr10 != null) {
                    if (arr10.length > 0) {
                        final Direction[] arr11 = Direction.values();
                        final PalettedContainer<BlockState> palettedContainer12 = chunkSection9.getContainer();
                        for (final int integer9 : arr10) {
                            final int integer10 = integer9 & 0xF;
                            final int integer11 = integer9 >> 8 & 0xF;
                            final int integer12 = integer9 >> 4 & 0xF;
                            pooledMutable2.set(chunkPos6.getStartX() + integer10, (integer8 << 4) + integer11, chunkPos6.getStartZ() + integer12);
                            BlockState blockState21;
                            final BlockState blockState20 = blockState21 = palettedContainer12.get(integer9);
                            for (final Direction direction25 : arr11) {
                                pooledMutable3.set((Vec3i)pooledMutable2).setOffset(direction25);
                                if (pooledMutable2.getX() >> 4 == chunkPos6.x) {
                                    if (pooledMutable2.getZ() >> 4 == chunkPos6.z) {
                                        blockState21 = a(blockState21, direction25, iWorld7, pooledMutable2, pooledMutable3);
                                    }
                                }
                            }
                            Block.replaceBlock(blockState20, blockState21, iWorld7, pooledMutable2, 18);
                        }
                    }
                }
            }
            for (int integer8 = 0; integer8 < this.indices.length; ++integer8) {
                if (this.indices[integer8] != null) {
                    UpgradeData.LOGGER.warn("Discarding update data for section {} for chunk ({} {})", integer8, chunkPos6.x, chunkPos6.z);
                }
                this.indices[integer8] = null;
            }
        }
    }
    
    public boolean a() {
        for (final int[] arr4 : this.indices) {
            if (arr4 != null) {
                return false;
            }
        }
        return this.sides.isEmpty();
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        final CompoundTag compoundTag2 = new CompoundTag();
        for (int integer3 = 0; integer3 < this.indices.length; ++integer3) {
            final String string4 = String.valueOf(integer3);
            if (this.indices[integer3] != null && this.indices[integer3].length != 0) {
                compoundTag2.putIntArray(string4, this.indices[integer3]);
            }
        }
        if (!compoundTag2.isEmpty()) {
            compoundTag1.put("Indices", compoundTag2);
        }
        int integer3 = 0;
        for (final EightWayDirection eightWayDirection5 : this.sides) {
            integer3 |= 1 << eightWayDirection5.ordinal();
        }
        compoundTag1.putByte("Sides", (byte)integer3);
        return compoundTag1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        NO_UPGRADE_DATA = new UpgradeData();
        c = EightWayDirection.values();
        f = new IdentityHashMap<Block, a>();
        g = Sets.newHashSet();
    }
    
    public interface a
    {
        BlockState a(final BlockState arg1, final Direction arg2, final BlockState arg3, final IWorld arg4, final BlockPos arg5, final BlockPos arg6);
        
        default void a(final IWorld iWorld) {
        }
    }
    
    enum b implements a
    {
        a(new Block[] { Blocks.iG, Blocks.cM, Blocks.jE, Blocks.jF, Blocks.jG, Blocks.jH, Blocks.jI, Blocks.jJ, Blocks.jK, Blocks.jL, Blocks.jM, Blocks.jN, Blocks.jO, Blocks.jP, Blocks.jQ, Blocks.jR, Blocks.jS, Blocks.jT, Blocks.fg, Blocks.fh, Blocks.fi, Blocks.dX, Blocks.E, Blocks.C, Blocks.D, Blocks.bX, Blocks.bY, Blocks.bZ, Blocks.ca, Blocks.cb, Blocks.cc, Blocks.ch, Blocks.ci, Blocks.cj, Blocks.ck, Blocks.cl, Blocks.cm }) {
            @Override
            public BlockState a(final BlockState blockState1, final Direction direction, final BlockState blockState3, final IWorld iWorld, final BlockPos blockPos5, final BlockPos blockPos6) {
                return blockState1;
            }
        }, 
        b(new Block[0]) {
            @Override
            public BlockState a(final BlockState blockState1, final Direction direction, final BlockState blockState3, final IWorld iWorld, final BlockPos blockPos5, final BlockPos blockPos6) {
                return blockState1.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos6), iWorld, blockPos5, blockPos6);
            }
        }, 
        c(new Block[] { Blocks.bP, Blocks.fj }) {
            @Override
            public BlockState a(final BlockState blockState1, final Direction direction, final BlockState blockState3, final IWorld iWorld, final BlockPos blockPos5, final BlockPos blockPos6) {
                if (blockState3.getBlock() == blockState1.getBlock() && direction.getAxis().isHorizontal() && blockState1.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.a && blockState3.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.a) {
                    final Direction direction2 = blockState1.<Direction>get((Property<Direction>)ChestBlock.FACING);
                    if (direction.getAxis() != direction2.getAxis() && direction2 == blockState3.<Direction>get((Property<Direction>)ChestBlock.FACING)) {
                        final ChestType chestType8 = (direction == direction2.rotateYClockwise()) ? ChestType.b : ChestType.c;
                        iWorld.setBlockState(blockPos6, ((AbstractPropertyContainer<O, BlockState>)blockState3).<ChestType, ChestType>with(ChestBlock.CHEST_TYPE, chestType8.getOpposite()), 18);
                        if (direction2 == Direction.NORTH || direction2 == Direction.EAST) {
                            final BlockEntity blockEntity9 = iWorld.getBlockEntity(blockPos5);
                            final BlockEntity blockEntity10 = iWorld.getBlockEntity(blockPos6);
                            if (blockEntity9 instanceof ChestBlockEntity && blockEntity10 instanceof ChestBlockEntity) {
                                ChestBlockEntity.copyInventory((ChestBlockEntity)blockEntity9, (ChestBlockEntity)blockEntity10);
                            }
                        }
                        return ((AbstractPropertyContainer<O, BlockState>)blockState1).<ChestType, ChestType>with(ChestBlock.CHEST_TYPE, chestType8);
                    }
                }
                return blockState1;
            }
        }, 
        d(true, new Block[] { Blocks.ak, Blocks.ai, Blocks.al, Blocks.aj, Blocks.ag, Blocks.ah }) {
            private final ThreadLocal<List<ObjectSet<BlockPos>>> g;
            
            {
                this.g = ThreadLocal.<List<ObjectSet<BlockPos>>>withInitial(() -> Lists.newArrayListWithCapacity(7));
            }
            
            @Override
            public BlockState a(final BlockState blockState1, final Direction direction, final BlockState blockState3, final IWorld iWorld, final BlockPos blockPos5, final BlockPos blockPos6) {
                final BlockState blockState4 = blockState1.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos6), iWorld, blockPos5, blockPos6);
                if (blockState1 != blockState4) {
                    final int integer8 = blockState4.<Integer>get((Property<Integer>)Properties.DISTANCE_1_7);
                    final List<ObjectSet<BlockPos>> list9 = this.g.get();
                    if (list9.isEmpty()) {
                        for (int integer9 = 0; integer9 < 7; ++integer9) {
                            list9.add((ObjectSet<BlockPos>)new ObjectOpenHashSet());
                        }
                    }
                    list9.get(integer8).add(blockPos5.toImmutable());
                }
                return blockState1;
            }
            
            @Override
            public void a(final IWorld iWorld) {
                final BlockPos.Mutable mutable2 = new BlockPos.Mutable();
                final List<ObjectSet<BlockPos>> list3 = this.g.get();
                for (int integer4 = 2; integer4 < list3.size(); ++integer4) {
                    final int integer5 = integer4 - 1;
                    final ObjectSet<BlockPos> objectSet6 = list3.get(integer5);
                    final ObjectSet<BlockPos> objectSet7 = list3.get(integer4);
                    for (final BlockPos blockPos9 : objectSet6) {
                        final BlockState blockState10 = iWorld.getBlockState(blockPos9);
                        if (blockState10.<Integer>get((Property<Integer>)Properties.DISTANCE_1_7) < integer5) {
                            continue;
                        }
                        iWorld.setBlockState(blockPos9, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Integer>with((Property<Comparable>)Properties.DISTANCE_1_7, integer5), 18);
                        if (integer4 == 7) {
                            continue;
                        }
                        for (final Direction direction14 : UpgradeData$b$4.f) {
                            mutable2.set(blockPos9).setOffset(direction14);
                            final BlockState blockState11 = iWorld.getBlockState(mutable2);
                            if (blockState11.<Comparable>contains((Property<Comparable>)Properties.DISTANCE_1_7) && blockState10.<Integer>get((Property<Integer>)Properties.DISTANCE_1_7) > integer4) {
                                objectSet7.add(mutable2.toImmutable());
                            }
                        }
                    }
                }
                list3.clear();
            }
        }, 
        e(new Block[] { Blocks.dG, Blocks.dF }) {
            @Override
            public BlockState a(final BlockState blockState1, final Direction direction, final BlockState blockState3, final IWorld iWorld, final BlockPos blockPos5, final BlockPos blockPos6) {
                if (blockState1.<Integer>get((Property<Integer>)StemBlock.AGE) == 7) {
                    final GourdBlock gourdBlock7 = ((StemBlock)blockState1.getBlock()).getGourdBlock();
                    if (blockState3.getBlock() == gourdBlock7) {
                        return ((AbstractPropertyContainer<O, BlockState>)gourdBlock7.getAttachedStem().getDefaultState()).<Comparable, Direction>with((Property<Comparable>)HorizontalFacingBlock.FACING, direction);
                    }
                }
                return blockState1;
            }
        };
        
        public static final Direction[] f;
        
        private b(final Block[] arr) {
            this(false, arr);
        }
        
        private b(final boolean boolean1, final Block[] arr) {
            for (final Block block6 : arr) {
                UpgradeData.f.put(block6, this);
            }
            if (boolean1) {
                UpgradeData.g.add(this);
            }
        }
        
        static {
            f = Direction.values();
        }
    }
}
