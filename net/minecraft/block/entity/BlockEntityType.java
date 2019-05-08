package net.minecraft.block.entity;

import org.apache.logging.log4j.LogManager;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity>
{
    private static final Logger LOGGER;
    public static final BlockEntityType<FurnaceBlockEntity> FURNACE;
    public static final BlockEntityType<ChestBlockEntity> CHEST;
    public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST;
    public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST;
    public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX;
    public static final BlockEntityType<DispenserBlockEntity> DISPENSER;
    public static final BlockEntityType<DropperBlockEntity> DROPPER;
    public static final BlockEntityType<SignBlockEntity> SIGN;
    public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER;
    public static final BlockEntityType<PistonBlockEntity> PISTON;
    public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND;
    public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE;
    public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL;
    public static final BlockEntityType<BeaconBlockEntity> BEACON;
    public static final BlockEntityType<SkullBlockEntity> SKULL;
    public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR;
    public static final BlockEntityType<HopperBlockEntity> HOPPER;
    public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR;
    public static final BlockEntityType<BannerBlockEntity> BANNER;
    public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK;
    public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY;
    public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK;
    public static final BlockEntityType<ShulkerBoxBlockEntity> SHUlKER_BOX;
    public static final BlockEntityType<BedBlockEntity> BED;
    public static final BlockEntityType<ConduitBlockEntity> CONDUIT;
    public static final BlockEntityType<BarrelBlockEntity> BARREL;
    public static final BlockEntityType<SmokerBlockEntity> SMOKER;
    public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE;
    public static final BlockEntityType<LecternBlockEntity> LECTERN;
    public static final BlockEntityType<BellBlockEntity> BELL;
    public static final BlockEntityType<JigsawBlockEntity> JIGSAW;
    public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE;
    private final Supplier<? extends T> supplier;
    private final Type<?> type;
    
    @Nullable
    public static Identifier getId(final BlockEntityType<?> blockEntityType) {
        return Registry.BLOCK_ENTITY.getId(blockEntityType);
    }
    
    private static <T extends BlockEntity> BlockEntityType<T> create(final String string, final Builder<T> builder) {
        Type<?> type3 = null;
        try {
            type3 = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, string);
        }
        catch (IllegalStateException illegalStateException4) {
            if (SharedConstants.isDevelopment) {
                throw illegalStateException4;
            }
            BlockEntityType.LOGGER.warn("No data fixer registered for block entity {}", string);
        }
        return Registry.<BlockEntityType<T>>register(Registry.BLOCK_ENTITY, string, builder.build(type3));
    }
    
    public BlockEntityType(final Supplier<? extends T> supplier, final Type<?> type) {
        this.supplier = supplier;
        this.type = type;
    }
    
    @Nullable
    public T instantiate() {
        return (T)this.supplier.get();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        FURNACE = BlockEntityType.<FurnaceBlockEntity>create("furnace", Builder.<FurnaceBlockEntity>create(FurnaceBlockEntity::new));
        CHEST = BlockEntityType.<ChestBlockEntity>create("chest", Builder.<ChestBlockEntity>create(ChestBlockEntity::new));
        TRAPPED_CHEST = BlockEntityType.<TrappedChestBlockEntity>create("trapped_chest", Builder.<TrappedChestBlockEntity>create(TrappedChestBlockEntity::new));
        ENDER_CHEST = BlockEntityType.<EnderChestBlockEntity>create("ender_chest", Builder.<EnderChestBlockEntity>create(EnderChestBlockEntity::new));
        JUKEBOX = BlockEntityType.<JukeboxBlockEntity>create("jukebox", Builder.<JukeboxBlockEntity>create(JukeboxBlockEntity::new));
        DISPENSER = BlockEntityType.<DispenserBlockEntity>create("dispenser", Builder.<DispenserBlockEntity>create(DispenserBlockEntity::new));
        DROPPER = BlockEntityType.<DropperBlockEntity>create("dropper", Builder.<DropperBlockEntity>create(DropperBlockEntity::new));
        SIGN = BlockEntityType.<SignBlockEntity>create("sign", Builder.<SignBlockEntity>create(SignBlockEntity::new));
        MOB_SPAWNER = BlockEntityType.<MobSpawnerBlockEntity>create("mob_spawner", Builder.<MobSpawnerBlockEntity>create(MobSpawnerBlockEntity::new));
        PISTON = BlockEntityType.<PistonBlockEntity>create("piston", Builder.<PistonBlockEntity>create(PistonBlockEntity::new));
        BREWING_STAND = BlockEntityType.<BrewingStandBlockEntity>create("brewing_stand", Builder.<BrewingStandBlockEntity>create(BrewingStandBlockEntity::new));
        ENCHANTING_TABLE = BlockEntityType.<EnchantingTableBlockEntity>create("enchanting_table", Builder.<EnchantingTableBlockEntity>create(EnchantingTableBlockEntity::new));
        END_PORTAL = BlockEntityType.<EndPortalBlockEntity>create("end_portal", Builder.<EndPortalBlockEntity>create(EndPortalBlockEntity::new));
        BEACON = BlockEntityType.<BeaconBlockEntity>create("beacon", Builder.<BeaconBlockEntity>create(BeaconBlockEntity::new));
        SKULL = BlockEntityType.<SkullBlockEntity>create("skull", Builder.<SkullBlockEntity>create(SkullBlockEntity::new));
        DAYLIGHT_DETECTOR = BlockEntityType.<DaylightDetectorBlockEntity>create("daylight_detector", Builder.<DaylightDetectorBlockEntity>create(DaylightDetectorBlockEntity::new));
        HOPPER = BlockEntityType.<HopperBlockEntity>create("hopper", Builder.<HopperBlockEntity>create(HopperBlockEntity::new));
        COMPARATOR = BlockEntityType.<ComparatorBlockEntity>create("comparator", Builder.<ComparatorBlockEntity>create(ComparatorBlockEntity::new));
        BANNER = BlockEntityType.<BannerBlockEntity>create("banner", Builder.<BannerBlockEntity>create(BannerBlockEntity::new));
        STRUCTURE_BLOCK = BlockEntityType.<StructureBlockBlockEntity>create("structure_block", Builder.<StructureBlockBlockEntity>create(StructureBlockBlockEntity::new));
        END_GATEWAY = BlockEntityType.<EndGatewayBlockEntity>create("end_gateway", Builder.<EndGatewayBlockEntity>create(EndGatewayBlockEntity::new));
        COMMAND_BLOCK = BlockEntityType.<CommandBlockBlockEntity>create("command_block", Builder.<CommandBlockBlockEntity>create(CommandBlockBlockEntity::new));
        SHUlKER_BOX = BlockEntityType.<ShulkerBoxBlockEntity>create("shulker_box", Builder.<ShulkerBoxBlockEntity>create(ShulkerBoxBlockEntity::new));
        BED = BlockEntityType.<BedBlockEntity>create("bed", Builder.<BedBlockEntity>create(BedBlockEntity::new));
        CONDUIT = BlockEntityType.<ConduitBlockEntity>create("conduit", Builder.<ConduitBlockEntity>create(ConduitBlockEntity::new));
        BARREL = BlockEntityType.<BarrelBlockEntity>create("barrel", Builder.<BarrelBlockEntity>create(BarrelBlockEntity::new));
        SMOKER = BlockEntityType.<SmokerBlockEntity>create("smoker", Builder.<SmokerBlockEntity>create(SmokerBlockEntity::new));
        BLAST_FURNACE = BlockEntityType.<BlastFurnaceBlockEntity>create("blast_furnace", Builder.<BlastFurnaceBlockEntity>create(BlastFurnaceBlockEntity::new));
        LECTERN = BlockEntityType.<LecternBlockEntity>create("lectern", Builder.<LecternBlockEntity>create(LecternBlockEntity::new));
        BELL = BlockEntityType.<BellBlockEntity>create("bell", Builder.<BellBlockEntity>create(BellBlockEntity::new));
        JIGSAW = BlockEntityType.<JigsawBlockEntity>create("jigsaw", Builder.<JigsawBlockEntity>create(JigsawBlockEntity::new));
        CAMPFIRE = BlockEntityType.<CampfireBlockEntity>create("campfire", Builder.<CampfireBlockEntity>create(CampfireBlockEntity::new));
    }
    
    public static final class Builder<T extends BlockEntity>
    {
        private final Supplier<? extends T> supplier;
        
        private Builder(final Supplier<? extends T> supplier) {
            this.supplier = supplier;
        }
        
        public static <T extends BlockEntity> Builder<T> create(final Supplier<? extends T> supplier) {
            return new Builder<T>(supplier);
        }
        
        public BlockEntityType<T> build(final Type<?> type) {
            return new BlockEntityType<T>(this.supplier, type);
        }
    }
}
