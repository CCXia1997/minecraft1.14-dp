package net.minecraft.block;

import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;

public class WoodButtonBlock extends AbstractButtonBlock
{
    protected WoodButtonBlock(final Settings settings) {
        super(true, settings);
    }
    
    @Override
    protected SoundEvent getClickSound(final boolean powered) {
        return powered ? SoundEvents.nL : SoundEvents.nK;
    }
}
