package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.LightType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.function.BooleanSupplier;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkStatus;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.chunk.ChunkManager;

@Environment(EnvType.CLIENT)
public class ClientChunkManager extends ChunkManager
{
    private static final Logger LOGGER;
    private final WorldChunk emptyChunk;
    private final LightingProvider lightingProvider;
    private volatile ClientChunkMap chunks;
    private final ClientWorld world;
    
    public ClientChunkManager(final ClientWorld clientWorld, final int integer) {
        this.world = clientWorld;
        this.emptyChunk = new EmptyChunk(clientWorld, new ChunkPos(0, 0));
        this.lightingProvider = new LightingProvider(this, true, clientWorld.getDimension().hasSkyLight());
        this.chunks = new ClientChunkMap(b(integer));
    }
    
    @Override
    public LightingProvider getLightingProvider() {
        return this.lightingProvider;
    }
    
    private static boolean a(@Nullable final WorldChunk worldChunk, final int integer2, final int integer3) {
        if (worldChunk == null) {
            return false;
        }
        final ChunkPos chunkPos4 = worldChunk.getPos();
        return chunkPos4.x == integer2 && chunkPos4.z == integer3;
    }
    
    public void unload(final int chunkX, final int chunkZ) {
        if (!this.chunks.hasChunk(chunkX, chunkZ)) {
            return;
        }
        final int integer3 = this.chunks.index(chunkX, chunkZ);
        final WorldChunk worldChunk4 = this.chunks.getChunk(integer3);
        if (a(worldChunk4, chunkX, chunkZ)) {
            this.chunks.a(integer3, worldChunk4, null);
        }
    }
    
    @Nullable
    @Override
    public WorldChunk getChunk(final int x, final int z, final ChunkStatus leastStatus, final boolean create) {
        if (this.chunks.hasChunk(x, z)) {
            final WorldChunk worldChunk5 = this.chunks.getChunk(this.chunks.index(x, z));
            if (a(worldChunk5, x, z)) {
                return worldChunk5;
            }
        }
        if (create) {
            return this.emptyChunk;
        }
        return null;
    }
    
    @Override
    public BlockView getWorld() {
        return this.world;
    }
    
    @Nullable
    public WorldChunk loadChunkFromPacket(final World world, final int chunkX, final int chunkZ, final PacketByteBuf data, final CompoundTag nbt, final int updatedSectionsBits, final boolean clearOld) {
        if (!this.chunks.hasChunk(chunkX, chunkZ)) {
            ClientChunkManager.LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ);
            return null;
        }
        final int integer8 = this.chunks.index(chunkX, chunkZ);
        WorldChunk worldChunk9 = this.chunks.chunks.get(integer8);
        if (!a(worldChunk9, chunkX, chunkZ)) {
            if (!clearOld) {
                ClientChunkManager.LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", chunkX, chunkZ);
                return null;
            }
            worldChunk9 = new WorldChunk(world, new ChunkPos(chunkX, chunkZ), new Biome[256]);
            worldChunk9.loadFromPacket(data, nbt, updatedSectionsBits, clearOld);
            this.chunks.unload(integer8, worldChunk9);
        }
        else {
            worldChunk9.loadFromPacket(data, nbt, updatedSectionsBits, clearOld);
        }
        final ChunkSection[] arr10 = worldChunk9.getSectionArray();
        final LightingProvider lightingProvider11 = this.getLightingProvider();
        lightingProvider11.suppressLight(new ChunkPos(chunkX, chunkZ), true);
        for (int integer9 = 0; integer9 < arr10.length; ++integer9) {
            final ChunkSection chunkSection13 = arr10[integer9];
            lightingProvider11.updateSectionStatus(ChunkSectionPos.from(chunkX, integer9, chunkZ), ChunkSection.isEmpty(chunkSection13));
        }
        return worldChunk9;
    }
    
    @Override
    public void tick(final BooleanSupplier booleanSupplier) {
    }
    
    public void setChunkMapCenter(final int x, final int z) {
        this.chunks.centerChunkX = x;
        this.chunks.centerChunkZ = z;
    }
    
    public void updateLoadDistance(final int integer) {
        final int integer2 = this.chunks.loadDistance;
        final int integer3 = b(integer);
        if (integer2 != integer3) {
            final ClientChunkMap clientChunkMap4 = new ClientChunkMap(integer3);
            clientChunkMap4.centerChunkX = this.chunks.centerChunkX;
            clientChunkMap4.centerChunkZ = this.chunks.centerChunkZ;
            for (int integer4 = 0; integer4 < this.chunks.chunks.length(); ++integer4) {
                final WorldChunk worldChunk6 = this.chunks.chunks.get(integer4);
                if (worldChunk6 != null) {
                    final ChunkPos chunkPos7 = worldChunk6.getPos();
                    if (clientChunkMap4.hasChunk(chunkPos7.x, chunkPos7.z)) {
                        clientChunkMap4.unload(clientChunkMap4.index(chunkPos7.x, chunkPos7.z), worldChunk6);
                    }
                }
            }
            this.chunks = clientChunkMap4;
        }
    }
    
    private static int b(final int integer) {
        return Math.max(2, integer) + 3;
    }
    
    @Override
    public String getStatus() {
        return "MultiplayerChunkCache: " + this.chunks.chunks.length() + ", " + this.g();
    }
    
    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        return null;
    }
    
    public int g() {
        return this.chunks.g;
    }
    
    @Override
    public void onLightUpdate(final LightType type, final ChunkSectionPos chunkSectionPos) {
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkY(), chunkSectionPos.getChunkZ());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    final class ClientChunkMap
    {
        private final AtomicReferenceArray<WorldChunk> chunks;
        private final int loadDistance;
        private final int loadDiameter;
        private volatile int centerChunkX;
        private volatile int centerChunkZ;
        private int g;
        
        private ClientChunkMap(final int loadDistance) {
            this.loadDistance = loadDistance;
            this.loadDiameter = loadDistance * 2 + 1;
            this.chunks = new AtomicReferenceArray<WorldChunk>(this.loadDiameter * this.loadDiameter);
        }
        
        private int index(final int chunkX, final int chunkZ) {
            return Math.floorMod(chunkZ, this.loadDiameter) * this.loadDiameter + Math.floorMod(chunkX, this.loadDiameter);
        }
        
        protected void unload(final int chunkX, @Nullable final WorldChunk worldChunk) {
            final WorldChunk worldChunk2 = this.chunks.getAndSet(chunkX, worldChunk);
            if (worldChunk2 != null) {
                --this.g;
                ClientChunkManager.this.world.unloadBlockEntities(worldChunk2);
            }
            if (worldChunk != null) {
                ++this.g;
            }
        }
        
        protected WorldChunk a(final int integer, final WorldChunk worldChunk2, @Nullable final WorldChunk worldChunk3) {
            if (this.chunks.compareAndSet(integer, worldChunk2, worldChunk3) && worldChunk3 == null) {
                --this.g;
            }
            ClientChunkManager.this.world.unloadBlockEntities(worldChunk2);
            return worldChunk2;
        }
        
        private boolean hasChunk(final int chunkX, final int chunkZ) {
            return Math.abs(chunkX - this.centerChunkX) <= this.loadDistance && Math.abs(chunkZ - this.centerChunkZ) <= this.loadDistance;
        }
        
        @Nullable
        protected WorldChunk getChunk(final int chunkX) {
            return this.chunks.get(chunkX);
        }
    }
}
