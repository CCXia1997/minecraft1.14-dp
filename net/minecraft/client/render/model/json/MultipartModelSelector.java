package net.minecraft.client.render.model.json;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface MultipartModelSelector
{
    public static final MultipartModelSelector TRUE = stateFactory -> blockState -> 1;
    public static final MultipartModelSelector FALSE = stateFactory -> blockState -> 0;
    
    Predicate<BlockState> getPredicate(final StateFactory<Block, BlockState> arg1);
}
