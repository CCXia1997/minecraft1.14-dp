package net.minecraft.structure;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.world.loot.LootTables;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import java.util.Random;

public class JungleTempleGenerator extends StructurePieceWithDimensions
{
    private boolean placedMainChest;
    private boolean placedHiddenChest;
    private boolean placedTrap1;
    private boolean placedTrap2;
    private static final CobblestoneRandomizer COBBLESTONE_RANDOMIZER;
    
    public JungleTempleGenerator(final Random random, final int x, final int z) {
        super(StructurePieceType.JUNGLE_TEMPLE, random, x, 64, z, 12, 10, 15);
    }
    
    public JungleTempleGenerator(final StructureManager manager, final CompoundTag tag) {
        super(StructurePieceType.JUNGLE_TEMPLE, tag);
        this.placedMainChest = tag.getBoolean("placedMainChest");
        this.placedHiddenChest = tag.getBoolean("placedHiddenChest");
        this.placedTrap1 = tag.getBoolean("placedTrap1");
        this.placedTrap2 = tag.getBoolean("placedTrap2");
    }
    
    @Override
    protected void toNbt(final CompoundTag tag) {
        super.toNbt(tag);
        tag.putBoolean("placedMainChest", this.placedMainChest);
        tag.putBoolean("placedHiddenChest", this.placedHiddenChest);
        tag.putBoolean("placedTrap1", this.placedTrap1);
        tag.putBoolean("placedTrap2", this.placedTrap2);
    }
    
