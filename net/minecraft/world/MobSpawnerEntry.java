package net.minecraft.world;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.WeightedPicker;

public class MobSpawnerEntry extends WeightedPicker.Entry
{
    private final CompoundTag entityTag;
    
    public MobSpawnerEntry() {
        super(1);
        (this.entityTag = new CompoundTag()).putString("id", "minecraft:pig");
    }
    
    public MobSpawnerEntry(final CompoundTag compoundTag) {
        this(compoundTag.containsKey("Weight", 99) ? compoundTag.getInt("Weight") : 1, compoundTag.getCompound("Entity"));
    }
    
    public MobSpawnerEntry(final int weight, final CompoundTag compoundTag) {
        super(weight);
        this.entityTag = compoundTag;
    }
    
    public CompoundTag serialize() {
        final CompoundTag compoundTag1 = new CompoundTag();
        if (!this.entityTag.containsKey("id", 8)) {
            this.entityTag.putString("id", "minecraft:pig");
        }
        else if (!this.entityTag.getString("id").contains(":")) {
            this.entityTag.putString("id", new Identifier(this.entityTag.getString("id")).toString());
        }
        compoundTag1.put("Entity", this.entityTag);
        compoundTag1.putInt("Weight", this.weight);
        return compoundTag1;
    }
    
    public CompoundTag getEntityTag() {
        return this.entityTag;
    }
}
