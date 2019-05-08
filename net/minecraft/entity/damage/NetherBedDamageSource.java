package net.minecraft.entity.damage;

import net.minecraft.text.Style;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.LivingEntity;

public class NetherBedDamageSource extends DamageSource
{
    protected NetherBedDamageSource() {
        super("netherBed");
        this.setScaledWithDifficulty();
        this.setExplosive();
    }
    
    @Override
    public TextComponent getDeathMessage(final LivingEntity livingEntity) {
        final HoverEvent hoverEvent;
        final Object o;
        final TextComponent textComponent2 = TextFormatter.bracketed(new TranslatableTextComponent("death.attack.netherBed.link", new Object[0])).modifyStyle(style -> {
            style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723"));
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723"));
            ((Style)o).setHoverEvent(hoverEvent);
            return;
        });
        return new TranslatableTextComponent("death.attack.netherBed.message", new Object[] { livingEntity.getDisplayName(), textComponent2 });
    }
}
