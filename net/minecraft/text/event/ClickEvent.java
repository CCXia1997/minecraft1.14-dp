package net.minecraft.text.event;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public class ClickEvent
{
    private final Action action;
    private final String value;
    
    public ClickEvent(final Action action, final String string) {
        this.action = action;
        this.value = string;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ClickEvent clickEvent2 = (ClickEvent)o;
        if (this.action != clickEvent2.action) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(clickEvent2.value)) {
                return true;
            }
        }
        else if (clickEvent2.value == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.action.hashCode();
        integer1 = 31 * integer1 + ((this.value != null) ? this.value.hashCode() : 0);
        return integer1;
    }
    
    public enum Action
    {
        OPEN_URL("open_url", true), 
        OPEN_FILE("open_file", false), 
        RUN_COMMAND("run_command", true), 
        SUGGEST_COMMAND("suggest_command", true), 
        CHANGE_PAGE("change_page", true);
        
        private static final Map<String, Action> ACTIONS;
        private final boolean safe;
        private final String name;
        
        private Action(final String string1, final boolean boolean2) {
            this.name = string1;
            this.safe = boolean2;
        }
        
        public boolean isSafe() {
            return this.safe;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Action get(final String string) {
            return Action.ACTIONS.get(string);
        }
        
        static {
            ACTIONS = Arrays.<Action>stream(values()).collect(Collectors.toMap(Action::getName, action -> action));
        }
    }
}
