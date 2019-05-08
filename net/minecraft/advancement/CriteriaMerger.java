package net.minecraft.advancement;

import java.util.Iterator;
import java.util.Collection;

public interface CriteriaMerger
{
    public static final CriteriaMerger AND = collection -> {
        arr2 = new String[collection.size()][];
        integer3 = 0;
        collection.iterator();
        while (iterator.hasNext()) {
            string5 = iterator.next();
            integer3++;
            o[n] = new String[] { string5 };
        }
        return arr2;
    };
    public static final CriteriaMerger OR = collection -> new String[][] { collection.<String>toArray(new String[0]) };
    
    String[][] createRequirements(final Collection<String> arg1);
    
    default static {
        final String[][] arr2;
        int integer3;
        final Iterator<String> iterator;
        String string5;
        final Object o;
        final int n;
    }
}
