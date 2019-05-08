package net.minecraft.datafixers.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityCodSalmonFix extends EntityRenameFix
{
    public static final Map<String, String> ENTITIES;
    public static final Map<String, String> SPAWN_EGGS;
    
    public EntityCodSalmonFix(final Schema schema, final boolean boolean2) {
        super("EntityCodSalmonFix", schema, boolean2);
    }
    
    @Override
    protected String rename(final String string) {
        return EntityCodSalmonFix.ENTITIES.getOrDefault(string, string);
    }
    
    static {
        ENTITIES = ImmutableMap.<String, String>builder().put("minecraft:salmon_mob", "minecraft:salmon").put("minecraft:cod_mob", "minecraft:cod").build();
        SPAWN_EGGS = ImmutableMap.<String, String>builder().put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg").put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg").build();
    }
}
