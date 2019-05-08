package net.minecraft.world.loot.context;

import net.minecraft.world.loot.LootTableReporter;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class LootContextType
{
    private final Set<LootContextParameter<?>> required;
    private final Set<LootContextParameter<?>> allowed;
    
    private LootContextType(final Set<LootContextParameter<?>> required, final Set<LootContextParameter<?>> allowed) {
        this.required = ImmutableSet.copyOf(required);
        this.allowed = ImmutableSet.copyOf(Sets.union(required, allowed));
    }
    
    public Set<LootContextParameter<?>> getRequired() {
        return this.required;
    }
    
    public Set<LootContextParameter<?>> getAllowed() {
        return this.allowed;
    }
    
    @Override
    public String toString() {
        return "[" + Joiner.on(", ").join(this.allowed.stream().map(parameter -> (this.required.contains(parameter) ? "!" : "") + parameter.getIdentifier()).iterator()) + "]";
    }
    
    public void check(final LootTableReporter reporter, final ParameterConsumer parameterConsumer) {
        final Set<LootContextParameter<?>> set3 = parameterConsumer.getRequiredParameters();
        final Set<LootContextParameter<?>> set4 = Sets.<LootContextParameter<?>>difference(set3, this.allowed);
        if (!set4.isEmpty()) {
            reporter.report("Parameters " + set4 + " are not provided in this context");
        }
    }
    
    public static class Builder
    {
        private final Set<LootContextParameter<?>> required;
        private final Set<LootContextParameter<?>> allowed;
        
        public Builder() {
            this.required = Sets.<LootContextParameter<?>>newIdentityHashSet();
            this.allowed = Sets.<LootContextParameter<?>>newIdentityHashSet();
        }
        
        public Builder require(final LootContextParameter<?> parameter) {
            if (this.allowed.contains(parameter)) {
                throw new IllegalArgumentException("Parameter " + parameter.getIdentifier() + " is already optional");
            }
            this.required.add(parameter);
            return this;
        }
        
        public Builder allow(final LootContextParameter<?> parameter) {
            if (this.required.contains(parameter)) {
                throw new IllegalArgumentException("Parameter " + parameter.getIdentifier() + " is already required");
            }
            this.allowed.add(parameter);
            return this;
        }
        
        public LootContextType build() {
            return new LootContextType(this.required, this.allowed, null);
        }
    }
}
