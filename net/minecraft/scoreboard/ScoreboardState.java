package net.minecraft.scoreboard;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.PersistentState;

public class ScoreboardState extends PersistentState
{
    private static final Logger LOGGER;
    private Scoreboard scoreboard;
    private CompoundTag tag;
    
    public ScoreboardState() {
        super("scoreboard");
    }
    
    public void setScoreboard(final Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        if (this.tag != null) {
            this.fromTag(this.tag);
        }
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        if (this.scoreboard == null) {
            this.tag = compoundTag;
            return;
        }
        this.deserializeObjectives(compoundTag.getList("Objectives", 10));
        this.scoreboard.fromTag(compoundTag.getList("PlayerScores", 10));
        if (compoundTag.containsKey("DisplaySlots", 10)) {
            this.deserializeDisplaySlots(compoundTag.getCompound("DisplaySlots"));
        }
        if (compoundTag.containsKey("Teams", 9)) {
            this.deserializeTeams(compoundTag.getList("Teams", 10));
        }
    }
    
    protected void deserializeTeams(final ListTag listTag) {
        for (int integer2 = 0; integer2 < listTag.size(); ++integer2) {
            final CompoundTag compoundTag3 = listTag.getCompoundTag(integer2);
            String string4 = compoundTag3.getString("Name");
            if (string4.length() > 16) {
                string4 = string4.substring(0, 16);
            }
            final Team team5 = this.scoreboard.addTeam(string4);
            final TextComponent textComponent6 = TextComponent.Serializer.fromJsonString(compoundTag3.getString("DisplayName"));
            if (textComponent6 != null) {
                team5.setDisplayName(textComponent6);
            }
            if (compoundTag3.containsKey("TeamColor", 8)) {
                team5.setColor(TextFormat.getFormatByName(compoundTag3.getString("TeamColor")));
            }
            if (compoundTag3.containsKey("AllowFriendlyFire", 99)) {
                team5.setFriendlyFireAllowed(compoundTag3.getBoolean("AllowFriendlyFire"));
            }
            if (compoundTag3.containsKey("SeeFriendlyInvisibles", 99)) {
                team5.setShowFriendlyInvisibles(compoundTag3.getBoolean("SeeFriendlyInvisibles"));
            }
            if (compoundTag3.containsKey("MemberNamePrefix", 8)) {
                final TextComponent textComponent7 = TextComponent.Serializer.fromJsonString(compoundTag3.getString("MemberNamePrefix"));
                if (textComponent7 != null) {
                    team5.setPrefix(textComponent7);
                }
            }
            if (compoundTag3.containsKey("MemberNameSuffix", 8)) {
                final TextComponent textComponent7 = TextComponent.Serializer.fromJsonString(compoundTag3.getString("MemberNameSuffix"));
                if (textComponent7 != null) {
                    team5.setSuffix(textComponent7);
                }
            }
            if (compoundTag3.containsKey("NameTagVisibility", 8)) {
                final AbstractTeam.VisibilityRule visibilityRule7 = AbstractTeam.VisibilityRule.getRule(compoundTag3.getString("NameTagVisibility"));
                if (visibilityRule7 != null) {
                    team5.setNameTagVisibilityRule(visibilityRule7);
                }
            }
            if (compoundTag3.containsKey("DeathMessageVisibility", 8)) {
                final AbstractTeam.VisibilityRule visibilityRule7 = AbstractTeam.VisibilityRule.getRule(compoundTag3.getString("DeathMessageVisibility"));
                if (visibilityRule7 != null) {
                    team5.setDeathMessageVisibilityRule(visibilityRule7);
                }
            }
            if (compoundTag3.containsKey("CollisionRule", 8)) {
                final AbstractTeam.CollisionRule collisionRule7 = AbstractTeam.CollisionRule.getRule(compoundTag3.getString("CollisionRule"));
                if (collisionRule7 != null) {
                    team5.setCollisionRule(collisionRule7);
                }
            }
            this.deserializeTeamPlayers(team5, compoundTag3.getList("Players", 8));
        }
    }
    
    protected void deserializeTeamPlayers(final Team team, final ListTag listTag) {
        for (int integer3 = 0; integer3 < listTag.size(); ++integer3) {
            this.scoreboard.addPlayerToTeam(listTag.getString(integer3), team);
        }
    }
    
    protected void deserializeDisplaySlots(final CompoundTag compoundTag) {
        for (int integer2 = 0; integer2 < 19; ++integer2) {
            if (compoundTag.containsKey("slot_" + integer2, 8)) {
                final String string3 = compoundTag.getString("slot_" + integer2);
                final ScoreboardObjective scoreboardObjective4 = this.scoreboard.getNullableObjective(string3);
                this.scoreboard.setObjectiveSlot(integer2, scoreboardObjective4);
            }
        }
    }
    
