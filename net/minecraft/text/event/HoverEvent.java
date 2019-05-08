package net.minecraft.text.event;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.text.TextComponent;

public class HoverEvent
{
    private final Action action;
    private final TextComponent value;
    
    public HoverEvent(final Action action, final TextComponent textComponent) {
        this.action = action;
        this.value = textComponent;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public TextComponent getValue() {
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
        final HoverEvent hoverEvent2 = (HoverEvent)o;
        if (this.action != hoverEvent2.action) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(hoverEvent2.value)) {
                return true;
            }
        }
        else if (hoverEvent2.value == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.action.hashCode();
        integer1 = 31 * integer1 + ((this.value != null) ? this.value.hashCode() : 0);
        return integer1;
    }
    
    public enum Action
    {
        SHOW_TEXT("show_text", true), 
        SHOW_ITEM("show_item", true), 
        SHOW_ENTITY("show_entity", true);
        
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
