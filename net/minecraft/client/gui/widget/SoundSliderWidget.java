package net.minecraft.client.gui.widget;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundSliderWidget extends SliderWidget
{
    private final SoundCategory category;
    
    public SoundSliderWidget(final MinecraftClient client, final int x, final int y, final SoundCategory soundCategory, final int width) {
        super(client.options, x, y, width, 20, client.options.getSoundVolume(soundCategory));
        this.category = soundCategory;
        this.updateMessage();
    }
    
    @Override
    protected void updateMessage() {
        final String string1 = ((float)this.value == this.getYImage(false)) ? I18n.translate("options.off") : ((int)((float)this.value * 100.0f) + "%");
        this.setMessage(I18n.translate("soundCategory." + this.category.getName()) + ": " + string1);
    }
    
    @Override
    protected void applyValue() {
        this.options.setSoundVolume(this.category, (float)this.value);
        this.options.write();
    }
}
