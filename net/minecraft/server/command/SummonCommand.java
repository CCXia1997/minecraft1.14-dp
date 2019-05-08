package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.World;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntitySummonArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SummonCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("summon").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("entity", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntitySummonArgumentType.create()).suggests((SuggestionProvider)SuggestionProviders.SUMMONABLE_ENTITIES).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), ((ServerCommandSource)commandContext.getSource()).getPosition(), new CompoundTag(), true))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), new CompoundTag(), true))).then(CommandManager.argument("nbt", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtCompoundTagArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), NbtCompoundTagArgumentType.getCompoundTag((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "nbt"), false))))));
    }
    
    private static int execute(final ServerCommandSource source, final Identifier entity, final Vec3d pos, final CompoundTag nbt, final boolean initialize) throws CommandSyntaxException {
        final CompoundTag compoundTag6 = nbt.copy();
        compoundTag6.putString("id", entity.toString());
        if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(entity)) {
            final LightningEntity lightningEntity7 = new LightningEntity(source.getWorld(), pos.x, pos.y, pos.z, false);
            source.getWorld().addLightning(lightningEntity7);
            source.sendFeedback(new TranslatableTextComponent("commands.summon.success", new Object[] { lightningEntity7.getDisplayName() }), true);
            return 1;
        }
        final ServerWorld serverWorld7 = source.getWorld();
        final ServerWorld serverWorld8;
        final Entity entity2 = EntityType.loadEntityWithPassengers(compoundTag6, (World)serverWorld7, entity -> {
            entity.setPositionAndAngles(pos.x, pos.y, pos.z, entity.yaw, entity.pitch);
            if (!serverWorld8.d(entity)) {
                return null;
            }
            else {
                return entity;
            }
        });
        if (entity2 == null) {
            throw SummonCommand.FAILED_EXCEPTION.create();
        }
        if (initialize && entity2 instanceof MobEntity) {
            ((MobEntity)entity2).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(new BlockPos(entity2)), SpawnType.n, null, null);
        }
        source.sendFeedback(new TranslatableTextComponent("commands.summon.success", new Object[] { entity2.getDisplayName() }), true);
        return 1;
    }
    
    static {
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.summon.failed", new Object[0]));
    }
}
