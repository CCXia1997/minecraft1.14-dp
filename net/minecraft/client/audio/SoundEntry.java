package net.minecraft.client.audio;

import javax.annotation.Nullable;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundEntry
{
    private final List<Sound> sounds;
    private final boolean replace;
    private final String subtitle;
    
    public SoundEntry(final List<Sound> sounds, final boolean replace, final String subtitle) {
        this.sounds = sounds;
        this.replace = replace;
        this.subtitle = subtitle;
    }
    
    public List<Sound> getSounds() {
        return this.sounds;
    }
    
    public boolean canReplace() {
        return this.replace;
    }
    
    @Nullable
    public String getSubtitle() {
        return this.subtitle;
    }
}
