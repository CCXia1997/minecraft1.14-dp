package net.minecraft.text;

import java.util.List;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatUtil;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelectorReader;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;

public class ScoreTextComponent extends AbstractTextComponent implements TextComponentWithSelectors
{
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;
    private String text;
    
    public ScoreTextComponent(final String name, final String string2) {
        this.text = "";
        this.name = name;
        this.objective = string2;
        EntitySelector entitySelector3 = null;
        try {
            final EntitySelectorReader entitySelectorReader4 = new EntitySelectorReader(new StringReader(name));
            entitySelector3 = entitySelectorReader4.read();
        }
        catch (CommandSyntaxException ex) {}
        this.selector = entitySelector3;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getObjective() {
        return this.objective;
    }
    
    public void setText(final String string) {
        this.text = string;
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    private void resolve(final ServerCommandSource source) {
        final MinecraftServer minecraftServer2 = source.getMinecraftServer();
        if (minecraftServer2 != null && minecraftServer2.E() && ChatUtil.isEmpty(this.text)) {
            final Scoreboard scoreboard3 = minecraftServer2.getScoreboard();
            final ScoreboardObjective scoreboardObjective4 = scoreboard3.getNullableObjective(this.objective);
            if (scoreboard3.playerHasObjective(this.name, scoreboardObjective4)) {
                final ScoreboardPlayerScore scoreboardPlayerScore5 = scoreboard3.getPlayerScore(this.name, scoreboardObjective4);
                this.setText(String.format("%d", scoreboardPlayerScore5.getScore()));
            }
            else {
                this.text = "";
            }
        }
    }
    
    @Override
    public ScoreTextComponent copyShallow() {
        final ScoreTextComponent scoreTextComponent1 = new ScoreTextComponent(this.name, this.objective);
        scoreTextComponent1.setText(this.text);
        return scoreTextComponent1;
    }
    
    @Override
    public TextComponent resolve(@Nullable final ServerCommandSource source, @Nullable final Entity entity) throws CommandSyntaxException {
        if (source == null) {
            return this.copyShallow();
        }
        String string3;
        if (this.selector != null) {
            final List<? extends Entity> list4 = this.selector.getEntities(source);
            if (list4.isEmpty()) {
                string3 = this.name;
            }
            else {
                if (list4.size() != 1) {
                    throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
                }
                string3 = ((Entity)list4.get(0)).getEntityName();
            }
        }
        else {
            string3 = this.name;
        }
        final String string4 = (entity != null && string3.equals("*")) ? entity.getEntityName() : string3;
        final ScoreTextComponent scoreTextComponent5 = new ScoreTextComponent(string4, this.objective);
        scoreTextComponent5.setText(this.text);
        scoreTextComponent5.resolve(source);
        return scoreTextComponent5;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ScoreTextComponent) {
            final ScoreTextComponent scoreTextComponent2 = (ScoreTextComponent)o;
            return this.name.equals(scoreTextComponent2.name) && this.objective.equals(scoreTextComponent2.objective) && super.equals(o);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}
