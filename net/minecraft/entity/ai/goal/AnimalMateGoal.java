package net.minecraft.entity.ai.goal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.stat.Stats;
import net.minecraft.entity.passive.PassiveEntity;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.world.World;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.ai.TargetPredicate;

public class AnimalMateGoal extends Goal
{
    private static final TargetPredicate VALID_MATE_PREDICATE;
    protected final AnimalEntity owner;
    private final Class<? extends AnimalEntity> entityClass;
    protected final World world;
    protected AnimalEntity mate;
    private int timer;
    private final double chance;
    
    public AnimalMateGoal(final AnimalEntity owner, final double chance) {
        this(owner, chance, owner.getClass());
    }
    
    public AnimalMateGoal(final AnimalEntity animalEntity, final double chance, final Class<? extends AnimalEntity> entityClass) {
        this.owner = animalEntity;
        this.world = animalEntity.world;
        this.entityClass = entityClass;
        this.chance = chance;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        if (!this.owner.isInLove()) {
            return false;
        }
        this.mate = this.findMate();
        return this.mate != null;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60;
    }
    
    @Override
    public void stop() {
        this.mate = null;
        this.timer = 0;
    }
    
    @Override
    public void tick() {
        this.owner.getLookControl().lookAt(this.mate, 10.0f, (float)this.owner.getLookPitchSpeed());
        this.owner.getNavigation().startMovingTo(this.mate, this.chance);
        ++this.timer;
        if (this.timer >= 60 && this.owner.squaredDistanceTo(this.mate) < 9.0) {
            this.breed();
        }
    }
    
    @Nullable
    private AnimalEntity findMate() {
        final List<AnimalEntity> list1 = this.world.<AnimalEntity>getTargets(this.entityClass, AnimalMateGoal.VALID_MATE_PREDICATE, (LivingEntity)this.owner, this.owner.getBoundingBox().expand(8.0));
        double double2 = Double.MAX_VALUE;
        AnimalEntity animalEntity4 = null;
        for (final AnimalEntity animalEntity5 : list1) {
            if (this.owner.canBreedWith(animalEntity5) && this.owner.squaredDistanceTo(animalEntity5) < double2) {
                animalEntity4 = animalEntity5;
                double2 = this.owner.squaredDistanceTo(animalEntity5);
            }
        }
        return animalEntity4;
    }
    
    protected void breed() {
        final PassiveEntity passiveEntity1 = this.owner.createChild(this.mate);
        if (passiveEntity1 == null) {
            return;
        }
        ServerPlayerEntity serverPlayerEntity2 = this.owner.getLovingPlayer();
        if (serverPlayerEntity2 == null && this.mate.getLovingPlayer() != null) {
            serverPlayerEntity2 = this.mate.getLovingPlayer();
        }
        if (serverPlayerEntity2 != null) {
            serverPlayerEntity2.incrementStat(Stats.N);
            Criterions.BRED_ANIMALS.handle(serverPlayerEntity2, this.owner, this.mate, passiveEntity1);
        }
        this.owner.setBreedingAge(6000);
        this.mate.setBreedingAge(6000);
        this.owner.resetLoveTicks();
        this.mate.resetLoveTicks();
        passiveEntity1.setBreedingAge(-24000);
        passiveEntity1.setPositionAndAngles(this.owner.x, this.owner.y, this.owner.z, 0.0f, 0.0f);
        this.world.spawnEntity(passiveEntity1);
        this.world.sendEntityStatus(this.owner, (byte)18);
        if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.owner.x, this.owner.y, this.owner.z, this.owner.getRand().nextInt(7) + 1));
        }
    }
    
    static {
        VALID_MATE_PREDICATE = new TargetPredicate().setBaseMaxDistance(8.0).includeInvulnerable().includeTeammates().includeHidden();
    }
}
