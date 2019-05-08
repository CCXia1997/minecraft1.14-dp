package net.minecraft.world;

import java.util.Comparator;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;

public enum Difficulty
{
    PEACEFUL(0, "peaceful"), 
    EASY(1, "easy"), 
    NORMAL(2, "normal"), 
    HARD(3, "hard");
    
    private static final Difficulty[] DIFFICULTIES;
    private final int id;
    private final String translationKey;
    
    private Difficulty(final int id, final String string2) {
        this.id = id;
        this.translationKey = string2;
    }
    
    public int getId() {
        return this.id;
    }
    
    public TextComponent toTextComponent() {
        return new TranslatableTextComponent("options.difficulty." + this.translationKey, new Object[0]);
    }
    
    public static Difficulty getDifficulty(final int difficulty) {
        return Difficulty.DIFFICULTIES[difficulty % Difficulty.DIFFICULTIES.length];
    }
    
    @Nullable
    public static Difficulty getDifficulty(final String translationKey) {
        for (final Difficulty difficulty5 : values()) {
            if (difficulty5.translationKey.equals(translationKey)) {
                return difficulty5;
            }
        }
        return null;
    }
    
    public String getTranslationKey() {
        return this.translationKey;
    }
    
    static {
        DIFFICULTIES = Arrays.<Difficulty>stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).<Difficulty>toArray(Difficulty[]::new);
    }
}
