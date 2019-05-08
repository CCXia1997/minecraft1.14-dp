package net.minecraft.client.render.model.json;

import com.google.common.base.MoreObjects;
import java.util.Optional;
import net.minecraft.state.property.Property;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import com.google.common.base.Splitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SimpleMultipartModelSelector implements MultipartModelSelector
{
    private static final Splitter VALUE_SPLITTER;
    private final String key;
    private final String valueString;
    
    public SimpleMultipartModelSelector(final String key, final String valueString) {
        this.key = key;
        this.valueString = valueString;
    }
    
    @Override
    public Predicate<BlockState> getPredicate(final StateFactory<Block, BlockState> stateFactory) {
        final Property<?> property2 = stateFactory.getProperty(this.key);
        if (property2 == null) {
            throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, stateFactory.getBaseObject().toString()));
        }
        String string2 = this.valueString;
        final boolean boolean4 = !string2.isEmpty() && string2.charAt(0) == '!';
        if (boolean4) {
            string2 = string2.substring(1);
        }
        final List<String> list5 = SimpleMultipartModelSelector.VALUE_SPLITTER.splitToList(string2);
        if (list5.isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.valueString, this.key, stateFactory.getBaseObject().toString()));
        }
        Predicate<BlockState> predicate2;
        if (list5.size() == 1) {
            predicate2 = this.createPredicate(stateFactory, property2, string2);
        }
        else {
            final List<Predicate<BlockState>> list6 = list5.stream().map(string -> this.createPredicate(stateFactory, property2, string)).collect(Collectors.toList());
            predicate2 = (blockState -> list6.stream().anyMatch(predicate -> predicate.test(blockState)));
        }
        return boolean4 ? predicate2.negate() : predicate2;
    }
    
    private Predicate<BlockState> createPredicate(final StateFactory<Block, BlockState> stateFactory, final Property<?> property, final String valueString) {
        final Optional<?> optional4 = property.getValue(valueString);
        if (!optional4.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", valueString, this.key, stateFactory.getBaseObject().toString(), this.valueString));
        }
        return blockState -> blockState.get(property).equals(optional4.get());
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.valueString).toString();
    }
    
    static {
        VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();
    }
}
