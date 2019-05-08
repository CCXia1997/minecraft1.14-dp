package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import net.minecraft.text.TextFormat;
import com.google.common.collect.Maps;
import java.util.function.Function;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.Optional;
import java.util.Map;

public class ScoreboardCriterion
{
    public static final Map<String, ScoreboardCriterion> OBJECTIVES;
    public static final ScoreboardCriterion DUMMY;
    public static final ScoreboardCriterion TRIGGER;
    public static final ScoreboardCriterion DEATH_COUNT;
    public static final ScoreboardCriterion PLAYER_KILL_COUNT;
    public static final ScoreboardCriterion TOTAL_KILL_COUNT;
    public static final ScoreboardCriterion HEALTH;
    public static final ScoreboardCriterion FOOD;
    public static final ScoreboardCriterion AIR;
    public static final ScoreboardCriterion ARMOR;
    public static final ScoreboardCriterion XP;
    public static final ScoreboardCriterion LEVEL;
    public static final ScoreboardCriterion[] TEAM_KILLS;
    public static final ScoreboardCriterion[] KILLED_BY_TEAMS;
    private final String name;
    private final boolean readOnly;
    private final RenderType criterionType;
    
    public ScoreboardCriterion(final String string) {
        this(string, false, RenderType.INTEGER);
    }
    
    protected ScoreboardCriterion(final String string, final boolean boolean2, final RenderType renderType) {
        this.name = string;
        this.readOnly = boolean2;
        this.criterionType = renderType;
        ScoreboardCriterion.OBJECTIVES.put(string, this);
    }
    
    public static Optional<ScoreboardCriterion> createStatCriterion(final String string) {
        if (ScoreboardCriterion.OBJECTIVES.containsKey(string)) {
            return Optional.<ScoreboardCriterion>of(ScoreboardCriterion.OBJECTIVES.get(string));
        }
        final int integer2 = string.indexOf(58);
        if (integer2 < 0) {
            return Optional.<ScoreboardCriterion>empty();
        }
        return Registry.STAT_TYPE.getOrEmpty(Identifier.createSplit(string.substring(0, integer2), '.')).<ScoreboardCriterion>flatMap(statType -> ScoreboardCriterion.createStatCriterion(statType, Identifier.createSplit(string.substring(integer2 + 1), '.')));
    }
    
    private static <T> Optional<ScoreboardCriterion> createStatCriterion(final StatType<T> statType, final Identifier id) {
        return statType.getRegistry().getOrEmpty(id).<ScoreboardCriterion>map(statType::getOrCreateStat);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    public RenderType getCriterionType() {
        return this.criterionType;
    }
    
    static {
        OBJECTIVES = Maps.newHashMap();
        DUMMY = new ScoreboardCriterion("dummy");
        TRIGGER = new ScoreboardCriterion("trigger");
        DEATH_COUNT = new ScoreboardCriterion("deathCount");
        PLAYER_KILL_COUNT = new ScoreboardCriterion("playerKillCount");
        TOTAL_KILL_COUNT = new ScoreboardCriterion("totalKillCount");
        HEALTH = new ScoreboardCriterion("health", true, RenderType.HEARTS);
        FOOD = new ScoreboardCriterion("food", true, RenderType.INTEGER);
        AIR = new ScoreboardCriterion("air", true, RenderType.INTEGER);
        ARMOR = new ScoreboardCriterion("armor", true, RenderType.INTEGER);
        XP = new ScoreboardCriterion("xp", true, RenderType.INTEGER);
        LEVEL = new ScoreboardCriterion("level", true, RenderType.INTEGER);
        TEAM_KILLS = new ScoreboardCriterion[] { new ScoreboardCriterion("teamkill." + TextFormat.BLACK.getName()), new ScoreboardCriterion("teamkill." + TextFormat.b.getName()), new ScoreboardCriterion("teamkill." + TextFormat.c.getName()), new ScoreboardCriterion("teamkill." + TextFormat.d.getName()), new ScoreboardCriterion("teamkill." + TextFormat.e.getName()), new ScoreboardCriterion("teamkill." + TextFormat.f.getName()), new ScoreboardCriterion("teamkill." + TextFormat.g.getName()), new ScoreboardCriterion("teamkill." + TextFormat.h.getName()), new ScoreboardCriterion("teamkill." + TextFormat.i.getName()), new ScoreboardCriterion("teamkill." + TextFormat.j.getName()), new ScoreboardCriterion("teamkill." + TextFormat.k.getName()), new ScoreboardCriterion("teamkill." + TextFormat.l.getName()), new ScoreboardCriterion("teamkill." + TextFormat.m.getName()), new ScoreboardCriterion("teamkill." + TextFormat.n.getName()), new ScoreboardCriterion("teamkill." + TextFormat.o.getName()), new ScoreboardCriterion("teamkill." + TextFormat.p.getName()) };
        KILLED_BY_TEAMS = new ScoreboardCriterion[] { new ScoreboardCriterion("killedByTeam." + TextFormat.BLACK.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.b.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.c.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.d.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.e.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.f.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.g.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.h.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.i.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.j.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.k.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.l.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.m.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.n.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.o.getName()), new ScoreboardCriterion("killedByTeam." + TextFormat.p.getName()) };
    }
    
    public enum RenderType
    {
        INTEGER("integer"), 
        HEARTS("hearts");
        
        private final String name;
        private static final Map<String, RenderType> CRITERION_TYPES;
        
        private RenderType(final String string1) {
            this.name = string1;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static RenderType getType(final String string) {
            return RenderType.CRITERION_TYPES.getOrDefault(string, RenderType.INTEGER);
        }
        
        static {
            final ImmutableMap.Builder<String, RenderType> builder = ImmutableMap.<String, RenderType>builder();
            for (final RenderType renderType3 : values()) {
                builder.put(renderType3.name, renderType3);
            }
            CRITERION_TYPES = builder.build();
        }
    }
}
