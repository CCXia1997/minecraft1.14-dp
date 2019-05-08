package net.minecraft.command.arguments;

import java.util.Arrays;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class BlockStateArgumentType implements ArgumentType<BlockStateArgument>
{
    private static final Collection<String> EXAMPLES;
    
    public static BlockStateArgumentType create() {
        return new BlockStateArgumentType();
    }
    
    public BlockStateArgument a(final StringReader stringReader) throws CommandSyntaxException {
        final BlockArgumentParser blockArgumentParser2 = new BlockArgumentParser(stringReader, false).parse(true);
        return new BlockStateArgument(blockArgumentParser2.getBlockState(), blockArgumentParser2.getBlockProperties().keySet(), blockArgumentParser2.getNbtData());
    }
    
    public static BlockStateArgument getBlockState(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (BlockStateArgument)commandContext.getArgument(string, (Class)BlockStateArgument.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final StringReader stringReader3 = new StringReader(builder.getInput());
        stringReader3.setCursor(builder.getStart());
        final BlockArgumentParser blockArgumentParser4 = new BlockArgumentParser(stringReader3, false);
        try {
            blockArgumentParser4.parse(true);
        }
        catch (CommandSyntaxException ex) {}
        return blockArgumentParser4.getSuggestions(builder);
    }
    
    public Collection<String> getExamples() {
        return BlockStateArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
    }
}
