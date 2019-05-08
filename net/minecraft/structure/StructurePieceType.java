package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.CompoundTag;

public interface StructurePieceType
{
    public static final StructurePieceType MINESHAFT_CORRIDOR = register(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
    public static final StructurePieceType MINESHAFT_CROSSING = register(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
    public static final StructurePieceType MINESHAFT_ROOM = register(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
    public static final StructurePieceType MINESHAFT_STAIRS = register(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
    public static final StructurePieceType PILLAGER_OUTPOST = register(PillagerOutpostGenerator.Piece::new, "PCP");
    public static final StructurePieceType VILLAGE = register(VillageGenerator.Piece::new, "NVi");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = register(NetherFortressGenerator.BridgeCrossing::new, "NeBCr");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_END = register(NetherFortressGenerator.BridgeEnd::new, "NeBEF");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE = register(NetherFortressGenerator.Bridge::new, "NeBS");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_STAIRS = register(NetherFortressGenerator.CorridorStairs::new, "NeCCS");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_BALCONY = register(NetherFortressGenerator.CorridorBalcony::new, "NeCTB");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_EXIT = register(NetherFortressGenerator.CorridorExit::new, "NeCE");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_CROSSING = register(NetherFortressGenerator.CorridorCrossing::new, "NeSCSC");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_LEFT_TURN = register(NetherFortressGenerator.CorridorLeftTurn::new, "NeSCLT");
    public static final StructurePieceType NETHER_FORTRESS_SMALL_CORRIDOR = register(NetherFortressGenerator.SmallCorridor::new, "NeSC");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_RIGHT_TURN = register(NetherFortressGenerator.CorridorRightTurn::new, "NeSCRT");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM = register(NetherFortressGenerator.CorridorNetherWartsRoom::new, "NeCSR");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_PLATFORM = register(NetherFortressGenerator.BridgePlatform::new, "NeMT");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_SMALL_CROSSING = register(NetherFortressGenerator.BridgeSmallCrossing::new, "NeRC");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_STAIRS = register(NetherFortressGenerator.BridgeStairs::new, "NeSR");
    public static final StructurePieceType NETHER_FORTRESS_START = register(NetherFortressGenerator.Start::new, "NeStart");
    public static final StructurePieceType STRONGHOLD_CHEST_CORRIDOR = register(StrongholdGenerator.ChestCorridor::new, "SHCC");
    public static final StructurePieceType STRONGHOLD_SMALL_CORRIDOR = register(StrongholdGenerator.SmallCorridor::new, "SHFC");
    public static final StructurePieceType STRONGHOLD_FIVE_WAY_CROSSING = register(StrongholdGenerator.FiveWayCrossing::new, "SH5C");
    public static final StructurePieceType STRONGHOLD_LEFT_TURN = register(StrongholdGenerator.LeftTurn::new, "SHLT");
    public static final StructurePieceType STRONGJOLD_LIBRARY = register(StrongholdGenerator.Library::new, "SHLi");
    public static final StructurePieceType STRONGHOLD_PORTAL_ROOM = register(StrongholdGenerator.PortalRoom::new, "SHPR");
    public static final StructurePieceType STRONGHOLD_PRISON_HALL = register(StrongholdGenerator.PrisonHall::new, "SHPH");
    public static final StructurePieceType STRONGHOLD_RIGHT_TURN = register(StrongholdGenerator.RightTurn::new, "SHRT");
    public static final StructurePieceType STRONGHOLD_SQUARE_ROOM = register(StrongholdGenerator.SquareRoom::new, "SHRC");
    public static final StructurePieceType STRONGHOLD_SPIRAL_STAIRCASE = register(StrongholdGenerator.SpiralStaircase::new, "SHSD");
    public static final StructurePieceType STRONGHOLD_START = register(StrongholdGenerator.Start::new, "SHStart");
    public static final StructurePieceType STRONGHOLD_CORRIDOR = register(StrongholdGenerator.Corridor::new, "SHS");
    public static final StructurePieceType STRONGHOLD_STAIRS = register(StrongholdGenerator.Stairs::new, "SHSSD");
    public static final StructurePieceType JUNGLE_TEMPLE = register(JungleTempleGenerator::new, "TeJP");
    public static final StructurePieceType OCEAN_TEMPLE = register(OceanRuinGenerator.Piece::new, "ORP");
    public static final StructurePieceType IGLOO = register(IglooGenerator.Piece::new, "Iglu");
    public static final StructurePieceType SWAMP_HUT = register(SwampHutGenerator::new, "TeSH");
    public static final StructurePieceType DESERT_TEMPLE = register(DesertTempleGenerator::new, "TeDP");
    public static final StructurePieceType OCEAN_MONUMENT_BASE = register(OceanMonumentGenerator.Base::new, "OMB");
    public static final StructurePieceType OCEAN_MONUMENT_CORE_ROOM = register(OceanMonumentGenerator.CoreRoom::new, "OMCR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = register(OceanMonumentGenerator.DoubleXRoom::new, "OMDXR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_X_Y_ROOM = register(OceanMonumentGenerator.DoubleXYRoom::new, "OMDXYR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = register(OceanMonumentGenerator.DoubleYRoom::new, "OMDYR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM = register(OceanMonumentGenerator.DoubleYZRoom::new, "OMDYZR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = register(OceanMonumentGenerator.DoubleZRoom::new, "OMDZR");
    public static final StructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = register(OceanMonumentGenerator.Entry::new, "OMEntry");
    public static final StructurePieceType OCEAN_MONUMENT_PENTHOUSE = register(OceanMonumentGenerator.Penthouse::new, "OMPenthouse");
    public static final StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = register(OceanMonumentGenerator.SimpleRoom::new, "OMSimple");
    public static final StructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = register(OceanMonumentGenerator.SimpleRoomTop::new, "OMSimpleT");
    public static final StructurePieceType OCEAN_MONUMENT_WING_ROOM = register(OceanMonumentGenerator.WingRoom::new, "OMWR");
    public static final StructurePieceType END_CITY = register(EndCityGenerator.Piece::new, "ECP");
    public static final StructurePieceType WOODLAND_MANSION = register(WoodlandMansionGenerator.Piece::new, "WMP");
    public static final StructurePieceType BURIED_TREASURE = register(BuriedTreasureGenerator.Piece::new, "BTP");
    public static final StructurePieceType SHIPWRECK = register(ShipwreckGenerator.Piece::new, "Shipwreck");
    
    StructurePiece load(final StructureManager arg1, final CompoundTag arg2);
    
    default StructurePieceType register(final StructurePieceType pieceType, final String id) {
        return Registry.<StructurePieceType>register(Registry.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), pieceType);
    }
}
