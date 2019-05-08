package net.minecraft.scoreboard;

import net.minecraft.text.Style;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.StringTextComponent;
import com.google.common.collect.Sets;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextComponent;
import java.util.Set;

public class Team extends AbstractTeam
{
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> playerList;
    private TextComponent displayName;
    private TextComponent prefix;
    private TextComponent suffix;
    private boolean friendlyFire;
    private boolean showFriendlyInvisibles;
    private VisibilityRule nameTagVisibilityRule;
    private VisibilityRule deathMessageVisibilityRule;
    private TextFormat color;
    private CollisionRule collisionRule;
    
    public Team(final Scoreboard scoreboard, final String name) {
        this.playerList = Sets.newHashSet();
        this.prefix = new StringTextComponent("");
        this.suffix = new StringTextComponent("");
        this.friendlyFire = true;
        this.showFriendlyInvisibles = true;
        this.nameTagVisibilityRule = VisibilityRule.ALWAYS;
        this.deathMessageVisibilityRule = VisibilityRule.ALWAYS;
        this.color = TextFormat.RESET;
        this.collisionRule = CollisionRule.ALWAYS;
        this.scoreboard = scoreboard;
        this.name = name;
        this.displayName = new StringTextComponent(name);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public TextComponent getDisplayName() {
        return this.displayName;
    }
    
    public TextComponent getFormattedName() {
        final HoverEvent hoverEvent;
        final Object o;
        final TextComponent textComponent1 = TextFormatter.bracketed(this.displayName.copy().modifyStyle(style -> {
            style.setInsertion(this.name);
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.name));
            ((Style)o).setHoverEvent(hoverEvent);
            return;
        }));
        final TextFormat textFormat2 = this.getColor();
        if (textFormat2 != TextFormat.RESET) {
            textComponent1.applyFormat(textFormat2);
        }
        return textComponent1;
    }
    
    public void setDisplayName(final TextComponent textComponent) {
        if (textComponent == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.displayName = textComponent;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    public void setPrefix(@Nullable final TextComponent textComponent) {
        this.prefix = ((textComponent == null) ? new StringTextComponent("") : textComponent.copy());
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    public TextComponent getPrefix() {
        return this.prefix;
    }
    
    public void setSuffix(@Nullable final TextComponent textComponent) {
        this.suffix = ((textComponent == null) ? new StringTextComponent("") : textComponent.copy());
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    public TextComponent getSuffix() {
        return this.suffix;
    }
    
    @Override
    public Collection<String> getPlayerList() {
        return this.playerList;
    }
    
    @Override
    public TextComponent modifyText(final TextComponent textComponent) {
        final TextComponent textComponent2 = new StringTextComponent("").append(this.prefix).append(textComponent).append(this.suffix);
        final TextFormat textFormat3 = this.getColor();
        if (textFormat3 != TextFormat.RESET) {
            textComponent2.applyFormat(textFormat3);
        }
        return textComponent2;
    }
    
    public static TextComponent modifyText(@Nullable final AbstractTeam abstractTeam, final TextComponent textComponent) {
        if (abstractTeam == null) {
            return textComponent.copy();
        }
        return abstractTeam.modifyText(textComponent);
    }
    
    @Override
    public boolean isFriendlyFireAllowed() {
        return this.friendlyFire;
    }
    
    public void setFriendlyFireAllowed(final boolean boolean1) {
        this.friendlyFire = boolean1;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    @Override
    public boolean shouldShowFriendlyInvisibles() {
        return this.showFriendlyInvisibles;
    }
    
    public void setShowFriendlyInvisibles(final boolean boolean1) {
        this.showFriendlyInvisibles = boolean1;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    @Override
    public VisibilityRule getNameTagVisibilityRule() {
        return this.nameTagVisibilityRule;
    }
    
    @Override
    public VisibilityRule getDeathMessageVisibilityRule() {
        return this.deathMessageVisibilityRule;
    }
    
    public void setNameTagVisibilityRule(final VisibilityRule visibilityRule) {
        this.nameTagVisibilityRule = visibilityRule;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    public void setDeathMessageVisibilityRule(final VisibilityRule visibilityRule) {
        this.deathMessageVisibilityRule = visibilityRule;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    @Override
    public CollisionRule getCollisionRule() {
        return this.collisionRule;
    }
    
    public void setCollisionRule(final CollisionRule collisionRule) {
        this.collisionRule = collisionRule;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    public int getFriendlyFlagsBitwise() {
        int integer1 = 0;
        if (this.isFriendlyFireAllowed()) {
            integer1 |= 0x1;
        }
        if (this.shouldShowFriendlyInvisibles()) {
            integer1 |= 0x2;
        }
        return integer1;
    }
    
    @Environment(EnvType.CLIENT)
    public void setFriendlyFlagsBitwise(final int integer) {
        this.setFriendlyFireAllowed((integer & 0x1) > 0);
        this.setShowFriendlyInvisibles((integer & 0x2) > 0);
    }
    
    public void setColor(final TextFormat textFormat) {
        this.color = textFormat;
        this.scoreboard.updateScoreboardTeam(this);
    }
    
    @Override
    public TextFormat getColor() {
        return this.color;
    }
}
