package net.minecraft.block.dispenser;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.DyeColor;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.item.ArmorItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.TntBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.block.Blocks;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.block.FluidDrainable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.item.BucketItem;
import net.minecraft.entity.vehicle.BoatEntity;
import java.util.Random;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public interface DispenserBehavior
{
    public static final DispenserBehavior NOOP = (blockPointer, itemStack) -> itemStack;
    
    ItemStack dispense(final BlockPointer arg1, final ItemStack arg2);
    
    default void registerDefaults() {
        DispenserBlock.registerBehavior(Items.jg, new ProjectileDispenserBehavior() {
            @Override
            protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                final ArrowEntity arrowEntity4 = new ArrowEntity(position, stack.getX(), stack.getY(), stack.getZ());
                arrowEntity4.pickupType = ProjectileEntity.PickupType.PICKUP;
                return arrowEntity4;
            }
        });
        DispenserBlock.registerBehavior(Items.oU, new ProjectileDispenserBehavior() {
            @Override
            protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                final ArrowEntity arrowEntity4 = new ArrowEntity(position, stack.getX(), stack.getY(), stack.getZ());
                arrowEntity4.initFromStack(itemStack);
                arrowEntity4.pickupType = ProjectileEntity.PickupType.PICKUP;
                return arrowEntity4;
            }
        });
        DispenserBlock.registerBehavior(Items.oT, new ProjectileDispenserBehavior() {
            @Override
            protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                final ProjectileEntity projectileEntity4 = new SpectralArrowEntity(position, stack.getX(), stack.getY(), stack.getZ());
                projectileEntity4.pickupType = ProjectileEntity.PickupType.PICKUP;
                return projectileEntity4;
            }
        });
        DispenserBlock.registerBehavior(Items.kW, new ProjectileDispenserBehavior() {
            @Override
            protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                return SystemUtil.<ThrownEggEntity>consume(new ThrownEggEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownEggEntity -> thrownEggEntity.setItem(itemStack));
            }
        });
        DispenserBlock.registerBehavior(Items.kD, new ProjectileDispenserBehavior() {
            @Override
            protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                return SystemUtil.<SnowballEntity>consume(new SnowballEntity(position, stack.getX(), stack.getY(), stack.getZ()), snowballEntity -> snowballEntity.setItem(itemStack));
            }
        });
        DispenserBlock.registerBehavior(Items.nB, new ProjectileDispenserBehavior() {
            @Override
            protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                return SystemUtil.<ThrownExperienceBottleEntity>consume(new ThrownExperienceBottleEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownExperienceBottleEntity -> thrownExperienceBottleEntity.setItem(itemStack));
            }
            
            @Override
            protected float getVariation() {
                return super.getVariation() * 0.5f;
            }
            
            @Override
            protected float getForce() {
                return super.getForce() * 1.25f;
            }
        });
        DispenserBlock.registerBehavior(Items.oS, new DispenserBehavior() {
            @Override
            public ItemStack dispense(final BlockPointer location, final ItemStack stack) {
                return new ProjectileDispenserBehavior() {
                    @Override
                    protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                        return SystemUtil.<ThrownPotionEntity>consume(new ThrownPotionEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownPotionEntity -> thrownPotionEntity.setItemStack(itemStack));
                    }
                    
                    @Override
                    protected float getVariation() {
                        return super.getVariation() * 0.5f;
                    }
                    
                    @Override
                    protected float getForce() {
                        return super.getForce() * 1.25f;
                    }
                }.dispense(location, stack);
            }
        });
        DispenserBlock.registerBehavior(Items.oV, new DispenserBehavior() {
            @Override
            public ItemStack dispense(final BlockPointer location, final ItemStack stack) {
                return new ProjectileDispenserBehavior() {
                    @Override
                    protected Projectile createProjectile(final World position, final Position stack, final ItemStack itemStack) {
                        return SystemUtil.<ThrownPotionEntity>consume(new ThrownPotionEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownPotionEntity -> thrownPotionEntity.setItemStack(itemStack));
                    }
                    
                    @Override
                    protected float getVariation() {
                        return super.getVariation() * 0.5f;
                    }
                    
                    @Override
                    protected float getForce() {
                        return super.getForce() * 1.25f;
                    }
                }.dispense(location, stack);
            }
        });
        final ItemDispenserBehavior itemDispenserBehavior1 = new ItemDispenserBehavior() {
            public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final Direction direction3 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
                final EntityType<?> entityType4 = ((SpawnEggItem)stack.getItem()).entityTypeFromTag(stack.getTag());
                entityType4.spawnFromItemStack(pointer.getWorld(), stack, null, pointer.getBlockPos().offset(direction3), SpawnType.o, direction3 != Direction.UP, false);
                stack.subtractAmount(1);
                return stack;
            }
        };
        for (final SpawnEggItem spawnEggItem3 : SpawnEggItem.iterator()) {
            DispenserBlock.registerBehavior(spawnEggItem3, itemDispenserBehavior1);
        }
        DispenserBlock.registerBehavior(Items.nX, new ItemDispenserBehavior() {
            public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final Direction direction3 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
                final double double4 = pointer.getX() + direction3.getOffsetX();
                final double double5 = pointer.getBlockPos().getY() + 0.2f;
                final double double6 = pointer.getZ() + direction3.getOffsetZ();
                pointer.getWorld().spawnEntity(new FireworkEntity(pointer.getWorld(), double4, double5, double6, stack));
                stack.subtractAmount(1);
                return stack;
            }
            
            @Override
            protected void playSound(final BlockPointer blockPointer) {
                blockPointer.getWorld().playLevelEvent(1004, blockPointer.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.nC, new ItemDispenserBehavior() {
            public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final Direction direction3 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
                final Position position4 = DispenserBlock.getOutputLocation(pointer);
                final double double5 = position4.getX() + direction3.getOffsetX() * 0.3f;
                final double double6 = position4.getY() + direction3.getOffsetY() * 0.3f;
                final double double7 = position4.getZ() + direction3.getOffsetZ() * 0.3f;
                final World world11 = pointer.getWorld();
                final Random random12 = world11.random;
                final double double8 = random12.nextGaussian() * 0.05 + direction3.getOffsetX();
                final double double9 = random12.nextGaussian() * 0.05 + direction3.getOffsetY();
                final double double10 = random12.nextGaussian() * 0.05 + direction3.getOffsetZ();
                world11.spawnEntity(SystemUtil.<SmallFireballEntity>consume(new SmallFireballEntity(world11, double5, double6, double7, double8, double9, double10), smallFireballEntity -> smallFireballEntity.b(stack)));
                stack.subtractAmount(1);
                return stack;
            }
            
            @Override
            protected void playSound(final BlockPointer blockPointer) {
                blockPointer.getWorld().playLevelEvent(1018, blockPointer.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.kE, new BoatDispenserBehavior(BoatEntity.Type.OAK));
        DispenserBlock.registerBehavior(Items.oY, new BoatDispenserBehavior(BoatEntity.Type.SPRUCE));
        DispenserBlock.registerBehavior(Items.oZ, new BoatDispenserBehavior(BoatEntity.Type.BIRCH));
        DispenserBlock.registerBehavior(Items.pa, new BoatDispenserBehavior(BoatEntity.Type.JUNGLE));
        DispenserBlock.registerBehavior(Items.pc, new BoatDispenserBehavior(BoatEntity.Type.DARK_OAK));
        DispenserBlock.registerBehavior(Items.pb, new BoatDispenserBehavior(BoatEntity.Type.ACACIA));
        final DispenserBehavior dispenserBehavior2 = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior b = new ItemDispenserBehavior();
            
            public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final BucketItem bucketItem3 = (BucketItem)stack.getItem();
                final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                final World world5 = pointer.getWorld();
                if (bucketItem3.placeFluid(null, world5, blockPos4, null)) {
                    bucketItem3.onEmptied(world5, stack, blockPos4);
                    return new ItemStack(Items.kx);
                }
                return this.b.dispense(pointer, stack);
            }
        };
        DispenserBlock.registerBehavior(Items.kz, dispenserBehavior2);
        DispenserBlock.registerBehavior(Items.ky, dispenserBehavior2);
        DispenserBlock.registerBehavior(Items.kI, dispenserBehavior2);
        DispenserBlock.registerBehavior(Items.kJ, dispenserBehavior2);
        DispenserBlock.registerBehavior(Items.kH, dispenserBehavior2);
        DispenserBlock.registerBehavior(Items.kK, dispenserBehavior2);
        DispenserBlock.registerBehavior(Items.kx, new ItemDispenserBehavior() {
            private final ItemDispenserBehavior b = new ItemDispenserBehavior();
            
            public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final IWorld iWorld3 = pointer.getWorld();
                final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                final BlockState blockState5 = iWorld3.getBlockState(blockPos4);
                final Block block6 = blockState5.getBlock();
                if (!(block6 instanceof FluidDrainable)) {
                    return super.dispenseStack(pointer, stack);
                }
                final Fluid fluid8 = ((FluidDrainable)block6).tryDrainFluid(iWorld3, blockPos4, blockState5);
                if (!(fluid8 instanceof BaseFluid)) {
                    return super.dispenseStack(pointer, stack);
                }
                final Item item7 = fluid8.getBucketItem();
                stack.subtractAmount(1);
                if (stack.isEmpty()) {
                    return new ItemStack(item7);
                }
                if (pointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(new ItemStack(item7)) < 0) {
                    this.b.dispense(pointer, new ItemStack(item7));
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Items.jd, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final World world3 = pointer.getWorld();
                this.success = true;
                final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                final BlockState blockState5 = world3.getBlockState(blockPos4);
                if (FlintAndSteelItem.canSetOnFire(blockState5, world3, blockPos4)) {
                    world3.setBlockState(blockPos4, Blocks.bM.getDefaultState());
                }
                else if (FlintAndSteelItem.canBeLit(blockState5)) {
                    world3.setBlockState(blockPos4, ((AbstractPropertyContainer<O, BlockState>)blockState5).<Comparable, Boolean>with((Property<Comparable>)Properties.LIT, true));
                }
                else if (blockState5.getBlock() instanceof TntBlock) {
                    TntBlock.primeTnt(world3, blockPos4);
                    world3.clearBlockState(blockPos4, false);
                }
                else {
                    this.success = false;
                }
                if (this.success && stack.applyDamage(1, world3.random, (ServerPlayerEntity)null)) {
                    stack.setAmount(0);
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Items.lw, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                this.success = true;
                final World world3 = pointer.getWorld();
                final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                if (BoneMealItem.useOnFertilizable(stack, world3, blockPos4) || BoneMealItem.useOnGround(stack, world3, blockPos4, null)) {
                    if (!world3.isClient) {
                        world3.playLevelEvent(2005, blockPos4, 0);
                    }
                }
                else {
                    this.success = false;
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.bG, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final World world3 = pointer.getWorld();
                final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                final PrimedTntEntity primedTntEntity5 = new PrimedTntEntity(world3, blockPos4.getX() + 0.5, blockPos4.getY(), blockPos4.getZ() + 0.5, null);
                world3.spawnEntity(primedTntEntity5);
                world3.playSound(null, primedTntEntity5.x, primedTntEntity5.y, primedTntEntity5.z, SoundEvents.lD, SoundCategory.e, 1.0f, 1.0f);
                stack.subtractAmount(1);
                return stack;
            }
        });
        final DispenserBehavior dispenserBehavior3 = new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                this.success = !ArmorItem.dispenseArmor(pointer, stack).isEmpty();
                return stack;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, dispenserBehavior3);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, dispenserBehavior3);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, dispenserBehavior3);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, dispenserBehavior3);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, dispenserBehavior3);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final World world3 = pointer.getWorld();
                final Direction direction4 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
                final BlockPos blockPos5 = pointer.getBlockPos().offset(direction4);
                this.success = true;
                if (world3.isAir(blockPos5) && WitherSkullBlock.canDispense(world3, blockPos5, stack)) {
                    world3.setBlockState(blockPos5, ((AbstractPropertyContainer<O, BlockState>)Blocks.eW.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SkullBlock.ROTATION, (direction4.getAxis() == Direction.Axis.Y) ? 0 : (direction4.getOpposite().getHorizontal() * 4)), 3);
                    final BlockEntity blockEntity6 = world3.getBlockEntity(blockPos5);
                    if (blockEntity6 instanceof SkullBlockEntity) {
                        WitherSkullBlock.onPlaced(world3, blockPos5, (SkullBlockEntity)blockEntity6);
                    }
                    stack.subtractAmount(1);
                }
                else if (ArmorItem.dispenseArmor(pointer, stack).isEmpty()) {
                    this.success = false;
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.cN, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final World world3 = pointer.getWorld();
                final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                final CarvedPumpkinBlock carvedPumpkinBlock5 = (CarvedPumpkinBlock)Blocks.cN;
                this.success = true;
                if (world3.isAir(blockPos4) && carvedPumpkinBlock5.a((ViewableWorld)world3, blockPos4)) {
                    if (!world3.isClient) {
                        world3.setBlockState(blockPos4, carvedPumpkinBlock5.getDefaultState(), 3);
                    }
                    stack.subtractAmount(1);
                }
                else {
                    final ItemStack itemStack6 = ArmorItem.dispenseArmor(pointer, stack);
                    if (itemStack6.isEmpty()) {
                        this.success = false;
                    }
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.iH.getItem(), new BlockPlacementDispenserBehavior());
        for (final DyeColor dyeColor7 : DyeColor.values()) {
            DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dyeColor7).getItem(), new BlockPlacementDispenserBehavior());
        }
        DispenserBlock.registerBehavior(Items.lW.getItem(), new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final World world3 = pointer.getWorld();
                if (!world3.isClient()) {
                    this.success = false;
                    final BlockPos blockPos4 = pointer.getBlockPos().offset(pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
                    final List<SheepEntity> list5 = world3.<SheepEntity>getEntities(SheepEntity.class, new BoundingBox(blockPos4));
                    for (final SheepEntity sheepEntity7 : list5) {
                        if (sheepEntity7.isAlive() && !sheepEntity7.isSheared() && !sheepEntity7.isChild()) {
                            sheepEntity7.dropItems();
                            if (stack.applyDamage(1, world3.random, (ServerPlayerEntity)null)) {
                                stack.setAmount(0);
                            }
                            this.success = true;
                            break;
                        }
                    }
                }
                return stack;
            }
        });
    }
}
