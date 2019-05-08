package net.minecraft.server.function;

import org.apache.logging.log4j.LogManager;
import net.minecraft.resource.Resource;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.tag.Tag;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import net.minecraft.tag.TagContainer;
import java.util.ArrayDeque;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.minecraft.resource.SynchronousResourceReloadListener;

public class CommandFunctionManager implements SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    private static final Identifier TICK_FUNCTION;
    private static final Identifier LOAD_FUNCTION;
    public static final int PATH_PREFIX_LENGTH;
    public static final int EXTENSION_LENGTH;
    private final MinecraftServer server;
    private final Map<Identifier, CommandFunction> idMap;
    private final ArrayDeque<Entry> chain;
    private boolean i;
    private final TagContainer<CommandFunction> tags;
    private final List<CommandFunction> tickFunctions;
    private boolean needToRunLoadFunctions;
    
    public CommandFunctionManager(final MinecraftServer minecraftServer) {
        this.idMap = Maps.newHashMap();
        this.chain = new ArrayDeque<Entry>();
        this.tags = new TagContainer<CommandFunction>(this::getFunction, "tags/functions", true, "function");
        this.tickFunctions = Lists.newArrayList();
        this.server = minecraftServer;
    }
    
    public Optional<CommandFunction> getFunction(final Identifier identifier) {
        return Optional.<CommandFunction>ofNullable(this.idMap.get(identifier));
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public int getMaxCommandChainLength() {
        return this.server.getGameRules().getInteger("maxCommandChainLength");
    }
    
    public Map<Identifier, CommandFunction> getFunctions() {
        return this.idMap;
    }
    
    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.server.getCommandManager().getDispatcher();
    }
    
    public void tick() {
        this.server.getProfiler().push(CommandFunctionManager.TICK_FUNCTION::toString);
        for (final CommandFunction commandFunction2 : this.tickFunctions) {
            this.execute(commandFunction2, this.getFunctionCommandSource());
        }
        this.server.getProfiler().pop();
        if (this.needToRunLoadFunctions) {
            this.needToRunLoadFunctions = false;
            final Collection<CommandFunction> collection1 = this.getTags().getOrCreate(CommandFunctionManager.LOAD_FUNCTION).values();
            this.server.getProfiler().push(CommandFunctionManager.LOAD_FUNCTION::toString);
            for (final CommandFunction commandFunction3 : collection1) {
                this.execute(commandFunction3, this.getFunctionCommandSource());
            }
            this.server.getProfiler().pop();
        }
    }
    
    public int execute(final CommandFunction commandFunction, final ServerCommandSource serverCommandSource) {
        final int integer3 = this.getMaxCommandChainLength();
        if (this.i) {
            if (this.chain.size() < integer3) {
                this.chain.addFirst(new Entry(this, serverCommandSource, new CommandFunction.FunctionElement(commandFunction)));
            }
            return 0;
        }
        try {
            this.i = true;
            int integer4 = 0;
            final CommandFunction.Element[] arr5 = commandFunction.getElements();
            for (int integer5 = arr5.length - 1; integer5 >= 0; --integer5) {
                this.chain.push(new Entry(this, serverCommandSource, arr5[integer5]));
            }
            while (!this.chain.isEmpty()) {
                try {
                    final Entry entry6 = this.chain.removeFirst();
                    this.server.getProfiler().push(entry6::toString);
                    entry6.execute(this.chain, integer3);
                }
                finally {
                    this.server.getProfiler().pop();
                }
                if (++integer4 >= integer3) {
                    return integer4;
                }
            }
            return integer4;
        }
        finally {
            this.chain.clear();
            this.i = false;
        }
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.idMap.clear();
        this.tickFunctions.clear();
        this.tags.clear();
        final Collection<Identifier> collection2 = manager.findResources("functions", string -> string.endsWith(".mcfunction"));
        final List<CompletableFuture<CommandFunction>> list2 = Lists.newArrayList();
        for (final Identifier identifier5 : collection2) {
            final String string2 = identifier5.getPath();
            final Identifier identifier6 = new Identifier(identifier5.getNamespace(), string2.substring(CommandFunctionManager.PATH_PREFIX_LENGTH, string2.length() - CommandFunctionManager.EXTENSION_LENGTH));
            list2.add(CompletableFuture.<List<String>>supplyAsync(() -> readLines(manager, identifier5), ResourceImpl.RESOURCE_IO_EXECUTOR).thenApplyAsync(list -> CommandFunction.create(identifier6, this, list), this.server.getWorkerExecutor()).<CommandFunction>handle((commandFunction, throwable) -> this.load(commandFunction, throwable, identifier5)));
        }
        CompletableFuture.allOf(list2.<CompletableFuture<?>>toArray(new CompletableFuture[0])).join();
        if (!this.idMap.isEmpty()) {
            CommandFunctionManager.LOGGER.info("Loaded {} custom command functions", this.idMap.size());
        }
        this.tags.applyReload(this.tags.prepareReload(manager, this.server.getWorkerExecutor()).join());
        this.tickFunctions.addAll(this.tags.getOrCreate(CommandFunctionManager.TICK_FUNCTION).values());
        this.needToRunLoadFunctions = true;
    }
    
    @Nullable
    private CommandFunction load(final CommandFunction function, @Nullable final Throwable exception, final Identifier identifier) {
        if (exception != null) {
            CommandFunctionManager.LOGGER.error("Couldn't load function at {}", identifier, exception);
            return null;
        }
        synchronized (this.idMap) {
            this.idMap.put(function.getId(), function);
        }
        return function;
    }
    
    private static List<String> readLines(final ResourceManager resourceManager, final Identifier identifier) {
        try (final Resource resource3 = resourceManager.getResource(identifier)) {
            return (List<String>)IOUtils.readLines(resource3.getInputStream(), StandardCharsets.UTF_8);
        }
        catch (IOException iOException3) {
            throw new CompletionException(iOException3);
        }
    }
    
    public ServerCommandSource getFunctionCommandSource() {
        return this.server.getCommandSource().withLevel(2).withSilent();
    }
    
    public TagContainer<CommandFunction> getTags() {
        return this.tags;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        TICK_FUNCTION = new Identifier("tick");
        LOAD_FUNCTION = new Identifier("load");
        PATH_PREFIX_LENGTH = "functions/".length();
        EXTENSION_LENGTH = ".mcfunction".length();
    }
    
    public static class Entry
    {
        private final CommandFunctionManager manager;
        private final ServerCommandSource source;
        private final CommandFunction.Element element;
        
        public Entry(final CommandFunctionManager commandFunctionManager, final ServerCommandSource serverCommandSource, final CommandFunction.Element element) {
            this.manager = commandFunctionManager;
            this.source = serverCommandSource;
            this.element = element;
        }
        
        public void execute(final ArrayDeque<Entry> arrayDeque, final int integer) {
            try {
                this.element.execute(this.manager, this.source, arrayDeque, integer);
            }
            catch (Throwable t) {}
        }
        
        @Override
        public String toString() {
            return this.element.toString();
        }
    }
}