    protected void deserializeObjectives(final ListTag listTag) {
        for (int integer2 = 0; integer2 < listTag.size(); ++integer2) {
            final CompoundTag compoundTag3 = listTag.getCompoundTag(integer2);
            final CompoundTag compoundTag4;
            String string3;
            final TextComponent textComponent4;
            final ScoreboardCriterion.RenderType renderType5;
            ScoreboardCriterion.createStatCriterion(compoundTag3.getString("CriteriaName")).ifPresent(scoreboardCriterion -> {
                string3 = compoundTag4.getString("Name");
                if (string3.length() > 16) {
                    string3 = string3.substring(0, 16);
                }
                textComponent4 = TextComponent.Serializer.fromJsonString(compoundTag4.getString("DisplayName"));
                renderType5 = ScoreboardCriterion.RenderType.getType(compoundTag4.getString("RenderType"));
                this.scoreboard.addObjective(string3, scoreboardCriterion, textComponent4, renderType5);
                return;
            });
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        if (this.scoreboard == null) {
            ScoreboardState.LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
            return compoundTag;
        }
        compoundTag.put("Objectives", this.serializeObjectives());
        compoundTag.put("PlayerScores", this.scoreboard.toTag());
        compoundTag.put("Teams", this.serializeTeams());
        this.serializeSlots(compoundTag);
        return compoundTag;
    }
    
    protected ListTag serializeTeams() {
        final ListTag listTag1 = new ListTag();
        final Collection<Team> collection2 = this.scoreboard.getTeams();
        for (final Team team4 : collection2) {
            final CompoundTag compoundTag5 = new CompoundTag();
            compoundTag5.putString("Name", team4.getName());
            compoundTag5.putString("DisplayName", TextComponent.Serializer.toJsonString(team4.getDisplayName()));
            if (team4.getColor().getId() >= 0) {
                compoundTag5.putString("TeamColor", team4.getColor().getName());
            }
            compoundTag5.putBoolean("AllowFriendlyFire", team4.isFriendlyFireAllowed());
            compoundTag5.putBoolean("SeeFriendlyInvisibles", team4.shouldShowFriendlyInvisibles());
            compoundTag5.putString("MemberNamePrefix", TextComponent.Serializer.toJsonString(team4.getPrefix()));
            compoundTag5.putString("MemberNameSuffix", TextComponent.Serializer.toJsonString(team4.getSuffix()));
            compoundTag5.putString("NameTagVisibility", team4.getNameTagVisibilityRule().name);
            compoundTag5.putString("DeathMessageVisibility", team4.getDeathMessageVisibilityRule().name);
            compoundTag5.putString("CollisionRule", team4.getCollisionRule().name);
            final ListTag listTag2 = new ListTag();
            for (final String string8 : team4.getPlayerList()) {
                ((AbstractList<StringTag>)listTag2).add(new StringTag(string8));
            }
            compoundTag5.put("Players", listTag2);
            ((AbstractList<CompoundTag>)listTag1).add(compoundTag5);
        }
        return listTag1;
    }
    
    protected void serializeSlots(final CompoundTag compoundTag) {
        final CompoundTag compoundTag2 = new CompoundTag();
        boolean boolean3 = false;
        for (int integer4 = 0; integer4 < 19; ++integer4) {
            final ScoreboardObjective scoreboardObjective5 = this.scoreboard.getObjectiveForSlot(integer4);
            if (scoreboardObjective5 != null) {
                compoundTag2.putString("slot_" + integer4, scoreboardObjective5.getName());
                boolean3 = true;
            }
        }
        if (boolean3) {
            compoundTag.put("DisplaySlots", compoundTag2);
        }
    }
    
    protected ListTag serializeObjectives() {
        final ListTag listTag1 = new ListTag();
        final Collection<ScoreboardObjective> collection2 = this.scoreboard.getObjectives();
        for (final ScoreboardObjective scoreboardObjective4 : collection2) {
            if (scoreboardObjective4.getCriterion() == null) {
                continue;
            }
            final CompoundTag compoundTag5 = new CompoundTag();
            compoundTag5.putString("Name", scoreboardObjective4.getName());
            compoundTag5.putString("CriteriaName", scoreboardObjective4.getCriterion().getName());
            compoundTag5.putString("DisplayName", TextComponent.Serializer.toJsonString(scoreboardObjective4.getDisplayName()));
            compoundTag5.putString("RenderType", scoreboardObjective4.getRenderType().getName());
            ((AbstractList<CompoundTag>)listTag1).add(compoundTag5);
        }
        return listTag1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
