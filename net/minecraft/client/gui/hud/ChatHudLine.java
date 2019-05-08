package net.minecraft.client.gui.hud;

import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChatHudLine
{
    private final int tickCreated;
    private final TextComponent contents;
    private final int id;
    
    public ChatHudLine(final int tickCreated, final TextComponent contents, final int id) {
        this.contents = contents;
        this.tickCreated = tickCreated;
        this.id = id;
    }
    
    public TextComponent getContents() {
        return this.contents;
    }
    
    public int getTickCreated() {
        return this.tickCreated;
    }
    
    public int getId() {
        return this.id;
    }
}
