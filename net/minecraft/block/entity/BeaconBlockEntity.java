package net.minecraft.block.entity;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.common.collect.ImmutableList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.world.BlockView;
import java.util.Arrays;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvents;
import com.google.common.collect.Lists;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.container.ContainerLock;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Tickable;
import net.minecraft.container.NameableContainerProvider;

public class BeaconBlockEntity extends BlockEntity implements NameableContainerProvider, Tickable
{
    public static final StatusEffect[][] EFFECTS_BY_LEVEL;
    private static final Set<StatusEffect> EFFECTS;
    private List<BeamSegment> beamSegments;
    private List<BeamSegment> g;
    private int level;
    private int i;
    @Nullable
    private StatusEffect primary;
    @Nullable
    private StatusEffect secondary;
    @Nullable
    private TextComponent customName;
    private ContainerLock lock;
    private final PropertyDelegate propertyDelegate;
    
    public BeaconBlockEntity() {
        super(BlockEntityType.BEACON);
        this.beamSegments = Lists.newArrayList();
        this.g = Lists.newArrayList();
        this.level = 0;
        this.i = -1;
        this.lock = ContainerLock.NONE;
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(final int key) {
                switch (key) {
                    case 0: {
                        return BeaconBlockEntity.this.level;
                    }
                    case 1: {
                        return StatusEffect.getRawId(BeaconBlockEntity.this.primary);
                    }
                    case 2: {
                        return StatusEffect.getRawId(BeaconBlockEntity.this.secondary);
                    }
                    default: {
                        return 0;
                    }
                }
            }
            
            @Override
            public void set(final int key, final int value) {
                switch (key) {
                    case 0: {
                        BeaconBlockEntity.this.level = value;
                        break;
                    }
                    case 1: {
                        if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
                            BeaconBlockEntity.this.playSound(SoundEvents.X);
                        }
                        BeaconBlockEntity.this.primary = getPotionEffectById(value);
                        break;
                    }
                    case 2: {
                        BeaconBlockEntity.this.secondary = getPotionEffectById(value);
                        break;
                    }
                }
            }
            
