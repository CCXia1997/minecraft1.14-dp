package net.minecraft.scoreboard;

import javax.annotation.Nullable;
import java.util.Comparator;

public class ScoreboardPlayerScore
{
    public static final Comparator<ScoreboardPlayerScore> COMPARATOR;
    private final Scoreboard scoreboard;
    @Nullable
    private final ScoreboardObjective objective;
    private final String playerName;
    private int score;
    private boolean locked;
    private boolean forceUpdate;
    
    public ScoreboardPlayerScore(final Scoreboard scoreboard, final ScoreboardObjective objective, final String string) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.playerName = string;
        this.locked = true;
        this.forceUpdate = true;
    }
    
    public void incrementScore(final int integer) {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        this.setScore(this.getScore() + integer);
    }
    
    public void incrementScore() {
        this.incrementScore(1);
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void clearScore() {
        this.setScore(0);
    }
    
    public void setScore(final int integer) {
        final int integer2 = this.score;
        this.score = integer;
        if (integer2 != integer || this.forceUpdate) {
            this.forceUpdate = false;
            this.getScoreboard().updateScore(this);
        }
    }
    
    @Nullable
    public ScoreboardObjective getObjective() {
        return this.objective;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean boolean1) {
        this.locked = boolean1;
    }
    
    static {
        COMPARATOR = ((scoreboardPlayerScore1, scoreboardPlayerScore2) -> {
            if (scoreboardPlayerScore1.getScore() > scoreboardPlayerScore2.getScore()) {
                return 1;
            }
            else if (scoreboardPlayerScore1.getScore() < scoreboardPlayerScore2.getScore()) {
                return -1;
            }
            else {
                return scoreboardPlayerScore2.getPlayerName().compareToIgnoreCase(scoreboardPlayerScore1.getPlayerName());
            }
        });
    }
}
