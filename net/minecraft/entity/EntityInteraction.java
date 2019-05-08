package net.minecraft.entity;

public interface EntityInteraction
{
    public static final EntityInteraction a = create("zombie_villager_cured");
    public static final EntityInteraction b = create("golem_killed");
    public static final EntityInteraction c = create("villager_hurt");
    public static final EntityInteraction d = create("villager_killed");
    public static final EntityInteraction e = create("trade");
    
    default EntityInteraction create(final String key) {
        return new EntityInteraction() {
            @Override
            public String toString() {
                return key;
            }
        };
    }
}
