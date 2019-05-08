package net.minecraft.scoreboard;

import net.minecraft.world.PersistentState;

public class ScoreboardSynchronizer implements Runnable
{
    private final PersistentState compound;
    
    public ScoreboardSynchronizer(final PersistentState persistentState) {
        this.compound = persistentState;
    }
    
    @Override
    public void run() {
        this.compound.markDirty();
    }
}
