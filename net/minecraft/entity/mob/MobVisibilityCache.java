package net.minecraft.entity.mob;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import java.util.List;

public class MobVisibilityCache
{
    private final MobEntity owner;
    private final List<Entity> visibleEntities;
    private final List<Entity> invisibleEntities;
    
    public MobVisibilityCache(final MobEntity mobEntity) {
        this.visibleEntities = Lists.newArrayList();
        this.invisibleEntities = Lists.newArrayList();
        this.owner = mobEntity;
    }
    
    public void clear() {
        this.visibleEntities.clear();
        this.invisibleEntities.clear();
    }
    
    public boolean canSee(final Entity entity) {
        if (this.visibleEntities.contains(entity)) {
            return true;
        }
        if (this.invisibleEntities.contains(entity)) {
            return false;
        }
        this.owner.world.getProfiler().push("canSee");
        final boolean boolean2 = this.owner.canSee(entity);
        this.owner.world.getProfiler().pop();
        if (boolean2) {
            this.visibleEntities.add(entity);
        }
        else {
            this.invisibleEntities.add(entity);
        }
        return boolean2;
    }
}
