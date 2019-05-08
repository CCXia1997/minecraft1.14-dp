package net.minecraft.text;

import org.apache.logging.log4j.LogManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelectorReader;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import org.apache.logging.log4j.Logger;

public class SelectorTextComponent extends AbstractTextComponent implements TextComponentWithSelectors
{
    private static final Logger LOGGER;
    private final String pattern;
    @Nullable
    private final EntitySelector d;
    
    public SelectorTextComponent(final String string) {
        this.pattern = string;
        EntitySelector entitySelector2 = null;
        try {
            final EntitySelectorReader entitySelectorReader3 = new EntitySelectorReader(new StringReader(string));
            entitySelector2 = entitySelectorReader3.read();
        }
        catch (CommandSyntaxException commandSyntaxException3) {
            SelectorTextComponent.LOGGER.warn("Invalid selector component: {}", string, commandSyntaxException3.getMessage());
        }
        this.d = entitySelector2;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    @Override
    public TextComponent resolve(@Nullable final ServerCommandSource source, @Nullable final Entity entity) throws CommandSyntaxException {
        if (source == null || this.d == null) {
            return new StringTextComponent("");
        }
        return EntitySelector.getNames(this.d.getEntities(source));
    }
    
    @Override
    public String getText() {
        return this.pattern;
    }
    
    @Override
    public SelectorTextComponent copyShallow() {
        return new SelectorTextComponent(this.pattern);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SelectorTextComponent) {
            final SelectorTextComponent selectorTextComponent2 = (SelectorTextComponent)o;
            return this.pattern.equals(selectorTextComponent2.pattern) && super.equals(o);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
