package net.minecraft.predicate.entity;

import net.minecraft.entity.raid.Raid;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.item.ItemPredicate;

public class EntityEquipmentPredicate
{
    public static final EntityEquipmentPredicate ANY;
    public static final EntityEquipmentPredicate b;
    private final ItemPredicate head;
    private final ItemPredicate chest;
    private final ItemPredicate legs;
    private final ItemPredicate feet;
    private final ItemPredicate mainhand;
    private final ItemPredicate offhand;
    
    public EntityEquipmentPredicate(final ItemPredicate head, final ItemPredicate chest, final ItemPredicate legs, final ItemPredicate feet, final ItemPredicate mainhand, final ItemPredicate itemPredicate6) {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.mainhand = mainhand;
        this.offhand = itemPredicate6;
    }
    
    public boolean test(@Nullable final Entity entity) {
        if (this == EntityEquipmentPredicate.ANY) {
            return true;
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        final LivingEntity livingEntity2 = (LivingEntity)entity;
        return this.head.test(livingEntity2.getEquippedStack(EquipmentSlot.HEAD)) && this.chest.test(livingEntity2.getEquippedStack(EquipmentSlot.CHEST)) && this.legs.test(livingEntity2.getEquippedStack(EquipmentSlot.LEGS)) && this.feet.test(livingEntity2.getEquippedStack(EquipmentSlot.FEET)) && this.mainhand.test(livingEntity2.getEquippedStack(EquipmentSlot.HAND_MAIN)) && this.offhand.test(livingEntity2.getEquippedStack(EquipmentSlot.HAND_OFF));
    }
    
    public static EntityEquipmentPredicate deserialize(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EntityEquipmentPredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "equipment");
        final ItemPredicate itemPredicate3 = ItemPredicate.deserialize(jsonObject2.get("head"));
        final ItemPredicate itemPredicate4 = ItemPredicate.deserialize(jsonObject2.get("chest"));
        final ItemPredicate itemPredicate5 = ItemPredicate.deserialize(jsonObject2.get("legs"));
        final ItemPredicate itemPredicate6 = ItemPredicate.deserialize(jsonObject2.get("feet"));
        final ItemPredicate itemPredicate7 = ItemPredicate.deserialize(jsonObject2.get("mainhand"));
        final ItemPredicate itemPredicate8 = ItemPredicate.deserialize(jsonObject2.get("offhand"));
        return new EntityEquipmentPredicate(itemPredicate3, itemPredicate4, itemPredicate5, itemPredicate6, itemPredicate7, itemPredicate8);
    }
    
    public JsonElement serialize() {
        if (this == EntityEquipmentPredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("head", this.head.serialize());
        jsonObject1.add("chest", this.chest.serialize());
        jsonObject1.add("legs", this.legs.serialize());
        jsonObject1.add("feet", this.feet.serialize());
        jsonObject1.add("mainhand", this.mainhand.serialize());
        jsonObject1.add("offhand", this.offhand.serialize());
        return jsonObject1;
    }
    
    static {
        ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
        b = new EntityEquipmentPredicate(ItemPredicate.Builder.create().item(Items.ov).nbt(Raid.OMINOUS_BANNER.getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    }
}
