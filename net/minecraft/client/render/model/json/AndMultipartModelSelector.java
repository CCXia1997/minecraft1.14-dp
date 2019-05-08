package net.minecraft.client.render.model.json;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AndMultipartModelSelector implements MultipartModelSelector
{
    private final Iterable<? extends MultipartModelSelector> selectors;
    
    public AndMultipartModelSelector(final Iterable<? extends MultipartModelSelector> selectors) {
        this.selectors = selectors;
    }
    
    @Override
    public Predicate<BlockState> getPredicate(final StateFactory<Block, BlockState> stateFactory) {
        final List<Predicate<BlockState>> list2 = Streams.stream(this.selectors).map(multipartModelSelector -> multipartModelSelector.getPredicate(stateFactory)).collect(Collectors.toList());
        return blockState -> list2.stream().allMatch(predicate -> predicate.test(blockState));
    }
}
