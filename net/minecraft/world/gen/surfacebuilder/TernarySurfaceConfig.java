package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;

public class TernarySurfaceConfig implements SurfaceConfig
{
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underwaterMaterial;
    
    public TernarySurfaceConfig(final BlockState topMaterial, final BlockState underMaterial, final BlockState underwaterMaterial) {
        this.topMaterial = topMaterial;
        this.underMaterial = underMaterial;
        this.underwaterMaterial = underwaterMaterial;
    }
    
    @Override
    public BlockState getTopMaterial() {
        return this.topMaterial;
    }
    
    @Override
    public BlockState getUnderMaterial() {
        return this.underMaterial;
    }
    
    public BlockState getUnderwaterMaterial() {
        return this.underwaterMaterial;
    }
    
    public static TernarySurfaceConfig deserialize(final Dynamic<?> dynamic) {
        final BlockState blockState2 = dynamic.get("top_material").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        final BlockState blockState3 = dynamic.get("under_material").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        final BlockState blockState4 = dynamic.get("underwater_material").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new TernarySurfaceConfig(blockState2, blockState3, blockState4);
    }
}
