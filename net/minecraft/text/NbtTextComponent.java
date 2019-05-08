package net.minecraft.text;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.PosArgument;
import java.util.Objects;
import java.util.List;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.EntitySelector;
import org.apache.logging.log4j.LogManager;
import com.google.common.base.Joiner;
import java.util.function.Function;
import net.minecraft.nbt.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import java.util.stream.Stream;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.NbtPathArgumentType;
import org.apache.logging.log4j.Logger;

public abstract class NbtTextComponent extends AbstractTextComponent implements TextComponentWithSelectors
{
    private static final Logger LOGGER;
    protected final boolean componentJson;
    protected final String path;
    @Nullable
    protected final NbtPathArgumentType.NbtPath parsedPath;
    
    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(final String string) {
        try {
            return new NbtPathArgumentType().a(new StringReader(string));
        }
        catch (CommandSyntaxException commandSyntaxException2) {
            return null;
        }
    }
    
    public NbtTextComponent(final String path, final boolean boolean2) {
        this(path, parsePath(path), boolean2);
    }
    
    protected NbtTextComponent(final String path, @Nullable final NbtPathArgumentType.NbtPath parsedPath, final boolean componentJson) {
        this.path = path;
        this.parsedPath = parsedPath;
        this.componentJson = componentJson;
    }
    
    protected abstract Stream<CompoundTag> resolve(final ServerCommandSource arg1) throws CommandSyntaxException;
    
    @Override
    public String getText() {
        return "";
    }
    
    public String getPath() {
        return this.path;
    }
    
    public boolean isComponentJson() {
        return this.componentJson;
    }
    
    @Override
    public TextComponent resolve(@Nullable final ServerCommandSource source, @Nullable final Entity entity) throws CommandSyntaxException {
        if (source == null || this.parsedPath == null) {
            return new StringTextComponent("");
        }
        final Stream<String> stream3 = this.resolve(source).flatMap(compoundTag -> {
            try {
                return this.parsedPath.get(compoundTag).stream();
            }
            catch (CommandSyntaxException commandSyntaxException2) {
                return Stream.empty();
            }
        }).<String>map(Tag::asString);
        if (this.componentJson) {
            TextComponent textComponent3;
            return stream3.<StringTextComponent>flatMap(string -> {
                try {
                    textComponent3 = TextComponent.Serializer.fromJsonString(string);
                    return Stream.<TextComponent>of(TextFormatter.resolveAndStyle(source, textComponent3, entity));
                }
                catch (Exception exception4) {
                    NbtTextComponent.LOGGER.warn("Failed to parse component: " + string, (Throwable)exception4);
                    return Stream.<TextComponent>of(new TextComponent[0]);
                }
            }).reduce((textComponent1, textComponent2) -> textComponent1.append(", ").append(textComponent2)).orElse(new StringTextComponent(""));
        }
        return new StringTextComponent(Joiner.on(", ").join(stream3.iterator()));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class EntityNbtTextComponent extends NbtTextComponent
    {
        private final String selector;
        @Nullable
        private final EntitySelector parsedSelector;
        
        public EntityNbtTextComponent(final String string1, final boolean boolean2, final String string3) {
            super(string1, boolean2);
            this.selector = string3;
            this.parsedSelector = parseSelector(string3);
        }
        
        @Nullable
        private static EntitySelector parseSelector(final String string) {
            try {
                final EntitySelectorReader entitySelectorReader2 = new EntitySelectorReader(new StringReader(string));
                return entitySelectorReader2.read();
            }
            catch (CommandSyntaxException commandSyntaxException2) {
                return null;
            }
        }
        
        private EntityNbtTextComponent(final String string1, @Nullable final NbtPathArgumentType.NbtPath nbtPath, final boolean boolean3, final String string4, @Nullable final EntitySelector entitySelector) {
            super(string1, nbtPath, boolean3);
            this.selector = string4;
            this.parsedSelector = entitySelector;
        }
        
        public String getSelector() {
            return this.selector;
        }
        
        @Override
        public TextComponent copyShallow() {
            return new EntityNbtTextComponent(this.path, this.parsedPath, this.componentJson, this.selector, this.parsedSelector);
        }
        
        @Override
        protected Stream<CompoundTag> resolve(final ServerCommandSource source) throws CommandSyntaxException {
            if (this.parsedSelector != null) {
                final List<? extends Entity> list2 = this.parsedSelector.getEntities(source);
                return list2.stream().<CompoundTag>map(NbtPredicate::entityToTag);
            }
            return Stream.<CompoundTag>empty();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof EntityNbtTextComponent) {
                final EntityNbtTextComponent entityNbtTextComponent2 = (EntityNbtTextComponent)o;
                return Objects.equals(this.selector, entityNbtTextComponent2.selector) && Objects.equals(this.path, entityNbtTextComponent2.path) && super.equals(o);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.selector + '\'' + "path='" + this.path + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
    
    public static class BlockPosArgument extends NbtTextComponent
    {
        private final String pos;
        @Nullable
        private final PosArgument parsedPos;
        
        public BlockPosArgument(final String string1, final boolean boolean2, final String string3) {
            super(string1, boolean2);
            this.pos = string3;
            this.parsedPos = this.parsePos(this.pos);
        }
        
        @Nullable
        private PosArgument parsePos(final String pos) {
            try {
                return BlockPosArgumentType.create().a(new StringReader(pos));
            }
            catch (CommandSyntaxException commandSyntaxException2) {
                return null;
            }
        }
        
        private BlockPosArgument(final String string1, @Nullable final NbtPathArgumentType.NbtPath nbtPath, final boolean boolean3, final String string4, @Nullable final PosArgument posArgument) {
            super(string1, nbtPath, boolean3);
            this.pos = string4;
            this.parsedPos = posArgument;
        }
        
        @Nullable
        public String getPos() {
            return this.pos;
        }
        
        @Override
        public TextComponent copyShallow() {
            return new BlockPosArgument(this.path, this.parsedPath, this.componentJson, this.pos, this.parsedPos);
        }
        
        @Override
        protected Stream<CompoundTag> resolve(final ServerCommandSource source) {
            if (this.parsedPos != null) {
                final ServerWorld serverWorld2 = source.getWorld();
                final BlockPos blockPos3 = this.parsedPos.toAbsoluteBlockPos(source);
                if (serverWorld2.isHeightValidAndBlockLoaded(blockPos3)) {
                    final BlockEntity blockEntity4 = serverWorld2.getBlockEntity(blockPos3);
                    if (blockEntity4 != null) {
                        return Stream.<CompoundTag>of(blockEntity4.toTag(new CompoundTag()));
                    }
                }
            }
            return Stream.<CompoundTag>empty();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof BlockPosArgument) {
                final BlockPosArgument blockPosArgument2 = (BlockPosArgument)o;
                return Objects.equals(this.pos, blockPosArgument2.pos) && Objects.equals(this.path, blockPosArgument2.path) && super.equals(o);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.pos + '\'' + "path='" + this.path + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
}
