package net.minecraft.village;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;

public class VillagerData
{
    private static final int[] LEVEL_BASE_EXPERIENCE;
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;
    
    public VillagerData(final VillagerType villagerType, final VillagerProfession villagerProfession, final int level) {
        this.type = villagerType;
        this.profession = villagerProfession;
        this.level = Math.max(1, level);
    }
    
    public VillagerData(final Dynamic<?> dynamic) {
        this(Registry.VILLAGER_TYPE.get(Identifier.create(dynamic.get("type").asString(""))), Registry.VILLAGER_PROFESSION.get(Identifier.create(dynamic.get("profession").asString(""))), dynamic.get("level").asInt(1));
    }
    
    public VillagerType getType() {
        return this.type;
    }
    
    public VillagerProfession getProfession() {
        return this.profession;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public VillagerData withType(final VillagerType villagerType) {
        return new VillagerData(villagerType, this.profession, this.level);
    }
    
    public VillagerData withProfession(final VillagerProfession villagerProfession) {
        return new VillagerData(this.type, villagerProfession, this.level);
    }
    
    public VillagerData withLevel(final int level) {
        return new VillagerData(this.type, this.profession, level);
    }
    
    public <T> T serialize(final DynamicOps<T> ops) {
        return (T)ops.createMap((Map)ImmutableMap.of(ops.createString("type"), ops.createString(Registry.VILLAGER_TYPE.getId(this.type).toString()), ops.createString("profession"), ops.createString(Registry.VILLAGER_PROFESSION.getId(this.profession).toString()), ops.createString("level"), ops.createInt(this.level)));
    }
    
    public static int getLowerLevelExperience(final int level) {
        return canLevelUp(level) ? VillagerData.LEVEL_BASE_EXPERIENCE[level - 1] : 0;
    }
    
    public static int getUpperLevelExperience(final int level) {
        return canLevelUp(level) ? VillagerData.LEVEL_BASE_EXPERIENCE[level] : 0;
    }
    
    public static boolean canLevelUp(final int level) {
        return level >= 1 && level < 5;
    }
    
    static {
        LEVEL_BASE_EXPERIENCE = new int[] { 0, 10, 50, 100, 150 };
    }
}
