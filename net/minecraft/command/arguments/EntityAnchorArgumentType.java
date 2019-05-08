package net.minecraft.command.arguments;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import java.util.function.BiFunction;
import java.util.Map;
import java.util.Arrays;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import net.minecraft.server.command.CommandSource;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class EntityAnchorArgumentType implements ArgumentType<EntityAnchor>
{
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION;
    
    public static EntityAnchor getEntityAnchor(final CommandContext<ServerCommandSource> commandContext, final String string) {
        return (EntityAnchor)commandContext.getArgument(string, (Class)EntityAnchor.class);
    }
    
    public static EntityAnchorArgumentType create() {
        return new EntityAnchorArgumentType();
    }
    
    public EntityAnchor a(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        final String string3 = stringReader.readUnquotedString();
        final EntityAnchor entityAnchor4 = EntityAnchor.fromId(string3);
        if (entityAnchor4 == null) {
            stringReader.setCursor(integer2);
            throw EntityAnchorArgumentType.INVALID_ANCHOR_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, string3);
        }
        return entityAnchor4;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EntityAnchor.anchors.keySet(), builder);
    }
    
    public Collection<String> getExamples() {
        return EntityAnchorArgumentType.EXAMPLES;
    }
    
    static {
        EXAMPLES = Arrays.<String>asList("eyes", "feet");
        final TranslatableTextComponent translatableTextComponent;
        INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("argument.anchor.invalid", new Object[] { object });
            return translatableTextComponent;
        });
    }
    
    public enum EntityAnchor
    {
        a("feet", (vec3d, entity) -> vec3d), 
        b("eyes", (vec3d, entity) -> new Vec3d(vec3d.x, vec3d.y + entity.getStandingEyeHeight(), vec3d.z));
        
        private static final Map<String, EntityAnchor> anchors;
        private final String id;
        private final BiFunction<Vec3d, Entity, Vec3d> offset;
        
        private EntityAnchor(final String string1, final BiFunction<Vec3d, Entity, Vec3d> biFunction) {
            this.id = string1;
            this.offset = biFunction;
        }
        
        @Nullable
        public static EntityAnchor fromId(final String string) {
            return EntityAnchor.anchors.get(string);
        }
        
        public Vec3d positionAt(final Entity entity) {
            return this.offset.apply(new Vec3d(entity.x, entity.y, entity.z), entity);
        }
        
        public Vec3d positionAt(final ServerCommandSource serverCommandSource) {
            final Entity entity2 = serverCommandSource.getEntity();
            if (entity2 == null) {
                return serverCommandSource.getPosition();
            }
            return this.offset.apply(serverCommandSource.getPosition(), entity2);
        }
        
        static {
            final EntityAnchor[] array;
            int length;
            int i = 0;
            EntityAnchor entityAnchor5;
            anchors = SystemUtil.<Map<String, EntityAnchor>>consume(Maps.newHashMap(), hashMap -> {
                values();
                for (length = array.length; i < length; ++i) {
                    entityAnchor5 = array[i];
                    hashMap.put(entityAnchor5.id, entityAnchor5);
                }
            });
        }
    }
}
