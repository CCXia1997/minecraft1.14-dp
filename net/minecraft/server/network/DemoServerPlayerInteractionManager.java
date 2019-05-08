package net.minecraft.server.network;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.server.world.ServerWorld;

public class DemoServerPlayerInteractionManager extends ServerPlayerInteractionManager
{
    private boolean c;
    private boolean demoEnded;
    private int e;
    private int f;
    
    public DemoServerPlayerInteractionManager(final ServerWorld serverWorld) {
        super(serverWorld);
    }
    
    @Override
    public void update() {
        super.update();
        ++this.f;
        final long long1 = this.world.getTime();
        final long long2 = long1 / 24000L + 1L;
        if (!this.c && this.f > 20) {
            this.c = true;
            this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 0.0f));
        }
        this.demoEnded = (long1 > 120500L);
        if (this.demoEnded) {
            ++this.e;
        }
        if (long1 % 24000L == 500L) {
            if (long2 <= 6L) {
                if (long2 == 6L) {
                    this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 104.0f));
                }
                else {
                    this.player.sendMessage(new TranslatableTextComponent("demo.day." + long2, new Object[0]));
                }
            }
        }
        else if (long2 == 1L) {
            if (long1 == 100L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 101.0f));
            }
            else if (long1 == 175L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 102.0f));
            }
            else if (long1 == 250L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 103.0f));
            }
        }
        else if (long2 == 5L && long1 % 24000L == 22000L) {
            this.player.sendMessage(new TranslatableTextComponent("demo.day.warning", new Object[0]));
        }
    }
    
    private void sendDemoReminder() {
        if (this.e > 100) {
            this.player.sendMessage(new TranslatableTextComponent("demo.reminder", new Object[0]));
            this.e = 0;
        }
    }
    
    @Override
    public void a(final BlockPos blockPos, final Direction direction) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return;
        }
        super.a(blockPos, direction);
    }
    
    @Override
    public void a(final BlockPos blockPos) {
        if (this.demoEnded) {
            return;
        }
        super.a(blockPos);
    }
    
    @Override
    public boolean tryBreakBlock(final BlockPos blockPos) {
        return !this.demoEnded && super.tryBreakBlock(blockPos);
    }
    
    @Override
    public ActionResult interactItem(final PlayerEntity playerEntity, final World world, final ItemStack itemStack, final Hand hand) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return ActionResult.PASS;
        }
        return super.interactItem(playerEntity, world, itemStack, hand);
    }
    
    @Override
    public ActionResult interactBlock(final PlayerEntity player, final World world, final ItemStack stack, final Hand hand, final BlockHitResult blockHitResult) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return ActionResult.PASS;
        }
        return super.interactBlock(player, world, stack, hand, blockHitResult);
    }
}
