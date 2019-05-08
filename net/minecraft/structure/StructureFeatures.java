package net.minecraft.structure;

import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Locale;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.Logger;

public class StructureFeatures
{
    private static final Logger LOGGER;
    public static final StructureFeature<?> MINESHAFT;
    public static final StructureFeature<?> PILLAGER_OUTPOST;
    public static final StructureFeature<?> FORTRESS;
    public static final StructureFeature<?> STRONGHOLD;
    public static final StructureFeature<?> JUNGLE_PYRAMID;
    public static final StructureFeature<?> OCEAN_RUIN;
    public static final StructureFeature<?> DESERT_PYRAMID;
    public static final StructureFeature<?> IGLOO;
    public static final StructureFeature<?> SWAMP_HUT;
    public static final StructureFeature<?> MONUMENT;
    public static final StructureFeature<?> END_CITY;
    public static final StructureFeature<?> MANSION;
    public static final StructureFeature<?> BURIED_TREASURE;
    public static final StructureFeature<?> SHIPWRECK;
    public static final StructureFeature<?> VILLAGE;
    
    private static StructureFeature<?> register(final String name, final StructureFeature<?> feature) {
        return Registry.<StructureFeature<?>>register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), feature);
    }
    
    public static void initialize() {
    }
    
    @Nullable
    public static StructureStart readStructureStart(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final BiomeSource biomeSource, final CompoundTag tag) {
        final String string5 = tag.getString("id");
        if ("INVALID".equals(string5)) {
            return StructureStart.DEFAULT;
        }
        final StructureFeature<?> structureFeature6 = Registry.STRUCTURE_FEATURE.get(new Identifier(string5.toLowerCase(Locale.ROOT)));
        if (structureFeature6 == null) {
            StructureFeatures.LOGGER.error("Unknown feature id: {}", string5);
            return null;
        }
        final int integer7 = tag.getInt("ChunkX");
        final int integer8 = tag.getInt("ChunkZ");
        final Biome biome9 = tag.containsKey("biome") ? Registry.BIOME.get(new Identifier(tag.getString("biome"))) : biomeSource.getBiome(new BlockPos((integer7 << 4) + 9, 0, (integer8 << 4) + 9));
        final MutableIntBoundingBox mutableIntBoundingBox10 = tag.containsKey("BB") ? new MutableIntBoundingBox(tag.getIntArray("BB")) : MutableIntBoundingBox.empty();
        final ListTag listTag11 = tag.getList("Children", 10);
        try {
            final StructureStart structureStart12 = structureFeature6.getStructureStartFactory().create(structureFeature6, integer7, integer8, biome9, mutableIntBoundingBox10, 0, chunkGenerator.getSeed());
            for (int integer9 = 0; integer9 < listTag11.size(); ++integer9) {
                final CompoundTag compoundTag14 = listTag11.getCompoundTag(integer9);
                final String string6 = compoundTag14.getString("id");
                final StructurePieceType structurePieceType16 = Registry.STRUCTURE_PIECE.get(new Identifier(string6.toLowerCase(Locale.ROOT)));
                if (structurePieceType16 == null) {
                    StructureFeatures.LOGGER.error("Unknown structure piece id: {}", string6);
                }
                else {
                    try {
                        final StructurePiece structurePiece17 = structurePieceType16.load(structureManager, compoundTag14);
                        structureStart12.children.add(structurePiece17);
                    }
                    catch (Exception exception17) {
                        StructureFeatures.LOGGER.error("Exception loading structure piece with id {}", string6, exception17);
                    }
                }
            }
            return structureStart12;
        }
        catch (Exception exception18) {
            StructureFeatures.LOGGER.error("Failed Start with id {}", string5, exception18);
            return null;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
        PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
        FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
        STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
        JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
        OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
        DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
        IGLOO = register("Igloo", Feature.IGLOO);
        SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
        MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
        END_CITY = register("EndCity", Feature.END_CITY);
        MANSION = register("Mansion", Feature.WOODLAND_MANSION);
        BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
        SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
        VILLAGE = register("Village", Feature.VILLAGE);
    }
}
