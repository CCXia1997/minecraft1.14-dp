package net.minecraft.datafixers.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.Typed;
import java.util.function.Function;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import java.util.Set;
import com.mojang.datafixers.DataFix;

public class EntityHealthFix extends DataFix
{
    private static final Set<String> ENTITIES;
    
    public EntityHealthFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public Dynamic<?> a(Dynamic<?> dynamic) {
        final Optional<Number> optional3 = (Optional<Number>)dynamic.get("HealF").asNumber();
        final Optional<Number> optional4 = (Optional<Number>)dynamic.get("Health").asNumber();
        float float2;
        if (optional3.isPresent()) {
            float2 = optional3.get().floatValue();
            dynamic = dynamic.remove("HealF");
        }
        else {
            if (!optional4.isPresent()) {
                return dynamic;
            }
            float2 = optional4.get().floatValue();
        }
        return dynamic.set("Health", dynamic.createFloat(float2));
    }
    
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), (Function)this::a));
    }
    
    static {
        ENTITIES = Sets.<String>newHashSet("ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie");
    }
}
