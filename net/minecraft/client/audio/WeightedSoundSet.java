package net.minecraft.client.audio;

import java.util.Iterator;
import net.minecraft.text.TranslatableTextComponent;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import java.util.Random;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WeightedSoundSet implements SoundContainer<Sound>
{
    private final List<SoundContainer<Sound>> sounds;
    private final Random random;
    private final Identifier id;
    private final TextComponent subtitle;
    
    public WeightedSoundSet(final Identifier id, @Nullable final String string) {
        this.sounds = Lists.newArrayList();
        this.random = new Random();
        this.id = id;
        this.subtitle = ((string == null) ? null : new TranslatableTextComponent(string, new Object[0]));
    }
    
    @Override
    public int getWeight() {
        int integer1 = 0;
        for (final SoundContainer<Sound> soundContainer3 : this.sounds) {
            integer1 += soundContainer3.getWeight();
        }
        return integer1;
    }
    
    @Override
    public Sound getSound() {
        final int integer1 = this.getWeight();
        if (this.sounds.isEmpty() || integer1 == 0) {
            return SoundManager.SOUND_MISSING;
        }
        int integer2 = this.random.nextInt(integer1);
        for (final SoundContainer<Sound> soundContainer4 : this.sounds) {
            integer2 -= soundContainer4.getWeight();
            if (integer2 < 0) {
                return soundContainer4.getSound();
            }
        }
        return SoundManager.SOUND_MISSING;
    }
    
    public void add(final SoundContainer<Sound> soundContainer) {
        this.sounds.add(soundContainer);
    }
    
    @Nullable
    public TextComponent getSubtitle() {
        return this.subtitle;
    }
    
    @Override
    public void preload(final SoundSystem soundSystem) {
        for (final SoundContainer<Sound> soundContainer3 : this.sounds) {
            soundContainer3.preload(soundSystem);
        }
    }
}
