package net.minecraft.entity.boss.dragon.phase;

import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.apache.logging.log4j.Logger;

public class PhaseManager
{
    private static final Logger LOGGER;
    private final EnderDragonEntity dragon;
    private final Phase[] phases;
    private Phase current;
    
    public PhaseManager(final EnderDragonEntity dragon) {
        this.phases = new Phase[PhaseType.count()];
        this.dragon = dragon;
        this.setPhase(PhaseType.HOVER);
    }
    
    public void setPhase(final PhaseType<?> type) {
        if (this.current != null && type == this.current.getType()) {
            return;
        }
        if (this.current != null) {
            this.current.endPhase();
        }
        this.current = this.<Phase>create(type);
        if (!this.dragon.world.isClient) {
            this.dragon.getDataTracker().<Integer>set(EnderDragonEntity.PHASE_TYPE, type.getTypeId());
        }
        PhaseManager.LOGGER.debug("Dragon is now in phase {} on the {}", type, (this.dragon.world.isClient ? "client" : "server"));
        this.current.beginPhase();
    }
    
    public Phase getCurrent() {
        return this.current;
    }
    
    public <T extends Phase> T create(final PhaseType<T> type) {
        final int integer2 = type.getTypeId();
        if (this.phases[integer2] == null) {
            this.phases[integer2] = type.create(this.dragon);
        }
        return (T)this.phases[integer2];
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
