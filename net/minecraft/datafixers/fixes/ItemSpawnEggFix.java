package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemSpawnEggFix extends DataFix
{
    private static final String[] DAMAGE_TO_ENTITY_IDS;
    
    public ItemSpawnEggFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Schema schema1 = this.getInputSchema();
        final Type<?> type2 = schema1.getType(TypeReferences.ITEM_STACK);
        final OpticFinder<Pair<String, String>> opticFinder3 = (OpticFinder<Pair<String, String>>)DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
        final OpticFinder<String> opticFinder4 = (OpticFinder<String>)DSL.fieldFinder("id", DSL.string());
        final OpticFinder<?> opticFinder5 = type2.findField("tag");
        final OpticFinder<?> opticFinder6 = opticFinder5.type().findField("EntityTag");
        final OpticFinder<?> opticFinder7 = DSL.typeFinder(schema1.getTypeRaw(TypeReferences.ENTITY));
        final OpticFinder opticFinder8;
        final Optional<Pair<String, String>> optional7;
        Dynamic<?> dynamic8;
        short short9;
        final OpticFinder opticFinder9;
        Optional<? extends Typed<?>> optional8;
        final OpticFinder opticFinder10;
        Optional<? extends Typed<?>> optional9;
        final OpticFinder opticFinder11;
        Optional<? extends Typed<?>> optional10;
        final OpticFinder opticFinder12;
        Optional<String> optional11;
        Typed<?> typed2;
        String string15;
        Typed<?> typed3;
        Typed<?> typed4;
        Typed<?> typed5;
        Dynamic<?> dynamic9;
        Typed<?> typed6;
        Dynamic<?> dynamic10;
        return this.fixTypeEverywhereTyped("ItemSpawnEggFix", (Type)type2, typed -> {
            optional7 = (Optional<Pair<String, String>>)typed.getOptional(opticFinder8);
            if (optional7.isPresent() && Objects.equals(optional7.get().getSecond(), "minecraft:spawn_egg")) {
                dynamic8 = typed.get(DSL.remainderFinder());
                short9 = dynamic8.get("Damage").asShort((short)0);
                optional8 = typed.getOptionalTyped(opticFinder9);
                optional9 = optional8.flatMap(typed -> typed.getOptionalTyped(opticFinder10));
                optional10 = optional9.flatMap(typed -> typed.getOptionalTyped(opticFinder11));
                optional11 = optional10.<String>flatMap(typed -> typed.getOptional(opticFinder12));
                typed2 = typed;
                string15 = ItemSpawnEggFix.DAMAGE_TO_ENTITY_IDS[short9 & 0xFF];
                if (string15 != null && (!optional11.isPresent() || !Objects.equals(optional11.get(), string15))) {
                    typed3 = typed.getOrCreateTyped(opticFinder9);
                    typed4 = typed3.getOrCreateTyped(opticFinder10);
                    typed5 = typed4.getOrCreateTyped(opticFinder11);
                    dynamic9 = typed5.write().set("id", dynamic8.createString(string15));
                    typed6 = ((Optional)this.getOutputSchema().getTypeRaw(TypeReferences.ENTITY).readTyped((Dynamic)dynamic9).getSecond()).<Throwable>orElseThrow(() -> new IllegalStateException("Could not parse new entity"));
                    typed2 = typed2.set(opticFinder9, typed3.set(opticFinder10, typed4.set(opticFinder11, (Typed)typed6)));
                }
                if (short9 != 0) {
                    dynamic10 = dynamic8.set("Damage", dynamic8.createShort((short)0));
                    typed2 = typed2.set(DSL.remainderFinder(), dynamic10);
                }
                return typed2;
            }
            else {
                return typed;
            }
        });
    }
    
    static {
        DAMAGE_TO_ENTITY_IDS = (String[])DataFixUtils.make(new String[256], arr -> {
            arr[1] = "Item";
            arr[2] = "XPOrb";
            arr[7] = "ThrownEgg";
            arr[8] = "LeashKnot";
            arr[9] = "Painting";
            arr[10] = "Arrow";
            arr[11] = "Snowball";
            arr[12] = "Fireball";
            arr[13] = "SmallFireball";
            arr[14] = "ThrownEnderpearl";
            arr[15] = "EyeOfEnderSignal";
            arr[16] = "ThrownPotion";
            arr[17] = "ThrownExpBottle";
            arr[18] = "ItemFrame";
            arr[19] = "WitherSkull";
            arr[20] = "PrimedTnt";
            arr[21] = "FallingSand";
            arr[22] = "FireworksRocketEntity";
            arr[23] = "TippedArrow";
            arr[24] = "SpectralArrow";
            arr[25] = "ShulkerBullet";
            arr[26] = "DragonFireball";
            arr[30] = "ArmorStand";
            arr[41] = "Boat";
            arr[42] = "MinecartRideable";
            arr[43] = "MinecartChest";
            arr[44] = "MinecartFurnace";
            arr[45] = "MinecartTNT";
            arr[46] = "MinecartHopper";
            arr[47] = "MinecartSpawner";
            arr[40] = "MinecartCommandBlock";
            arr[48] = "Mob";
            arr[49] = "Monster";
            arr[50] = "Creeper";
            arr[51] = "Skeleton";
            arr[52] = "Spider";
            arr[53] = "Giant";
            arr[54] = "Zombie";
            arr[55] = "Slime";
            arr[56] = "Ghast";
            arr[57] = "PigZombie";
            arr[58] = "Enderman";
            arr[59] = "CaveSpider";
            arr[60] = "Silverfish";
            arr[61] = "Blaze";
            arr[62] = "LavaSlime";
            arr[63] = "EnderDragon";
            arr[64] = "WitherBoss";
            arr[65] = "Bat";
            arr[66] = "Witch";
            arr[67] = "Endermite";
            arr[68] = "Guardian";
            arr[69] = "Shulker";
            arr[90] = "Pig";
            arr[91] = "Sheep";
            arr[92] = "Cow";
            arr[93] = "Chicken";
            arr[94] = "Squid";
            arr[95] = "Wolf";
            arr[96] = "MushroomCow";
            arr[97] = "SnowMan";
            arr[98] = "Ozelot";
            arr[99] = "VillagerGolem";
            arr[100] = "EntityHorse";
            arr[101] = "Rabbit";
            arr[120] = "Villager";
            arr[200] = "EnderCrystal";
        });
    }
}
