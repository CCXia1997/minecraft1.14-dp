package net.minecraft.command.arguments;

import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.Recipe;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.arguments.ArgumentType;

public class IdentifierArgumentType implements ArgumentType<Identifier>
{
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType UNKNOWN_EXCEPTION;
    public static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION;
    public static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION;
    
    public static IdentifierArgumentType create() {
        return new IdentifierArgumentType();
    }
    
    public static Advancement getAdvancementArgument(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        final Identifier identifier3 = (Identifier)commandContext.getArgument(string, (Class)Identifier.class);
        final Advancement advancement4 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().get(identifier3);
        if (advancement4 == null) {
            throw IdentifierArgumentType.UNKNOWN_ADVANCEMENT_EXCEPTION.create(identifier3);
        }
        return advancement4;
    }
    
    public static Recipe<?> getRecipeArgument(final CommandContext<ServerCommandSource> commandContext, final String string) throws CommandSyntaxException {
        final RecipeManager recipeManager3 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager();
        final Identifier identifier4 = (Identifier)commandContext.getArgument(string, (Class)Identifier.class);
        return recipeManager3.get(identifier4).<Throwable>orElseThrow(() -> IdentifierArgumentType.UNKNOWN_RECIPE_EXCEPTION.create(identifier4));
    }
    
    public static Identifier getIdentifier(final CommandContext<ServerCommandSource> context, final String name) {
        return (Identifier)context.getArgument(name, (Class)Identifier.class);
    }
    
    public Identifier a(final StringReader stringReader) throws CommandSyntaxException {
        return Identifier.parse(stringReader);
    }
    
    public Collection<String> getExamples() {
        return IdentifierArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("foo", "foo:bar", "012");
        final TranslatableTextComponent translatableTextComponent;
        UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.id.unknown", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("advancement.advancementNotFound", new Object[] { object });
            return translatableTextComponent2;
        });
        final TranslatableTextComponent translatableTextComponent3;
        UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("recipe.notFound", new Object[] { object });
            return translatableTextComponent3;
        });
    }
}
