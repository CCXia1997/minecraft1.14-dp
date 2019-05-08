package net.minecraft.world.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Blocks;
import net.minecraft.util.TagHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class ChunkSection
{
    private static final Palette<BlockState> palette;
    private final int yOffset;
    private short nonEmptyBlockCount;
    private short randomTickableBlockCount;
    private short nonEmptyFluidCount;
    private final PalettedContainer<BlockState> container;
    
    public ChunkSection(final int yOffset) {
        this(yOffset, (short)0, (short)0, (short)0);
    }
    
    public ChunkSection(final int yOffset, final short nonEmptyBlockCount, final short randomTickableBlockCount, final short nonEmptyFluidCount) {
        this.yOffset = yOffset;
        this.nonEmptyBlockCount = nonEmptyBlockCount;
        this.randomTickableBlockCount = randomTickableBlockCount;
        this.nonEmptyFluidCount = nonEmptyFluidCount;
        this.container = new PalettedContainer<BlockState>(ChunkSection.palette, Block.STATE_IDS, TagHelper::deserializeBlockState, TagHelper::serializeBlockState, Blocks.AIR.getDefaultState());
    }
    
    public BlockState getBlockState(final int x, final int y, final int z) {
        return this.container.get(x, y, z);
    }
    
    public FluidState getFluidState(final int x, final int y, final int z) {
        return this.container.get(x, y, z).getFluidState();
    }
    
    public void lock() {
        this.container.lock();
    }
    
    public void unlock() {
        this.container.unlock();
    }
    
    public BlockState setBlockState(final int x, final int y, final int z, final BlockState state) {
        return this.setBlockState(x, y, z, state, true);
    }
    
    public BlockState setBlockState(final int x, final int y, final int z, final BlockState state, final boolean lock) {
        BlockState blockState6;
        if (lock) {
            blockState6 = this.container.setSync(x, y, z, state);
        }
        else {
            blockState6 = this.container.set(x, y, z, state);
        }
        final FluidState fluidState7 = blockState6.getFluidState();
        final FluidState fluidState8 = state.getFluidState();
        if (!blockState6.isAir()) {
            --this.nonEmptyBlockCount;
            if (blockState6.hasRandomTicks()) {
                --this.randomTickableBlockCount;
            }
        }
        if (!fluidState7.isEmpty()) {
            --this.nonEmptyFluidCount;
        }
        if (!state.isAir()) {
            ++this.nonEmptyBlockCount;
            if (state.hasRandomTicks()) {
                ++this.randomTickableBlockCount;
            }
        }
        if (!fluidState8.isEmpty()) {
            ++this.nonEmptyFluidCount;
        }
        return blockState6;
    }
    
    public boolean isEmpty() {
        return this.nonEmptyBlockCount == 0;
    }
    
    public static boolean isEmpty(@Nullable final ChunkSection section) {
        return section == WorldChunk.EMPTY_SECTION || section.isEmpty();
    }
    
    public boolean hasRandomTicks() {
        return this.hasRandomBlockTicks() || this.hasRandomFluidTicks();
    }
    
    public boolean hasRandomBlockTicks() {
        return this.randomTickableBlockCount > 0;
    }
    
    public boolean hasRandomFluidTicks() {
        return this.nonEmptyFluidCount > 0;
    }
    
    public int getYOffset() {
        return this.yOffset;
    }
    
    public void calculateCounts() {
        this.nonEmptyBlockCount = 0;
        this.randomTickableBlockCount = 0;
        this.nonEmptyFluidCount = 0;
        for (int integer1 = 0; integer1 < 16; ++integer1) {
            for (int integer2 = 0; integer2 < 16; ++integer2) {
                for (int integer3 = 0; integer3 < 16; ++integer3) {
                    final BlockState blockState4 = this.getBlockState(integer1, integer2, integer3);
                    final FluidState fluidState5 = this.getFluidState(integer1, integer2, integer3);
                    if (!blockState4.isAir()) {
                        ++this.nonEmptyBlockCount;
                        if (blockState4.hasRandomTicks()) {
                            ++this.randomTickableBlockCount;
                        }
                    }
                    if (!fluidState5.isEmpty()) {
                        ++this.nonEmptyBlockCount;
                        if (fluidState5.hasRandomTicks()) {
                            ++this.nonEmptyFluidCount;
                        }
                    }
                }
            }
        }
    }
    
    public PalettedContainer<BlockState> getContainer() {
        return this.container;
    }
    
    @Environment(EnvType.CLIENT)
    public void fromPacket(final PacketByteBuf packetByteBuf) {
        this.nonEmptyBlockCount = packetByteBuf.readShort();
        this.container.fromPacket(packetByteBuf);
    }
    
    public void toPacket(final PacketByteBuf packetByteBuf) {
        packetByteBuf.writeShort(this.nonEmptyBlockCount);
        this.container.toPacket(packetByteBuf);
    }
    
    public int getPacketSize() {
        return 2 + this.container.getPacketSize();
    }
    
    public boolean a(final BlockState blockState) {
        return this.container.a(blockState);
    }
    
    static {
        palette = new IdListPalette<BlockState>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
    }
}
