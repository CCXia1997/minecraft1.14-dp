package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;

public abstract class IllagerEntity extends RaiderEntity
{
    protected IllagerEntity(final EntityType<? extends IllagerEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ILLAGER;
    }
    
    @Environment(EnvType.CLIENT)
    public State getState() {
        return State.a;
    }
    
    @Environment(EnvType.CLIENT)
    public enum State
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g;
    }
    
    public class b extends LongDoorInteractGoal
    {
        public b(final RaiderEntity raiderEntity) {
            super(raiderEntity, false);
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && IllagerEntity.this.hasActiveRaid();
        }
    }
}