            @Override
            public int size() {
                return 3;
            }
        };
    }
    
    @Override
    public void tick() {
        final int integer1 = this.pos.getX();
        final int integer2 = this.pos.getY();
        final int integer3 = this.pos.getZ();
        BlockPos blockPos4;
        if (this.i < integer2) {
            blockPos4 = this.pos;
            this.g = Lists.newArrayList();
            this.i = blockPos4.getY() - 1;
        }
        else {
            blockPos4 = new BlockPos(integer1, this.i + 1, integer3);
        }
        BeamSegment beamSegment5 = this.g.isEmpty() ? null : this.g.get(this.g.size() - 1);
        final int integer4 = this.world.getTop(Heightmap.Type.b, integer1, integer3);
        for (int integer5 = 0; integer5 < 10; ++integer5) {
            if (blockPos4.getY() > integer4) {
                break;
            }
            final BlockState blockState8 = this.world.getBlockState(blockPos4);
            final Block block9 = blockState8.getBlock();
            if (block9 instanceof ColoredBlock) {
                final float[] arr10 = ((ColoredBlock)block9).getColor().getColorComponents();
                if (blockPos4.getY() <= integer2 + 1) {
                    beamSegment5 = new BeamSegment(arr10);
                    this.g.add(beamSegment5);
                }
                else if (Arrays.equals(arr10, beamSegment5.color)) {
                    beamSegment5.increaseHeight();
                }
                else {
                    beamSegment5 = new BeamSegment(new float[] { (beamSegment5.color[0] + arr10[0]) / 2.0f, (beamSegment5.color[1] + arr10[1]) / 2.0f, (beamSegment5.color[2] + arr10[2]) / 2.0f });
                    this.g.add(beamSegment5);
                }
            }
            else {
                if (blockState8.getLightSubtracted(this.world, blockPos4) >= 15 && block9 != Blocks.z) {
                    this.g.clear();
                    this.i = integer4;
                    break;
                }
                beamSegment5.increaseHeight();
            }
            blockPos4 = blockPos4.up();
            ++this.i;
        }
        if (this.i >= integer4) {
            this.i = -1;
            final int integer5 = this.level;
            final boolean boolean8 = this.level > 0 && !this.beamSegments.isEmpty();
            this.beamSegments = this.g;
            if (!this.world.isClient) {
                if (integer5 < this.level) {
                    for (final ServerPlayerEntity serverPlayerEntity10 : this.world.<ServerPlayerEntity>getEntities(ServerPlayerEntity.class, new BoundingBox(integer1, integer2, integer3, integer1, integer2 - 4, integer3).expand(10.0, 5.0, 10.0))) {
                        Criterions.CONSTRUCT_BEACON.handle(serverPlayerEntity10, this);
                    }
                }
                final boolean boolean9 = this.level > 0 && !this.beamSegments.isEmpty();
                if (boolean8 != boolean9) {
                    this.playSound(boolean9 ? SoundEvents.U : SoundEvents.W);
                }
            }
        }
        if (this.world.getTime() % 80L == 0L) {
            if (!this.beamSegments.isEmpty()) {
                this.updateLevel(integer1, integer2, integer3);
            }
            if (this.level > 0 && !this.beamSegments.isEmpty()) {
                this.applyPlayerEffects();
                this.playSound(SoundEvents.V);
            }
        }
    }
    
    private void updateLevel(final int x, final int y, final int z) {
        this.level = 0;
        for (int integer4 = 1; integer4 <= 4; ++integer4) {
            final int integer5 = y - integer4;
            if (integer5 < 0) {
                break;
            }
            boolean boolean6 = true;
            for (int integer6 = x - integer4; integer6 <= x + integer4 && boolean6; ++integer6) {
                for (int integer7 = z - integer4; integer7 <= z + integer4; ++integer7) {
                    final Block block9 = this.world.getBlockState(new BlockPos(integer6, integer5, integer7)).getBlock();
                    if (block9 != Blocks.ef && block9 != Blocks.bD && block9 != Blocks.bS && block9 != Blocks.bE) {
                        boolean6 = false;
                        break;
                    }
                }
            }
            if (!boolean6) {
                break;
            }
            this.level = integer4;
        }
    }
    
    private void applyPlayerEffects() {
        if (this.world.isClient || this.primary == null) {
            return;
        }
        final double double1 = this.level * 10 + 10;
        int integer3 = 0;
        if (this.level >= 4 && this.primary == this.secondary) {
            integer3 = 1;
        }
        final int integer4 = (9 + this.level * 2) * 20;
        final BoundingBox boundingBox5 = new BoundingBox(this.pos).expand(double1).stretch(0.0, this.world.getHeight(), 0.0);
        final List<PlayerEntity> list6 = this.world.<PlayerEntity>getEntities(PlayerEntity.class, boundingBox5);
        for (final PlayerEntity playerEntity8 : list6) {
            playerEntity8.addPotionEffect(new StatusEffectInstance(this.primary, integer4, integer3, true, true));
        }
        if (this.level >= 4 && this.primary != this.secondary && this.secondary != null) {
            for (final PlayerEntity playerEntity8 : list6) {
                playerEntity8.addPotionEffect(new StatusEffectInstance(this.secondary, integer4, 0, true, true));
            }
        }
    }
    
    public void playSound(final SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.e, 1.0f, 1.0f);
    }
    
    @Environment(EnvType.CLIENT)
    public List<BeamSegment> getBeamSegments() {
        return (List<BeamSegment>)((this.level == 0) ? ImmutableList.of() : this.beamSegments);
    }
    
    public int getLevel() {
        return this.level;
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 3, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public double getSquaredRenderDistance() {
        return 65536.0;
    }
    
    @Nullable
    private static StatusEffect getPotionEffectById(final int id) {
        final StatusEffect statusEffect2 = StatusEffect.byRawId(id);
        return BeaconBlockEntity.EFFECTS.contains(statusEffect2) ? statusEffect2 : null;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.primary = getPotionEffectById(compoundTag.getInt("Primary"));
        this.secondary = getPotionEffectById(compoundTag.getInt("Secondary"));
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
        this.lock = ContainerLock.deserialize(compoundTag);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putInt("Primary", StatusEffect.getRawId(this.primary));
        compoundTag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
        if (this.customName != null) {
            compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
        }
        this.lock.serialize(compoundTag);
        return compoundTag;
    }
    
    public void setCustomName(@Nullable final TextComponent textComponent) {
        this.customName = textComponent;
    }
    
    @Nullable
    @Override
    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
        if (LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName())) {
            return new BeaconContainer(syncId, playerInventory, this.propertyDelegate, BlockContext.create(this.world, this.getPos()));
        }
        return null;
    }
    
    @Override
    public TextComponent getDisplayName() {
        return (this.customName != null) ? this.customName : new TranslatableTextComponent("container.beacon", new Object[0]);
    }
    
    static {
        EFFECTS_BY_LEVEL = (StatusEffect[][])new ahx[][] { { StatusEffects.a, StatusEffects.c }, { StatusEffects.k, StatusEffects.h }, { StatusEffects.e }, { StatusEffects.j } };
        EFFECTS = Arrays.<StatusEffect[]>stream(BeaconBlockEntity.EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
    }
    
    public static class BeamSegment
    {
        private final float[] color;
        private int height;
        
        public BeamSegment(final float[] color) {
            this.color = color;
            this.height = 1;
        }
        
        protected void increaseHeight() {
            ++this.height;
        }
        
        @Environment(EnvType.CLIENT)
        public float[] getColor() {
            return this.color;
        }
        
        @Environment(EnvType.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}
