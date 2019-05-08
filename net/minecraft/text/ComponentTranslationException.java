package net.minecraft.text;

public class ComponentTranslationException extends IllegalArgumentException
{
    public ComponentTranslationException(final TranslatableTextComponent component, final String string) {
        super(String.format("Error parsing: %s: %s", component, string));
    }
    
    public ComponentTranslationException(final TranslatableTextComponent component, final int integer) {
        super(String.format("Invalid index %d requested for %s", integer, component));
    }
    
    public ComponentTranslationException(final TranslatableTextComponent component, final Throwable throwable) {
        super(String.format("Error while parsing: %s", component), throwable);
    }
}
