package net.minecraft.structure.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.BlockArgumentParser;
import com.mojang.brigadier.StringReader;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class JigsawReplacementStructureProcessor extends StructureProcessor
{
    public static final JigsawReplacementStructureProcessor INSTANCE;
    
    private JigsawReplacementStructureProcessor() {
    }
    
    @Nullable
    @Override
    public Structure.StructureBlockInfo process(final ViewableWorld world, final BlockPos pos, final Structure.StructureBlockInfo structureBlockInfo3, final Structure.StructureBlockInfo structureBlockInfo4, final StructurePlacementData placementData) {
        final Block block6 = structureBlockInfo4.state.getBlock();
        if (block6 != Blocks.lY) {
            return structureBlockInfo4;
        }
        final String string7 = structureBlockInfo4.tag.getString("final_state");
        final BlockArgumentParser blockArgumentParser8 = new BlockArgumentParser(new StringReader(string7), false);
        try {
            blockArgumentParser8.parse(true);
        }
        catch (CommandSyntaxException commandSyntaxException9) {
            throw new RuntimeException((Throwable)commandSyntaxException9);
        }
        if (blockArgumentParser8.getBlockState().getBlock() == Blocks.iF) {
            return null;
        }
        return new Structure.StructureBlockInfo(structureBlockInfo4.pos, blockArgumentParser8.getBlockState(), null);
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.e;
    }
    
    @Override
    protected <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    static {
        INSTANCE = new JigsawReplacementStructureProcessor();
    }
}
