package net.minecraft.entity;

public class EntityGroup
{
    public static final EntityGroup DEFAULT;
    public static final EntityGroup UNDEAD;
    public static final EntityGroup ARTHROPOD;
    public static final EntityGroup ILLAGER;
    public static final EntityGroup AQUATIC;
    
    static {
        DEFAULT = new EntityGroup();
        UNDEAD = new EntityGroup();
        ARTHROPOD = new EntityGroup();
        ILLAGER = new EntityGroup();
        AQUATIC = new EntityGroup();
    }
}
