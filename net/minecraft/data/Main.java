package net.minecraft.data;

import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.ItemListProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.server.LootTablesProvider;
import net.minecraft.data.server.AdvancementsProvider;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.EntityTypeTagsProvider;
import net.minecraft.data.server.ItemTagsProvider;
import net.minecraft.data.server.BlockTagsProvider;
import net.minecraft.data.server.FluidTagsProvider;
import java.io.IOException;
import joptsimple.OptionSet;
import java.nio.file.Path;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.nio.file.Paths;
import java.io.OutputStream;
import joptsimple.OptionSpec;
import joptsimple.OptionParser;

public class Main
{
    public static void main(final String[] arr) throws IOException {
        final OptionParser optionParser2 = new OptionParser();
        final OptionSpec<Void> optionSpec3 = (OptionSpec<Void>)optionParser2.accepts("help", "Show the help menu").forHelp();
        final OptionSpec<Void> optionSpec4 = (OptionSpec<Void>)optionParser2.accepts("server", "Include server generators");
        final OptionSpec<Void> optionSpec5 = (OptionSpec<Void>)optionParser2.accepts("client", "Include client generators");
        final OptionSpec<Void> optionSpec6 = (OptionSpec<Void>)optionParser2.accepts("dev", "Include development tools");
        final OptionSpec<Void> optionSpec7 = (OptionSpec<Void>)optionParser2.accepts("reports", "Include data reports");
        final OptionSpec<Void> optionSpec8 = (OptionSpec<Void>)optionParser2.accepts("validate", "Validate inputs");
        final OptionSpec<Void> optionSpec9 = (OptionSpec<Void>)optionParser2.accepts("all", "Include all generators");
        final OptionSpec<String> optionSpec10 = (OptionSpec<String>)optionParser2.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", (Object[])new String[0]);
        final OptionSpec<String> optionSpec11 = (OptionSpec<String>)optionParser2.accepts("input", "Input folder").withRequiredArg();
        final OptionSet optionSet12 = optionParser2.parse(arr);
        if (optionSet12.has((OptionSpec)optionSpec3) || !optionSet12.hasOptions()) {
            optionParser2.printHelpOn((OutputStream)System.out);
            return;
        }
        final Path path13 = Paths.get((String)optionSpec10.value(optionSet12));
        final boolean boolean14 = optionSet12.has((OptionSpec)optionSpec9);
        final boolean boolean15 = boolean14 || optionSet12.has((OptionSpec)optionSpec5);
        final boolean boolean16 = boolean14 || optionSet12.has((OptionSpec)optionSpec4);
        final boolean boolean17 = boolean14 || optionSet12.has((OptionSpec)optionSpec6);
        final boolean boolean18 = boolean14 || optionSet12.has((OptionSpec)optionSpec7);
        final boolean boolean19 = boolean14 || optionSet12.has((OptionSpec)optionSpec8);
        final DataGenerator dataGenerator20 = create(path13, (Collection<Path>)optionSet12.valuesOf((OptionSpec)optionSpec11).stream().map(string -> Paths.get(string)).collect(Collectors.toList()), boolean15, boolean16, boolean17, boolean18, boolean19);
        dataGenerator20.run();
    }
    
    public static DataGenerator create(final Path output, final Collection<Path> inputs, final boolean includeClient, final boolean includeServer, final boolean includeDev, final boolean includeReports, final boolean validate) {
        final DataGenerator dataGenerator8 = new DataGenerator(output, inputs);
        if (includeClient || includeServer) {
            dataGenerator8.install(new SnbtProvider(dataGenerator8));
        }
        if (includeServer) {
            dataGenerator8.install(new FluidTagsProvider(dataGenerator8));
            dataGenerator8.install(new BlockTagsProvider(dataGenerator8));
            dataGenerator8.install(new ItemTagsProvider(dataGenerator8));
            dataGenerator8.install(new EntityTypeTagsProvider(dataGenerator8));
            dataGenerator8.install(new RecipesProvider(dataGenerator8));
            dataGenerator8.install(new AdvancementsProvider(dataGenerator8));
            dataGenerator8.install(new LootTablesProvider(dataGenerator8));
        }
        if (includeDev) {
            dataGenerator8.install(new NbtProvider(dataGenerator8));
        }
        if (includeReports) {
            dataGenerator8.install(new BlockListProvider(dataGenerator8));
            dataGenerator8.install(new ItemListProvider(dataGenerator8));
            dataGenerator8.install(new CommandSyntaxProvider(dataGenerator8));
        }
        if (validate) {
            dataGenerator8.install(new StructureValidatorProvider(dataGenerator8));
        }
        return dataGenerator8;
    }
}
