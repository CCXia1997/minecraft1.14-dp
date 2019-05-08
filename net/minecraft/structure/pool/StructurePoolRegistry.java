package net.minecraft.structure.pool;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import java.util.Map;

public class StructurePoolRegistry
{
    private final Map<Identifier, StructurePool> pools;
    
    public StructurePoolRegistry() {
        this.pools = Maps.newHashMap();
        this.add(StructurePool.EMPTY);
    }
    
    public void add(final StructurePool pool) {
        this.pools.put(pool.getId(), pool);
    }
    
    public StructurePool get(final Identifier id) {
        final StructurePool structurePool2 = this.pools.get(id);
        return (structurePool2 != null) ? structurePool2 : StructurePool.INVALID;
    }
}
