package net.minecraft.scoreboard;

import net.minecraft.text.Style;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

public class ScoreboardObjective
{
    private final Scoreboard scoreboard;
    private final String name;
    private final ScoreboardCriterion criterion;
    private TextComponent displayName;
    private ScoreboardCriterion.RenderType renderType;
    
    public ScoreboardObjective(final Scoreboard scoreboard, final String name, final ScoreboardCriterion criterion, final TextComponent displayName, final ScoreboardCriterion.RenderType renderType) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.criterion = criterion;
        this.displayName = displayName;
        this.renderType = renderType;
    }
    
    @Environment(EnvType.CLIENT)
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ScoreboardCriterion getCriterion() {
        return this.criterion;
    }
    
    public TextComponent getDisplayName() {
        return this.displayName;
    }
    
    public TextComponent getTextComponent() {
        final HoverEvent hoverEvent;
        return TextFormatter.bracketed(this.displayName.copy().modifyStyle(style -> {
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getName()));
            style.setHoverEvent(hoverEvent);
        }));
    }
    
    public void setDisplayName(final TextComponent textComponent) {
        this.displayName = textComponent;
        this.scoreboard.updateExistingObjective(this);
    }
    
    public ScoreboardCriterion.RenderType getRenderType() {
        return this.renderType;
    }
    
    public void setRenderType(final ScoreboardCriterion.RenderType renderType) {
        this.renderType = renderType;
        this.scoreboard.updateExistingObjective(this);
    }
}
