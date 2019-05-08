package net.minecraft.scoreboard;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import java.util.Collection;
import net.minecraft.client.network.packet.TeamS2CPacket;
import java.util.Arrays;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard
{
    private final MinecraftServer server;
    private final Set<ScoreboardObjective> objectives;
    private Runnable[] updateListeners;
    
    public ServerScoreboard(final MinecraftServer minecraftServer) {
        this.objectives = Sets.newHashSet();
        this.updateListeners = new Runnable[0];
        this.server = minecraftServer;
    }
    
    @Override
    public void updateScore(final ScoreboardPlayerScore score) {
        super.updateScore(score);
        if (this.objectives.contains(score.getObjective())) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.a, score.getObjective().getName(), score.getPlayerName(), score.getScore()));
        }
        this.runUpdateListeners();
    }
    
    @Override
    public void updatePlayerScore(final String string) {
        super.updatePlayerScore(string);
        this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.b, null, string, 0));
        this.runUpdateListeners();
    }
    
    @Override
    public void updatePlayerScore(final String playerName, final ScoreboardObjective scoreboardObjective) {
        super.updatePlayerScore(playerName, scoreboardObjective);
        if (this.objectives.contains(scoreboardObjective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.b, scoreboardObjective.getName(), playerName, 0));
        }
        this.runUpdateListeners();
    }
    
    @Override
    public void setObjectiveSlot(final int slot, @Nullable final ScoreboardObjective scoreboardObjective) {
        final ScoreboardObjective scoreboardObjective2 = this.getObjectiveForSlot(slot);
        super.setObjectiveSlot(slot, scoreboardObjective);
        if (scoreboardObjective2 != scoreboardObjective && scoreboardObjective2 != null) {
            if (this.getSlot(scoreboardObjective2) > 0) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, scoreboardObjective));
            }
            else {
                this.removeScoreboardObjective(scoreboardObjective2);
            }
        }
        if (scoreboardObjective != null) {
            if (this.objectives.contains(scoreboardObjective)) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, scoreboardObjective));
            }
            else {
                this.addScoreboardObjective(scoreboardObjective);
            }
        }
        this.runUpdateListeners();
    }
    
    @Override
    public boolean addPlayerToTeam(final String playerName, final Team team) {
        if (super.addPlayerToTeam(playerName, team)) {
            this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.<String>asList(playerName), 3));
            this.runUpdateListeners();
            return true;
        }
        return false;
    }
    
    @Override
    public void removePlayerFromTeam(final String playerName, final Team team) {
        super.removePlayerFromTeam(playerName, team);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.<String>asList(playerName), 4));
        this.runUpdateListeners();
    }
    
    @Override
    public void updateObjective(final ScoreboardObjective scoreboardObjective) {
        super.updateObjective(scoreboardObjective);
        this.runUpdateListeners();
    }
    
    @Override
    public void updateExistingObjective(final ScoreboardObjective scoreboardObjective) {
        super.updateExistingObjective(scoreboardObjective);
        if (this.objectives.contains(scoreboardObjective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(scoreboardObjective, 2));
        }
        this.runUpdateListeners();
    }
    
    @Override
    public void updateRemovedObjective(final ScoreboardObjective scoreboardObjective) {
        super.updateRemovedObjective(scoreboardObjective);
        if (this.objectives.contains(scoreboardObjective)) {
            this.removeScoreboardObjective(scoreboardObjective);
        }
        this.runUpdateListeners();
    }
    
    @Override
    public void updateScoreboardTeamAndPlayers(final Team team) {
        super.updateScoreboardTeamAndPlayers(team);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 0));
        this.runUpdateListeners();
    }
    
    @Override
    public void updateScoreboardTeam(final Team team) {
        super.updateScoreboardTeam(team);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 2));
        this.runUpdateListeners();
    }
    
    @Override
    public void updateRemovedTeam(final Team team) {
        super.updateRemovedTeam(team);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 1));
        this.runUpdateListeners();
    }
    
    public void addUpdateListener(final Runnable listener) {
        (this.updateListeners = Arrays.<Runnable>copyOf(this.updateListeners, this.updateListeners.length + 1))[this.updateListeners.length - 1] = listener;
    }
    
    protected void runUpdateListeners() {
        for (final Runnable runnable4 : this.updateListeners) {
            runnable4.run();
        }
    }
    
    public List<Packet<?>> createChangePackets(final ScoreboardObjective objective) {
        final List<Packet<?>> list2 = Lists.newArrayList();
        list2.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 0));
        for (int integer3 = 0; integer3 < 19; ++integer3) {
            if (this.getObjectiveForSlot(integer3) == objective) {
                list2.add(new ScoreboardDisplayS2CPacket(integer3, objective));
            }
        }
        for (final ScoreboardPlayerScore scoreboardPlayerScore4 : this.getAllPlayerScores(objective)) {
            list2.add(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.a, scoreboardPlayerScore4.getObjective().getName(), scoreboardPlayerScore4.getPlayerName(), scoreboardPlayerScore4.getScore()));
        }
        return list2;
    }
    
    public void addScoreboardObjective(final ScoreboardObjective objective) {
        final List<Packet<?>> list2 = this.createChangePackets(objective);
        for (final ServerPlayerEntity serverPlayerEntity4 : this.server.getPlayerManager().getPlayerList()) {
            for (final Packet<?> packet6 : list2) {
                serverPlayerEntity4.networkHandler.sendPacket(packet6);
            }
        }
        this.objectives.add(objective);
    }
    
    public List<Packet<?>> createRemovePackets(final ScoreboardObjective objective) {
        final List<Packet<?>> list2 = Lists.newArrayList();
        list2.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 1));
        for (int integer3 = 0; integer3 < 19; ++integer3) {
            if (this.getObjectiveForSlot(integer3) == objective) {
                list2.add(new ScoreboardDisplayS2CPacket(integer3, objective));
            }
        }
        return list2;
    }
    
    public void removeScoreboardObjective(final ScoreboardObjective objective) {
        final List<Packet<?>> list2 = this.createRemovePackets(objective);
        for (final ServerPlayerEntity serverPlayerEntity4 : this.server.getPlayerManager().getPlayerList()) {
            for (final Packet<?> packet6 : list2) {
                serverPlayerEntity4.networkHandler.sendPacket(packet6);
            }
        }
        this.objectives.remove(objective);
    }
    
    public int getSlot(final ScoreboardObjective objective) {
        int integer2 = 0;
        for (int integer3 = 0; integer3 < 19; ++integer3) {
            if (this.getObjectiveForSlot(integer3) == objective) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    public enum UpdateMode
    {
        a, 
        b;
    }
}
