package net.minecraft.block.entity;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;

public class DropperBlockEntity extends DispenserBlockEntity
{
    public DropperBlockEntity() {
        super(BlockEntityType.DROPPER);
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.dropper", new Object[0]);
    }
}
