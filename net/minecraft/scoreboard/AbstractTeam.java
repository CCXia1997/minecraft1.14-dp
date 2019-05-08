package net.minecraft.scoreboard;

import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Map;
import java.util.Collection;
import net.minecraft.text.TextFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;

public abstract class AbstractTeam
{
    public boolean isEqual(@Nullable final AbstractTeam abstractTeam) {
        return abstractTeam != null && this == abstractTeam;
    }
    
    public abstract String getName();
    
    public abstract TextComponent modifyText(final TextComponent arg1);
    
    @Environment(EnvType.CLIENT)
    public abstract boolean shouldShowFriendlyInvisibles();
    
    public abstract boolean isFriendlyFireAllowed();
    
    @Environment(EnvType.CLIENT)
    public abstract VisibilityRule getNameTagVisibilityRule();
    
    public abstract TextFormat getColor();
    
    public abstract Collection<String> getPlayerList();
    
    public abstract VisibilityRule getDeathMessageVisibilityRule();
    
    public abstract CollisionRule getCollisionRule();
    
    public enum VisibilityRule
    {
        ALWAYS("always", 0), 
        NEVER("never", 1), 
        HIDDEN_FOR_OTHER_TEAMS("hideForOtherTeams", 2), 
        HIDDEN_FOR_TEAM("hideForOwnTeam", 3);
        
        private static final Map<String, VisibilityRule> VISIBILITY_RULES;
        public final String name;
        public final int value;
        
        @Nullable
        public static VisibilityRule getRule(final String name) {
            return VisibilityRule.VISIBILITY_RULES.get(name);
        }
        
        private VisibilityRule(final String name, final int value) {
            this.name = name;
            this.value = value;
        }
        
        public TextComponent getTranslationKey() {
            return new TranslatableTextComponent("team.visibility." + this.name, new Object[0]);
        }
        
        static {
            VISIBILITY_RULES = Arrays.<VisibilityRule>stream(values()).collect(Collectors.toMap(visibilityRule -> visibilityRule.name, visibilityRule -> visibilityRule));
        }
    }
    
    public enum CollisionRule
    {
        ALWAYS("always", 0), 
        b("never", 1), 
        c("pushOtherTeams", 2), 
        d("pushOwnTeam", 3);
        
        private static final Map<String, CollisionRule> COLLISION_RULES;
        public final String name;
        public final int value;
        
        @Nullable
        public static CollisionRule getRule(final String name) {
            return CollisionRule.COLLISION_RULES.get(name);
        }
        
        private CollisionRule(final String name, final int value) {
            this.name = name;
            this.value = value;
        }
        
        public TextComponent getTranslationKey() {
            return new TranslatableTextComponent("team.collision." + this.name, new Object[0]);
        }
        
        static {
            COLLISION_RULES = Arrays.<CollisionRule>stream(values()).collect(Collectors.toMap(collisionRule -> collisionRule.name, collisionRule -> collisionRule));
        }
    }
}
