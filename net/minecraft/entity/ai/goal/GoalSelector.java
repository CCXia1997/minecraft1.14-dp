package net.minecraft.entity.ai.goal;

import org.apache.logging.log4j.LogManager;
import java.util.stream.Stream;
import java.util.function.Predicate;
import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import net.minecraft.util.profiler.Profiler;
import java.util.Set;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class GoalSelector
{
    private static final Logger LOGGER;
    private static final WeightedGoal activeGoal;
    private final Map<Goal.Control, WeightedGoal> goalsByControl;
    private final Set<WeightedGoal> goals;
    private final Profiler profiler;
    private final EnumSet<Goal.Control> disabledControls;
    private int timeInterval;
    
    public GoalSelector(final Profiler profiler) {
        this.goalsByControl = new EnumMap<Goal.Control, WeightedGoal>(Goal.Control.class);
        this.goals = Sets.newLinkedHashSet();
        this.disabledControls = EnumSet.<Goal.Control>noneOf(Goal.Control.class);
        this.timeInterval = 3;
        this.profiler = profiler;
    }
    
    public void add(final int weight, final Goal goal) {
        this.goals.add(new WeightedGoal(weight, goal));
    }
    
    public void remove(final Goal goal) {
        this.goals.stream().filter(weightedGoal -> weightedGoal.getGoal() == goal).filter(WeightedGoal::isRunning).forEach(WeightedGoal::stop);
        this.goals.removeIf(weightedGoal -> weightedGoal.getGoal() == goal);
    }
    
    public void tick() {
        this.profiler.push("goalCleanup");
        this.getRunningGoals().filter(weightedGoal -> !weightedGoal.isRunning() || weightedGoal.getControls().stream().anyMatch(this.disabledControls::contains) || !weightedGoal.shouldContinue()).forEach(Goal::stop);
        this.goalsByControl.forEach((control, weightedGoal) -> {
            if (!weightedGoal.isRunning()) {
                this.goalsByControl.remove(control);
            }
            return;
        });
        this.profiler.pop();
        this.profiler.push("goalUpdate");
        final WeightedGoal weightedGoal2;
        this.goals.stream().filter(weightedGoal -> !weightedGoal.isRunning()).filter(weightedGoal -> weightedGoal.getControls().stream().noneMatch(this.disabledControls::contains)).filter(weightedGoal -> weightedGoal.getControls().stream().allMatch(control -> this.goalsByControl.getOrDefault(control, GoalSelector.activeGoal).canBeReplacedBy(weightedGoal))).filter(WeightedGoal::canStart).forEach(weightedGoal -> {
            weightedGoal.getControls().forEach(control -> {
                weightedGoal2 = this.goalsByControl.getOrDefault(control, GoalSelector.activeGoal);
                weightedGoal2.stop();
                this.goalsByControl.put(control, weightedGoal);
                return;
            });
            weightedGoal.start();
            return;
        });
        this.profiler.pop();
        this.profiler.push("goalTick");
        this.getRunningGoals().forEach(WeightedGoal::tick);
        this.profiler.pop();
    }
    
    public Stream<WeightedGoal> getRunningGoals() {
        return this.goals.stream().filter(WeightedGoal::isRunning);
    }
    
    public void disableControl(final Goal.Control control) {
        this.disabledControls.add(control);
    }
    
    public void enableControl(final Goal.Control control) {
        this.disabledControls.remove(control);
    }
    
    public void setControlEnabled(final Goal.Control control, final boolean enabled) {
        if (enabled) {
            this.enableControl(control);
        }
        else {
            this.disableControl(control);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        activeGoal = new WeightedGoal(Integer.MAX_VALUE, new Goal() {
            @Override
            public boolean canStart() {
                return false;
            }
        }) {
            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }
}
