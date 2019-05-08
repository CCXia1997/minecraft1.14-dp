package net.minecraft.client.util;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelIdentifier extends Identifier
{
    private final String variant;
    
    protected ModelIdentifier(final String[] arr) {
        super(arr);
        this.variant = arr[2].toLowerCase(Locale.ROOT);
    }
    
    public ModelIdentifier(final String id) {
        this(splitWithVariant(id));
    }
    
    public ModelIdentifier(final Identifier id, final String string) {
        this(id.toString(), string);
    }
    
    public ModelIdentifier(final String namespace, final String name) {
        this(splitWithVariant(namespace + '#' + name));
    }
    
    protected static String[] splitWithVariant(final String str) {
        final String[] arr2 = { null, str, "" };
        final int integer3 = str.indexOf(35);
        String string4 = str;
        if (integer3 >= 0) {
            arr2[2] = str.substring(integer3 + 1, str.length());
            if (integer3 > 1) {
                string4 = str.substring(0, integer3);
            }
        }
        System.arraycopy(Identifier.split(string4, ':'), 0, arr2, 0, 2);
        return arr2;
    }
    
    public String getVariant() {
        return this.variant;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ModelIdentifier && super.equals(o)) {
            final ModelIdentifier modelIdentifier2 = (ModelIdentifier)o;
            return this.variant.equals(modelIdentifier2.variant);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }
    
    @Override
    public String toString() {
        return super.toString() + '#' + this.variant;
    }
}
