package net.minecraft.scoreboard;

import java.util.AbstractList;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.nbt.ListTag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormat;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import com.google.common.collect.Lists;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

public class Scoreboard
{
    private final Map<String, ScoreboardObjective> objectives;
    private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion;
    private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives;
    private final ScoreboardObjective[] objectiveSlots;
    private final Map<String, Team> teams;
    private final Map<String, Team> teamsByPlayer;
    private static String[] displaySlotNames;
    
    public Scoreboard() {
        this.objectives = Maps.newHashMap();
        this.objectivesByCriterion = Maps.newHashMap();
        this.playerObjectives = Maps.newHashMap();
        this.objectiveSlots = new ScoreboardObjective[19];
        this.teams = Maps.newHashMap();
        this.teamsByPlayer = Maps.newHashMap();
    }
    
    @Environment(EnvType.CLIENT)
    public boolean containsObjective(final String name) {
        return this.objectives.containsKey(name);
    }
    
    public ScoreboardObjective getObjective(final String name) {
        return this.objectives.get(name);
    }
    
    @Nullable
    public ScoreboardObjective getNullableObjective(@Nullable final String name) {
        return this.objectives.get(name);
    }
    
    public ScoreboardObjective addObjective(final String name, final ScoreboardCriterion criterion, final TextComponent displayName, final ScoreboardCriterion.RenderType renderType) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
        }
        if (this.objectives.containsKey(name)) {
            throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
        }
        final ScoreboardObjective scoreboardObjective5 = new ScoreboardObjective(this, name, criterion, displayName, renderType);
        this.objectivesByCriterion.computeIfAbsent(criterion, scoreboardCriterion -> Lists.newArrayList()).add(scoreboardObjective5);
        this.objectives.put(name, scoreboardObjective5);
        this.updateObjective(scoreboardObjective5);
        return scoreboardObjective5;
    }
    
    public final void forEachScore(final ScoreboardCriterion criterion, final String player, final Consumer<ScoreboardPlayerScore> action) {
        this.objectivesByCriterion.getOrDefault(criterion, Collections.<ScoreboardObjective>emptyList()).forEach(scoreboardObjective -> action.accept(this.getPlayerScore(player, scoreboardObjective)));
    }
    
    public boolean playerHasObjective(final String playerName, final ScoreboardObjective scoreboardObjective) {
        final Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = this.playerObjectives.get(playerName);
        if (map3 == null) {
            return false;
        }
        final ScoreboardPlayerScore scoreboardPlayerScore4 = map3.get(scoreboardObjective);
        return scoreboardPlayerScore4 != null;
    }
    
    public ScoreboardPlayerScore getPlayerScore(final String player, final ScoreboardObjective scoreboardObjective) {
        if (player.length() > 40) {
            throw new IllegalArgumentException("The player name '" + player + "' is too long!");
        }
        final Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = this.playerObjectives.computeIfAbsent(player, string -> Maps.newHashMap());
        final ScoreboardPlayerScore scoreboardPlayerScore3;
        return map3.computeIfAbsent(scoreboardObjective, scoreboardObjective -> {
            scoreboardPlayerScore3 = new ScoreboardPlayerScore(this, scoreboardObjective, player);
            scoreboardPlayerScore3.setScore(0);
            return scoreboardPlayerScore3;
        });
    }
    
    public Collection<ScoreboardPlayerScore> getAllPlayerScores(final ScoreboardObjective scoreboardObjective) {
        final List<ScoreboardPlayerScore> list2 = Lists.newArrayList();
        for (final Map<ScoreboardObjective, ScoreboardPlayerScore> map4 : this.playerObjectives.values()) {
            final ScoreboardPlayerScore scoreboardPlayerScore5 = map4.get(scoreboardObjective);
            if (scoreboardPlayerScore5 != null) {
                list2.add(scoreboardPlayerScore5);
            }
        }
        Collections.<ScoreboardPlayerScore>sort(list2, ScoreboardPlayerScore.COMPARATOR);
        return list2;
    }
    
    public Collection<ScoreboardObjective> getObjectives() {
        return this.objectives.values();
    }
    
    public Collection<String> getObjectiveNames() {
        return this.objectives.keySet();
    }
    
    public Collection<String> getKnownPlayers() {
        return Lists.newArrayList(this.playerObjectives.keySet());
    }
    
    public void resetPlayerScore(final String playerName, @Nullable final ScoreboardObjective scoreboardObjective) {
        if (scoreboardObjective == null) {
            final Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = this.playerObjectives.remove(playerName);
            if (map3 != null) {
                this.updatePlayerScore(playerName);
            }
        }
        else {
            final Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = this.playerObjectives.get(playerName);
            if (map3 != null) {
                final ScoreboardPlayerScore scoreboardPlayerScore4 = map3.remove(scoreboardObjective);
                if (map3.size() < 1) {
                    final Map<ScoreboardObjective, ScoreboardPlayerScore> map4 = this.playerObjectives.remove(playerName);
                    if (map4 != null) {
                        this.updatePlayerScore(playerName);
                    }
                }
                else if (scoreboardPlayerScore4 != null) {
                    this.updatePlayerScore(playerName, scoreboardObjective);
                }
            }
        }
    }
    
    public Map<ScoreboardObjective, ScoreboardPlayerScore> getPlayerObjectives(final String string) {
        Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = this.playerObjectives.get(string);
        if (map2 == null) {
            map2 = Maps.newHashMap();
        }
        return map2;
    }
    
    public void removeObjective(final ScoreboardObjective scoreboardObjective) {
        this.objectives.remove(scoreboardObjective.getName());
        for (int integer2 = 0; integer2 < 19; ++integer2) {
            if (this.getObjectiveForSlot(integer2) == scoreboardObjective) {
                this.setObjectiveSlot(integer2, null);
            }
        }
        final List<ScoreboardObjective> list2 = this.objectivesByCriterion.get(scoreboardObjective.getCriterion());
        if (list2 != null) {
            list2.remove(scoreboardObjective);
        }
        for (final Map<ScoreboardObjective, ScoreboardPlayerScore> map4 : this.playerObjectives.values()) {
            map4.remove(scoreboardObjective);
        }
        this.updateRemovedObjective(scoreboardObjective);
    }
    
    public void setObjectiveSlot(final int slot, @Nullable final ScoreboardObjective scoreboardObjective) {
        this.objectiveSlots[slot] = scoreboardObjective;
    }
    
    @Nullable
    public ScoreboardObjective getObjectiveForSlot(final int integer) {
        return this.objectiveSlots[integer];
    }
    
    public Team getTeam(final String string) {
        return this.teams.get(string);
    }
    
    public Team addTeam(final String string) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The team name '" + string + "' is too long!");
        }
        Team team2 = this.getTeam(string);
        if (team2 != null) {
            throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
        }
        team2 = new Team(this, string);
        this.teams.put(string, team2);
        this.updateScoreboardTeamAndPlayers(team2);
        return team2;
    }
    
    public void removeTeam(final Team team) {
        this.teams.remove(team.getName());
        for (final String string3 : team.getPlayerList()) {
            this.teamsByPlayer.remove(string3);
        }
        this.updateRemovedTeam(team);
    }
    
    public boolean addPlayerToTeam(final String playerName, final Team team) {
        if (playerName.length() > 40) {
            throw new IllegalArgumentException("The player name '" + playerName + "' is too long!");
        }
        if (this.getPlayerTeam(playerName) != null) {
            this.clearPlayerTeam(playerName);
        }
        this.teamsByPlayer.put(playerName, team);
        return team.getPlayerList().add(playerName);
    }
    
    public boolean clearPlayerTeam(final String string) {
        final Team team2 = this.getPlayerTeam(string);
        if (team2 != null) {
            this.removePlayerFromTeam(string, team2);
            return true;
        }
        return false;
    }
    
    public void removePlayerFromTeam(final String playerName, final Team team) {
        if (this.getPlayerTeam(playerName) != team) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
        }
        this.teamsByPlayer.remove(playerName);
        team.getPlayerList().remove(playerName);
    }
    
    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }
    
    public Collection<Team> getTeams() {
        return this.teams.values();
    }
    
    @Nullable
    public Team getPlayerTeam(final String string) {
        return this.teamsByPlayer.get(string);
    }
    
    public void updateObjective(final ScoreboardObjective scoreboardObjective) {
    }
    
    public void updateExistingObjective(final ScoreboardObjective scoreboardObjective) {
    }
    
    public void updateRemovedObjective(final ScoreboardObjective scoreboardObjective) {
    }
    
    public void updateScore(final ScoreboardPlayerScore score) {
    }
    
    public void updatePlayerScore(final String string) {
    }
    
    public void updatePlayerScore(final String playerName, final ScoreboardObjective scoreboardObjective) {
    }
    
    public void updateScoreboardTeamAndPlayers(final Team team) {
    }
    
    public void updateScoreboardTeam(final Team team) {
    }
    
    public void updateRemovedTeam(final Team team) {
    }
    
    public static String getDisplaySlotName(final int slotId) {
        switch (slotId) {
            case 0: {
                return "list";
            }
            case 1: {
                return "sidebar";
            }
            case 2: {
                return "belowName";
            }
            default: {
                if (slotId >= 3 && slotId <= 18) {
                    final TextFormat textFormat2 = TextFormat.byId(slotId - 3);
                    if (textFormat2 != null && textFormat2 != TextFormat.RESET) {
                        return "sidebar.team." + textFormat2.getName();
                    }
                }
                return null;
            }
        }
    }
    
    public static int getDisplaySlotId(final String slotName) {
        if ("list".equalsIgnoreCase(slotName)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(slotName)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(slotName)) {
            return 2;
        }
        if (slotName.startsWith("sidebar.team.")) {
            final String string2 = slotName.substring("sidebar.team.".length());
            final TextFormat textFormat3 = TextFormat.getFormatByName(string2);
            if (textFormat3 != null && textFormat3.getId() >= 0) {
                return textFormat3.getId() + 3;
            }
        }
        return -1;
    }
    
    public static String[] getDisplaySlotNames() {
        if (Scoreboard.displaySlotNames == null) {
            Scoreboard.displaySlotNames = new String[19];
            for (int integer1 = 0; integer1 < 19; ++integer1) {
                Scoreboard.displaySlotNames[integer1] = getDisplaySlotName(integer1);
            }
        }
        return Scoreboard.displaySlotNames;
    }
    
    public void resetEntityScore(final Entity entity) {
        if (entity == null || entity instanceof PlayerEntity || entity.isAlive()) {
            return;
        }
        final String string2 = entity.getUuidAsString();
        this.resetPlayerScore(string2, null);
        this.clearPlayerTeam(string2);
    }
    
    protected ListTag toTag() {
        final ListTag listTag1 = new ListTag();
        final CompoundTag compoundTag3;
        final AbstractList<CompoundTag> list;
        this.playerObjectives.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getObjective() != null).forEach(scoreboardPlayerScore -> {
            compoundTag3 = new CompoundTag();
            compoundTag3.putString("Name", scoreboardPlayerScore.getPlayerName());
            compoundTag3.putString("Objective", scoreboardPlayerScore.getObjective().getName());
            compoundTag3.putInt("Score", scoreboardPlayerScore.getScore());
            compoundTag3.putBoolean("Locked", scoreboardPlayerScore.isLocked());
            list.add(compoundTag3);
        }));
        return listTag1;
    }
    
    protected void fromTag(final ListTag listTag) {
        for (int integer2 = 0; integer2 < listTag.size(); ++integer2) {
            final CompoundTag compoundTag3 = listTag.getCompoundTag(integer2);
            final ScoreboardObjective scoreboardObjective4 = this.getObjective(compoundTag3.getString("Objective"));
            String string5 = compoundTag3.getString("Name");
            if (string5.length() > 40) {
                string5 = string5.substring(0, 40);
            }
            final ScoreboardPlayerScore scoreboardPlayerScore6 = this.getPlayerScore(string5, scoreboardObjective4);
            scoreboardPlayerScore6.setScore(compoundTag3.getInt("Score"));
            if (compoundTag3.containsKey("Locked")) {
                scoreboardPlayerScore6.setLocked(compoundTag3.getBoolean("Locked"));
            }
        }
    }
}
