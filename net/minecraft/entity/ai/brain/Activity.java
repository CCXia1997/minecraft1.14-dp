package net.minecraft.entity.ai.brain;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Activity
{
    public static final Activity a;
    public static final Activity b;
    public static final Activity c;
    public static final Activity d;
    public static final Activity e;
    public static final Activity f;
    public static final Activity g;
    public static final Activity h;
    public static final Activity i;
    public static final Activity j;
    private final String id;
    
    private Activity(final String string) {
        this.id = string;
    }
    
    public String getId() {
        return this.id;
    }
    
    private static Activity register(final String string) {
        return Registry.ACTIVITY.<Activity>add(new Identifier(string), new Activity(string));
    }
    
    @Override
    public String toString() {
        return this.getId();
    }
    
    static {
        a = register("core");
        b = register("idle");
        c = register("work");
        d = register("play");
        e = register("rest");
        f = register("meet");
        g = register("panic");
        h = register("raid");
        i = register("pre_raid");
        j = register("hide");
    }
}