    @Override
    public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
        if (!this.a(world, boundingBox, 0)) {
            return false;
        }
        this.fillWithOutline(world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 2, 1, 2, 9, 2, 2, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 2, 1, 12, 9, 2, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 2, 1, 3, 2, 2, 11, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 9, 1, 3, 9, 2, 11, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 1, 3, 1, 10, 6, 1, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 1, 3, 13, 10, 6, 13, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 1, 3, 2, 1, 6, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 10, 3, 2, 10, 6, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 2, 3, 2, 9, 3, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 2, 6, 2, 9, 6, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 3, 7, 3, 8, 7, 11, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 4, 8, 4, 7, 8, 10, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fill(world, boundingBox, 3, 1, 3, 8, 2, 11);
        this.fill(world, boundingBox, 4, 3, 6, 7, 3, 9);
        this.fill(world, boundingBox, 2, 4, 2, 9, 5, 12);
        this.fill(world, boundingBox, 4, 6, 5, 7, 6, 9);
        this.fill(world, boundingBox, 5, 7, 6, 6, 7, 8);
        this.fill(world, boundingBox, 5, 1, 2, 6, 2, 2);
        this.fill(world, boundingBox, 5, 2, 12, 6, 2, 12);
        this.fill(world, boundingBox, 5, 5, 1, 6, 5, 1);
        this.fill(world, boundingBox, 5, 5, 13, 6, 5, 13);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 1, 5, 5, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 10, 5, 5, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 1, 5, 9, boundingBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 10, 5, 9, boundingBox);
        for (int integer5 = 0; integer5 <= 14; integer5 += 14) {
            this.fillWithOutline(world, boundingBox, 2, 4, integer5, 2, 5, integer5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(world, boundingBox, 4, 4, integer5, 4, 5, integer5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(world, boundingBox, 7, 4, integer5, 7, 5, integer5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(world, boundingBox, 9, 4, integer5, 9, 5, integer5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        }
        this.fillWithOutline(world, boundingBox, 5, 6, 0, 6, 6, 0, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        for (int integer5 = 0; integer5 <= 11; integer5 += 11) {
            for (int integer6 = 2; integer6 <= 12; integer6 += 2) {
                this.fillWithOutline(world, boundingBox, integer5, 4, integer6, integer5, 5, integer6, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
            }
            this.fillWithOutline(world, boundingBox, integer5, 6, 5, integer5, 6, 5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(world, boundingBox, integer5, 6, 9, integer5, 6, 9, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        }
        this.fillWithOutline(world, boundingBox, 2, 7, 2, 2, 9, 2, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 9, 7, 2, 9, 9, 2, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 2, 7, 12, 2, 9, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 9, 7, 12, 9, 9, 12, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 4, 9, 4, 4, 9, 4, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 7, 9, 4, 7, 9, 4, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 4, 9, 10, 4, 9, 10, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 7, 9, 10, 7, 9, 10, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 5, 9, 7, 6, 9, 7, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        final BlockState blockState5 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.EAST);
        final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.WEST);
        final BlockState blockState7 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.SOUTH);
        final BlockState blockState8 = ((AbstractPropertyContainer<O, BlockState>)Blocks.cg.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, Direction.NORTH);
        this.addBlock(world, blockState8, 5, 9, 6, boundingBox);
        this.addBlock(world, blockState8, 6, 9, 6, boundingBox);
        this.addBlock(world, blockState7, 5, 9, 8, boundingBox);
        this.addBlock(world, blockState7, 6, 9, 8, boundingBox);
        this.addBlock(world, blockState8, 4, 0, 0, boundingBox);
        this.addBlock(world, blockState8, 5, 0, 0, boundingBox);
        this.addBlock(world, blockState8, 6, 0, 0, boundingBox);
        this.addBlock(world, blockState8, 7, 0, 0, boundingBox);
        this.addBlock(world, blockState8, 4, 1, 8, boundingBox);
        this.addBlock(world, blockState8, 4, 2, 9, boundingBox);
        this.addBlock(world, blockState8, 4, 3, 10, boundingBox);
        this.addBlock(world, blockState8, 7, 1, 8, boundingBox);
        this.addBlock(world, blockState8, 7, 2, 9, boundingBox);
        this.addBlock(world, blockState8, 7, 3, 10, boundingBox);
        this.fillWithOutline(world, boundingBox, 4, 1, 9, 4, 1, 9, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 7, 1, 9, 7, 1, 9, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 4, 1, 10, 7, 2, 10, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 5, 4, 5, 6, 4, 5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.addBlock(world, blockState5, 4, 4, 5, boundingBox);
        this.addBlock(world, blockState6, 7, 4, 5, boundingBox);
        for (int integer7 = 0; integer7 < 4; ++integer7) {
            this.addBlock(world, blockState7, 5, 0 - integer7, 6 + integer7, boundingBox);
            this.addBlock(world, blockState7, 6, 0 - integer7, 6 + integer7, boundingBox);
            this.fill(world, boundingBox, 5, 0 - integer7, 7 + integer7, 6, 0 - integer7, 9 + integer7);
        }
        this.fill(world, boundingBox, 1, -3, 12, 10, -1, 13);
        this.fill(world, boundingBox, 1, -3, 1, 3, -1, 13);
        this.fill(world, boundingBox, 1, -3, 1, 9, -1, 5);
        for (int integer7 = 1; integer7 <= 13; integer7 += 2) {
            this.fillWithOutline(world, boundingBox, 1, -3, integer7, 1, -2, integer7, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        }
        for (int integer7 = 2; integer7 <= 12; integer7 += 2) {
            this.fillWithOutline(world, boundingBox, 1, -1, integer7, 3, -1, integer7, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        }
        this.fillWithOutline(world, boundingBox, 2, -2, 1, 5, -2, 1, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 7, -2, 1, 9, -2, 1, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 6, -3, 1, 6, -3, 1, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 6, -1, 1, 6, -1, 1, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.ed.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.FACING, Direction.EAST)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, true), 1, -3, 8, boundingBox);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.ed.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.FACING, Direction.WEST)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, true), 4, -3, 8, boundingBox);
        this.addBlock(world, ((((AbstractPropertyContainer<O, BlockState>)Blocks.ee.getDefaultState()).with((Property<Comparable>)TripwireBlock.EAST, true)).with((Property<Comparable>)TripwireBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.ATTACHED, true), 2, -3, 8, boundingBox);
        this.addBlock(world, ((((AbstractPropertyContainer<O, BlockState>)Blocks.ee.getDefaultState()).with((Property<Comparable>)TripwireBlock.EAST, true)).with((Property<Comparable>)TripwireBlock.WEST, true)).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.ATTACHED, true), 3, -3, 8, boundingBox);
        final BlockState blockState9 = (((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.b)).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.b);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.b), 5, -3, 7, boundingBox);
        this.addBlock(world, blockState9, 5, -3, 6, boundingBox);
        this.addBlock(world, blockState9, 5, -3, 5, boundingBox);
        this.addBlock(world, blockState9, 5, -3, 4, boundingBox);
        this.addBlock(world, blockState9, 5, -3, 3, boundingBox);
        this.addBlock(world, blockState9, 5, -3, 2, boundingBox);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.b)).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.b), 5, -3, 1, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.b), 4, -3, 1, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 3, -3, 1, boundingBox);
        if (!this.placedTrap1) {
            this.placedTrap1 = this.addDispenser(world, boundingBox, random, 3, -2, 1, Direction.NORTH, LootTables.DISPENSER_JUNGLE_TEMPLE);
        }
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)VineBlock.SOUTH, true), 3, -2, 2, boundingBox);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.ed.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, true), 7, -3, 1, boundingBox);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.ed.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.FACING, Direction.SOUTH)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, true), 7, -3, 5, boundingBox);
        this.addBlock(world, ((((AbstractPropertyContainer<O, BlockState>)Blocks.ee.getDefaultState()).with((Property<Comparable>)TripwireBlock.NORTH, true)).with((Property<Comparable>)TripwireBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.ATTACHED, true), 7, -3, 2, boundingBox);
        this.addBlock(world, ((((AbstractPropertyContainer<O, BlockState>)Blocks.ee.getDefaultState()).with((Property<Comparable>)TripwireBlock.NORTH, true)).with((Property<Comparable>)TripwireBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.ATTACHED, true), 7, -3, 3, boundingBox);
        this.addBlock(world, ((((AbstractPropertyContainer<O, BlockState>)Blocks.ee.getDefaultState()).with((Property<Comparable>)TripwireBlock.NORTH, true)).with((Property<Comparable>)TripwireBlock.SOUTH, true)).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.ATTACHED, true), 7, -3, 4, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.b), 8, -3, 6, boundingBox);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.b)).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.b), 9, -3, 6, boundingBox);
        this.addBlock(world, (((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.b)).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.a), 9, -3, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 9, -3, 4, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.b), 9, -2, 4, boundingBox);
        if (!this.placedTrap2) {
            this.placedTrap2 = this.addDispenser(world, boundingBox, random, 9, -2, 3, Direction.WEST, LootTables.DISPENSER_JUNGLE_TEMPLE);
        }
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)VineBlock.EAST, true), 8, -1, 3, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)VineBlock.EAST, true), 8, -2, 3, boundingBox);
        if (!this.placedMainChest) {
            this.placedMainChest = this.addChest(world, boundingBox, random, 8, -3, 3, LootTables.CHEST_JUNGLE_TEMPLE);
        }
        this.addBlock(world, Blocks.bI.getDefaultState(), 9, -3, 2, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 8, -3, 1, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 4, -3, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 5, -2, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 5, -1, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 6, -3, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 7, -2, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 7, -1, 5, boundingBox);
        this.addBlock(world, Blocks.bI.getDefaultState(), 8, -3, 5, boundingBox);
        this.fillWithOutline(world, boundingBox, 9, -1, 1, 9, -1, 5, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fill(world, boundingBox, 8, -3, 8, 10, -1, 10);
        this.addBlock(world, Blocks.dq.getDefaultState(), 8, -2, 11, boundingBox);
        this.addBlock(world, Blocks.dq.getDefaultState(), 9, -2, 11, boundingBox);
        this.addBlock(world, Blocks.dq.getDefaultState(), 10, -2, 11, boundingBox);
        final BlockState blockState10 = (((AbstractPropertyContainer<O, BlockState>)Blocks.cn.getDefaultState()).with((Property<Comparable>)LeverBlock.FACING, Direction.NORTH)).<WallMountLocation, WallMountLocation>with(LeverBlock.FACE, WallMountLocation.b);
        this.addBlock(world, blockState10, 8, -2, 12, boundingBox);
        this.addBlock(world, blockState10, 9, -2, 12, boundingBox);
        this.addBlock(world, blockState10, 10, -2, 12, boundingBox);
        this.fillWithOutline(world, boundingBox, 8, -3, 8, 8, -3, 10, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(world, boundingBox, 10, -3, 8, 10, -3, 10, false, random, JungleTempleGenerator.COBBLESTONE_RANDOMIZER);
        this.addBlock(world, Blocks.bI.getDefaultState(), 10, -2, 9, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.b), 8, -2, 9, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.bQ.getDefaultState()).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.b), 8, -2, 10, boundingBox);
        this.addBlock(world, Blocks.bQ.getDefaultState(), 10, -1, 9, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.aO.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)PistonBlock.FACING, Direction.UP), 9, -2, 8, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.aO.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)PistonBlock.FACING, Direction.WEST), 10, -2, 8, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.aO.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)PistonBlock.FACING, Direction.WEST), 10, -1, 8, boundingBox);
        this.addBlock(world, ((AbstractPropertyContainer<O, BlockState>)Blocks.cQ.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, boundingBox);
        if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.addChest(world, boundingBox, random, 9, -3, 10, LootTables.CHEST_JUNGLE_TEMPLE);
        }
        return true;
    }
    
    static {
        COBBLESTONE_RANDOMIZER = new CobblestoneRandomizer();
    }
    
    static class CobblestoneRandomizer extends BlockRandomizer
    {
        private CobblestoneRandomizer() {
        }
        
        @Override
        public void setBlock(final Random random, final int x, final int y, final int z, final boolean placeBlock) {
            if (random.nextFloat() < 0.4f) {
                this.block = Blocks.m.getDefaultState();
            }
            else {
                this.block = Blocks.bI.getDefaultState();
            }
        }
    }
}
