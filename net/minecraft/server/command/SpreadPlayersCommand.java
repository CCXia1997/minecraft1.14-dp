package net.minecraft.server.command;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Maps;
import net.minecraft.world.BlockView;
import net.minecraft.server.world.ServerWorld;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.entity.player.PlayerEntity;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Locale;
import java.util.Random;
import net.minecraft.entity.Entity;
import java.util.Collection;
import net.minecraft.util.math.Vec2f;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.Vec2ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;

public class SpreadPlayersCommand
{
    private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION;
    private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spreadplayers").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("center", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec2ArgumentType.create()).then(CommandManager.argument("spreadDistance", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).then(CommandManager.argument("maxRange", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(1.0f)).then(CommandManager.argument("respectTeams", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2((CommandContext<ServerCommandSource>)commandContext, "center"), FloatArgumentType.getFloat(commandContext, "spreadDistance"), FloatArgumentType.getFloat(commandContext, "maxRange"), BoolArgumentType.getBool(commandContext, "respectTeams"), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))))))));
    }
    
    private static int execute(final ServerCommandSource source, final Vec2f center, final float spreadDistance, final float maxRange, final boolean respectTeams, final Collection<? extends Entity> targets) throws CommandSyntaxException {
        final Random random7 = new Random();
        final double double8 = center.x - maxRange;
        final double double9 = center.y - maxRange;
        final double double10 = center.x + maxRange;
        final double double11 = center.y + maxRange;
        final Pile[] arr16 = makePiles(random7, respectTeams ? getPileCountRespectingTeams(targets) : targets.size(), double8, double9, double10, double11);
        spread(center, spreadDistance, source.getWorld(), random7, double8, double9, double10, double11, arr16, respectTeams);
        final double double12 = getMinimumDistance(targets, source.getWorld(), arr16, respectTeams);
        source.sendFeedback(new TranslatableTextComponent("commands.spreadplayers.success." + (respectTeams ? "teams" : "entities"), new Object[] { arr16.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", double12) }), true);
        return arr16.length;
    }
    
    private static int getPileCountRespectingTeams(final Collection<? extends Entity> entities) {
        final Set<AbstractTeam> set2 = Sets.newHashSet();
        for (final Entity entity4 : entities) {
            if (entity4 instanceof PlayerEntity) {
                set2.add(entity4.getScoreboardTeam());
            }
            else {
                set2.add(null);
            }
        }
        return set2.size();
    }
    
    private static void spread(final Vec2f center, final double spreadDistance, final ServerWorld world, final Random random, final double minX, final double minZ, final double maxX, final double maxZ, final Pile[] piles, final boolean respectTeams) throws CommandSyntaxException {
        boolean boolean16 = true;
        double double18 = 3.4028234663852886E38;
        int integer17;
        for (integer17 = 0; integer17 < 10000 && boolean16; ++integer17) {
            boolean16 = false;
            double18 = 3.4028234663852886E38;
            for (int integer18 = 0; integer18 < piles.length; ++integer18) {
                final Pile pile21 = piles[integer18];
                int integer19 = 0;
                final Pile pile22 = new Pile();
                for (int integer20 = 0; integer20 < piles.length; ++integer20) {
                    if (integer18 != integer20) {
                        final Pile pile23 = piles[integer20];
                        final double double19 = pile21.getDistance(pile23);
                        double18 = Math.min(double19, double18);
                        if (double19 < spreadDistance) {
                            ++integer19;
                            pile22.x += pile23.x - pile21.x;
                            pile22.z += pile23.z - pile21.z;
                        }
                    }
                }
                if (integer19 > 0) {
                    pile22.x /= integer19;
                    pile22.z /= integer19;
                    final double double20 = pile22.absolute();
                    if (double20 > 0.0) {
                        pile22.normalize();
                        pile21.subtract(pile22);
                    }
                    else {
                        pile21.setPileLocation(random, minX, minZ, maxX, maxZ);
                    }
                    boolean16 = true;
                }
                if (pile21.clamp(minX, minZ, maxX, maxZ)) {
                    boolean16 = true;
                }
            }
            if (!boolean16) {
                for (final Pile pile22 : piles) {
                    if (!pile22.isSafe(world)) {
                        pile22.setPileLocation(random, minX, minZ, maxX, maxZ);
                        boolean16 = true;
                    }
                }
            }
        }
        if (double18 == 3.4028234663852886E38) {
            double18 = 0.0;
        }
        if (integer17 < 10000) {
            return;
        }
        if (respectTeams) {
            throw SpreadPlayersCommand.FAILED_TEAMS_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", double18));
        }
        throw SpreadPlayersCommand.FAILED_ENTITIES_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", double18));
    }
    
    private static double getMinimumDistance(final Collection<? extends Entity> entities, final ServerWorld world, final Pile[] piles, final boolean betweenTeams) {
        double double5 = 0.0;
        int integer7 = 0;
        final Map<AbstractTeam, Pile> map8 = Maps.newHashMap();
        for (final Entity entity10 : entities) {
            Pile pile11;
            if (betweenTeams) {
                final AbstractTeam abstractTeam12 = (entity10 instanceof PlayerEntity) ? entity10.getScoreboardTeam() : null;
                if (!map8.containsKey(abstractTeam12)) {
                    map8.put(abstractTeam12, piles[integer7++]);
                }
                pile11 = map8.get(abstractTeam12);
            }
            else {
                pile11 = piles[integer7++];
            }
            entity10.requestTeleport(MathHelper.floor(pile11.x) + 0.5f, pile11.getY(world), MathHelper.floor(pile11.z) + 0.5);
            double double6 = Double.MAX_VALUE;
            for (final Pile pile12 : piles) {
                if (pile11 != pile12) {
                    final double double7 = pile11.getDistance(pile12);
                    double6 = Math.min(double7, double6);
                }
            }
            double5 += double6;
        }
        if (entities.size() < 2) {
            return 0.0;
        }
        double5 /= entities.size();
        return double5;
    }
    
    private static Pile[] makePiles(final Random random, final int count, final double minX, final double minZ, final double maxX, final double maxZ) {
        final Pile[] arr11 = new Pile[count];
        for (int integer12 = 0; integer12 < arr11.length; ++integer12) {
            final Pile pile13 = new Pile();
            pile13.setPileLocation(random, minX, minZ, maxX, maxZ);
            arr11[integer12] = pile13;
        }
        return arr11;
    }
    
    static {
        FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType((object1, object2, object3, object4) -> new TranslatableTextComponent("commands.spreadplayers.failed.teams", new Object[] { object1, object2, object3, object4 }));
        FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType((object1, object2, object3, object4) -> new TranslatableTextComponent("commands.spreadplayers.failed.entities", new Object[] { object1, object2, object3, object4 }));
    }
    
    static class Pile
    {
        private double x;
        private double z;
        
        double getDistance(final Pile other) {
            final double double2 = this.x - other.x;
            final double double3 = this.z - other.z;
            return Math.sqrt(double2 * double2 + double3 * double3);
        }
        
        void normalize() {
            final double double1 = this.absolute();
            this.x /= double1;
            this.z /= double1;
        }
        
        float absolute() {
            return MathHelper.sqrt(this.x * this.x + this.z * this.z);
        }
        
        public void subtract(final Pile other) {
            this.x -= other.x;
            this.z -= other.z;
        }
        
        public boolean clamp(final double minX, final double minZ, final double maxX, final double maxZ) {
            boolean boolean9 = false;
            if (this.x < minX) {
                this.x = minX;
                boolean9 = true;
            }
            else if (this.x > maxX) {
                this.x = maxX;
                boolean9 = true;
            }
            if (this.z < minZ) {
                this.z = minZ;
                boolean9 = true;
            }
            else if (this.z > maxZ) {
                this.z = maxZ;
                boolean9 = true;
            }
            return boolean9;
        }
        
        public int getY(final BlockView blockView) {
            BlockPos blockPos2 = new BlockPos(this.x, 256.0, this.z);
            while (blockPos2.getY() > 0) {
                blockPos2 = blockPos2.down();
                if (!blockView.getBlockState(blockPos2).isAir()) {
                    return blockPos2.getY() + 1;
                }
            }
            return 257;
        }
        
        public boolean isSafe(final BlockView world) {
            BlockPos blockPos2 = new BlockPos(this.x, 256.0, this.z);
            while (blockPos2.getY() > 0) {
                blockPos2 = blockPos2.down();
                final BlockState blockState3 = world.getBlockState(blockPos2);
                if (!blockState3.isAir()) {
                    final Material material4 = blockState3.getMaterial();
                    return !material4.isLiquid() && material4 != Material.FIRE;
                }
            }
            return false;
        }
        
        public void setPileLocation(final Random random, final double minX, final double minZ, final double maxX, final double maxZ) {
            this.x = MathHelper.nextDouble(random, minX, maxX);
            this.z = MathHelper.nextDouble(random, minZ, maxZ);
        }
    }
}
