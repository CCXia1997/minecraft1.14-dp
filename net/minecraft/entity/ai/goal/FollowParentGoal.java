package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;

public class FollowParentGoal extends Goal
{
    private final AnimalEntity owner;
    private AnimalEntity parent;
    private final double c;
    private int d;
    
    public FollowParentGoal(final AnimalEntity animalEntity, final double double2) {
        this.owner = animalEntity;
        this.c = double2;
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.getBreedingAge() >= 0) {
            return false;
        }
        final List<AnimalEntity> list1 = this.owner.world.<AnimalEntity>getEntities(this.owner.getClass(), this.owner.getBoundingBox().expand(8.0, 4.0, 8.0));
        AnimalEntity animalEntity2 = null;
        double double3 = Double.MAX_VALUE;
        for (final AnimalEntity animalEntity3 : list1) {
            if (animalEntity3.getBreedingAge() < 0) {
                continue;
            }
            final double double4 = this.owner.squaredDistanceTo(animalEntity3);
            if (double4 > double3) {
                continue;
            }
            double3 = double4;
            animalEntity2 = animalEntity3;
        }
        if (animalEntity2 == null) {
            return false;
        }
        if (double3 < 9.0) {
            return false;
        }
        this.parent = animalEntity2;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        if (this.owner.getBreedingAge() >= 0) {
            return false;
        }
        if (!this.parent.isAlive()) {
            return false;
        }
        final double double1 = this.owner.squaredDistanceTo(this.parent);
        return double1 >= 9.0 && double1 <= 256.0;
    }
    
    @Override
    public void start() {
        this.d = 0;
    }
    
    @Override
    public void stop() {
        this.parent = null;
    }
    
    @Override
    public void tick() {
        final int d = this.d - 1;
        this.d = d;
        if (d > 0) {
            return;
        }
        this.d = 10;
        this.owner.getNavigation().startMovingTo(this.parent, this.c);
    }
}
