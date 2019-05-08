package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class HorseArmorItem extends Item
{
    private final int bonus;
    private final String texture;
    
    public HorseArmorItem(final int bonus, final String name, final Settings settings) {
        super(settings);
        this.bonus = bonus;
        this.texture = "textures/entity/horse/armor/horse_armor_" + name + ".png";
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getHorseArmorTexture() {
        return new Identifier(this.texture);
    }
    
    public int getBonus() {
        return this.bonus;
    }
}
