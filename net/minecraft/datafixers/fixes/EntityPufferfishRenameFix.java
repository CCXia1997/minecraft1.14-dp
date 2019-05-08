package net.minecraft.datafixers.fixes;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityPufferfishRenameFix extends EntityRenameFix
{
    public static final Map<String, String> RENAMED_FISHES;
    
    public EntityPufferfishRenameFix(final Schema schema, final boolean boolean2) {
        super("EntityPufferfishRenameFix", schema, boolean2);
    }
    
    @Override
    protected String rename(final String string) {
        return Objects.equals("minecraft:puffer_fish", string) ? "minecraft:pufferfish" : string;
    }
    
    static {
        RENAMED_FISHES = ImmutableMap.<String, String>builder().put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg").build();
    }
}
