package net.minecraft.predicate;

import net.minecraft.entity.player.PlayerEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.util.TagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.Tag;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class NbtPredicate
{
    public static final NbtPredicate ANY;
    @Nullable
    private final CompoundTag tag;
    
    public NbtPredicate(@Nullable final CompoundTag compoundTag) {
        this.tag = compoundTag;
    }
    
    public boolean test(final ItemStack itemStack) {
        return this == NbtPredicate.ANY || this.test(itemStack.getTag());
    }
    
    public boolean test(final Entity entity) {
        return this == NbtPredicate.ANY || this.test(entityToTag(entity));
    }
    
    public boolean test(@Nullable final Tag tag) {
        if (tag == null) {
            return this == NbtPredicate.ANY;
        }
        return this.tag == null || TagHelper.areTagsEqual(this.tag, tag, true);
    }
    
    public JsonElement serialize() {
        if (this == NbtPredicate.ANY || this.tag == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(this.tag.toString());
    }
    
    public static NbtPredicate deserialize(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return NbtPredicate.ANY;
        }
        CompoundTag compoundTag2;
        try {
            compoundTag2 = StringNbtReader.parse(JsonHelper.asString(element, "nbt"));
        }
        catch (CommandSyntaxException commandSyntaxException3) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException3.getMessage());
        }
        return new NbtPredicate(compoundTag2);
    }
    
    public static CompoundTag entityToTag(final Entity entity) {
        final CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
        if (entity instanceof PlayerEntity) {
            final ItemStack itemStack3 = ((PlayerEntity)entity).inventory.getMainHandStack();
            if (!itemStack3.isEmpty()) {
                compoundTag2.put("SelectedItem", itemStack3.toTag(new CompoundTag()));
            }
        }
        return compoundTag2;
    }
    
    static {
        ANY = new NbtPredicate(null);
    }
}
