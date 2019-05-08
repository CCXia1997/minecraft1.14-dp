package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;

public enum GameMode
{
    INVALID(-1, ""), 
    b(0, "survival"), 
    c(1, "creative"), 
    d(2, "adventure"), 
    e(3, "spectator");
    
    private final int id;
    private final String name;
    
    private GameMode(final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public TextComponent getTextComponent() {
        return new TranslatableTextComponent("gameMode." + this.name, new Object[0]);
    }
    
    public void setAbilitites(final PlayerAbilities abilities) {
        if (this == GameMode.c) {
            abilities.allowFlying = true;
            abilities.creativeMode = true;
            abilities.invulnerable = true;
        }
        else if (this == GameMode.e) {
            abilities.allowFlying = true;
            abilities.creativeMode = false;
            abilities.invulnerable = true;
            abilities.flying = true;
        }
        else {
            abilities.allowFlying = false;
            abilities.creativeMode = false;
            abilities.invulnerable = false;
            abilities.flying = false;
        }
        abilities.allowModifyWorld = !this.shouldLimitWorldModification();
    }
    
    public boolean shouldLimitWorldModification() {
        return this == GameMode.d || this == GameMode.e;
    }
    
    public boolean isCreative() {
        return this == GameMode.c;
    }
    
    public boolean isSurvivalLike() {
        return this == GameMode.b || this == GameMode.d;
    }
    
    public static GameMode byId(final int id) {
        return byId(id, GameMode.b);
    }
    
    public static GameMode byId(final int id, final GameMode defaultMode) {
        for (final GameMode gameMode6 : values()) {
            if (gameMode6.id == id) {
                return gameMode6;
            }
        }
        return defaultMode;
    }
    
    public static GameMode byName(final String name) {
        return byName(name, GameMode.b);
    }
    
    public static GameMode byName(final String name, final GameMode defaultMode) {
        for (final GameMode gameMode6 : values()) {
            if (gameMode6.name.equals(name)) {
                return gameMode6;
            }
        }
        return defaultMode;
    }
}
