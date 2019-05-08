package net.minecraft.block;

import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;

public class StoneButtonBlock extends AbstractButtonBlock
{
    protected StoneButtonBlock(final Settings settings) {
        super(false, settings);
    }
    
    @Override
    protected SoundEvent getClickSound(final boolean powered) {
        return powered ? SoundEvents.lo : SoundEvents.ln;
    }
}
