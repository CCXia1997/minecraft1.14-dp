package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.Style;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.Vec3i;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class LocateCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locate").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("Pillager_Outpost").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Pillager_Outpost")))).then(CommandManager.literal("Mineshaft").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Mineshaft")))).then(CommandManager.literal("Mansion").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Mansion")))).then(CommandManager.literal("Igloo").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Igloo")))).then(CommandManager.literal("Desert_Pyramid").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Desert_Pyramid")))).then(CommandManager.literal("Jungle_Pyramid").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Jungle_Pyramid")))).then(CommandManager.literal("Swamp_Hut").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Swamp_Hut")))).then(CommandManager.literal("Stronghold").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Stronghold")))).then(CommandManager.literal("Monument").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Monument")))).then(CommandManager.literal("Fortress").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Fortress")))).then(CommandManager.literal("EndCity").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "EndCity")))).then(CommandManager.literal("Ocean_Ruin").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Ocean_Ruin")))).then(CommandManager.literal("Buried_Treasure").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Buried_Treasure")))).then(CommandManager.literal("Shipwreck").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Shipwreck")))).then(CommandManager.literal("Village").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), "Village"))));
    }
    
    private static int execute(final ServerCommandSource source, final String structure) throws CommandSyntaxException {
        final BlockPos blockPos3 = new BlockPos(source.getPosition());
        final BlockPos blockPos4 = source.getWorld().locateStructure(structure, blockPos3, 100, false);
        if (blockPos4 == null) {
            throw LocateCommand.FAILED_EXCEPTION.create();
        }
        final int integer5 = MathHelper.floor(getDistance(blockPos3.getX(), blockPos3.getZ(), blockPos4.getX(), blockPos4.getZ()));
        final ClickEvent clickEvent;
        final Vec3i vec3i;
        final Object o;
        final HoverEvent hoverEvent;
        final Object o2;
        final TextComponent textComponent6 = TextFormatter.bracketed(new TranslatableTextComponent("chat.coordinates", new Object[] { blockPos4.getX(), "~", blockPos4.getZ() })).modifyStyle(style -> {
            style.setColor(TextFormat.k);
            new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + vec3i.getX() + " ~ " + vec3i.getZ());
            ((Style)o).setClickEvent(clickEvent);
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableTextComponent("chat.coordinates.tooltip", new Object[0]));
            ((Style)o2).setHoverEvent(hoverEvent);
            return;
        });
        source.sendFeedback(new TranslatableTextComponent("commands.locate.success", new Object[] { structure, textComponent6, integer5 }), false);
        return integer5;
    }
    
    private static float getDistance(final int x1, final int y1, final int x2, final int y2) {
        final int integer5 = x2 - x1;
        final int integer6 = y2 - y1;
        return MathHelper.sqrt((float)(integer5 * integer5 + integer6 * integer6));
    }
    
    static {
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.locate.failed", new Object[0]));
    }
}
