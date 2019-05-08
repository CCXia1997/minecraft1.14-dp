package net.minecraft.entity;

public enum EquipmentSlot
{
    HAND_MAIN(Type.HAND, 0, 0, "mainhand"), 
    HAND_OFF(Type.HAND, 1, 5, "offhand"), 
    FEET(Type.ARMOR, 0, 1, "feet"), 
    LEGS(Type.ARMOR, 1, 2, "legs"), 
    CHEST(Type.ARMOR, 2, 3, "chest"), 
    HEAD(Type.ARMOR, 3, 4, "head");
    
    private final Type type;
    private final int entityId;
    private final int armorStandId;
    private final String name;
    
    private EquipmentSlot(final Type type, final int entityId, final int armorStandId, final String name) {
        this.type = type;
        this.entityId = entityId;
        this.armorStandId = armorStandId;
        this.name = name;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public int getEntitySlotId() {
        return this.entityId;
    }
    
    public int getArmorStandSlotId() {
        return this.armorStandId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static EquipmentSlot byName(final String name) {
        for (final EquipmentSlot equipmentSlot5 : values()) {
            if (equipmentSlot5.getName().equals(name)) {
                return equipmentSlot5;
            }
        }
        throw new IllegalArgumentException("Invalid slot '" + name + "'");
    }
    
    public static EquipmentSlot a(final Type type, final int integer) {
        for (final EquipmentSlot equipmentSlot6 : values()) {
            if (equipmentSlot6.getType() == type && equipmentSlot6.getEntitySlotId() == integer) {
                return equipmentSlot6;
            }
        }
        throw new IllegalArgumentException("Invalid slot '" + type + "': " + integer);
    }
    
    public enum Type
    {
        HAND, 
        ARMOR;
    }
}
