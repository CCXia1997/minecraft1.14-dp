package net.minecraft.stat;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.StatisticsS2CPacket;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.SharedConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.Tag;
import java.util.Map;
import com.google.gson.JsonObject;
import java.util.function.Function;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import com.google.gson.JsonElement;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import com.google.gson.internal.Streams;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import com.mojang.datafixers.DataFixer;
import net.minecraft.entity.player.PlayerEntity;
import com.google.gson.JsonParseException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import com.google.common.collect.Sets;
import java.util.Set;
import java.io.File;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler extends StatHandler
{
    private static final Logger LOGGER;
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> pendingStats;
    private int lastStatsUpdate;
    
    public ServerStatHandler(final MinecraftServer server, final File file) {
        this.pendingStats = Sets.newHashSet();
        this.lastStatsUpdate = -300;
        this.server = server;
        this.file = file;
        if (file.isFile()) {
            try {
                this.parse(server.getDataFixer(), FileUtils.readFileToString(file));
            }
            catch (IOException iOException3) {
                ServerStatHandler.LOGGER.error("Couldn't read statistics file {}", file, iOException3);
            }
            catch (JsonParseException jsonParseException3) {
                ServerStatHandler.LOGGER.error("Couldn't parse statistics file {}", file, jsonParseException3);
            }
        }
    }
    
    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.asString());
        }
        catch (IOException iOException1) {
            ServerStatHandler.LOGGER.error("Couldn't save stats", (Throwable)iOException1);
        }
    }
    
    @Override
    public void setStat(final PlayerEntity player, final Stat<?> stat, final int value) {
        super.setStat(player, stat, value);
        this.pendingStats.add(stat);
    }
    
    private Set<Stat<?>> takePendingStats() {
        final Set<Stat<?>> set1 = Sets.newHashSet(this.pendingStats);
        this.pendingStats.clear();
        return set1;
    }
    
    public void parse(final DataFixer dataFixer, final String json) {
        try (final JsonReader jsonReader3 = new JsonReader(new StringReader(json))) {
            jsonReader3.setLenient(false);
            final JsonElement jsonElement5 = Streams.parse(jsonReader3);
            if (jsonElement5.isJsonNull()) {
                ServerStatHandler.LOGGER.error("Unable to parse Stat data from {}", this.file);
                return;
            }
            CompoundTag compoundTag6 = jsonToCompound(jsonElement5.getAsJsonObject());
            if (!compoundTag6.containsKey("DataVersion", 99)) {
                compoundTag6.putInt("DataVersion", 1343);
            }
            compoundTag6 = TagHelper.update(dataFixer, DataFixTypes.g, compoundTag6, compoundTag6.getInt("DataVersion"));
            if (compoundTag6.containsKey("stats", 10)) {
                final CompoundTag compoundTag7 = compoundTag6.getCompound("stats");
                for (final String string9 : compoundTag7.getKeys()) {
                    if (compoundTag7.containsKey(string9, 10)) {
                        final CompoundTag compoundTag8;
                        final Iterator<String> iterator2;
                        String string10;
                        SystemUtil.<StatType<?>>ifPresentOrElse(Registry.STAT_TYPE.getOrEmpty(new Identifier(string9)), statType -> {
                            compoundTag8 = compoundTag7.getCompound(string9);
                            compoundTag8.getKeys().iterator();
                            while (iterator2.hasNext()) {
                                string10 = iterator2.next();
                                if (compoundTag8.containsKey(string10, 99)) {
                                    SystemUtil.<Stat<Object>>ifPresentOrElse(this.createStat(statType, string10), stat -> this.statMap.put(stat, compoundTag8.getInt(string10)), () -> ServerStatHandler.LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, string10));
                                }
                                else {
                                    ServerStatHandler.LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, compoundTag8.getTag(string10), string10);
                                }
                            }
                            return;
                        }, () -> ServerStatHandler.LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, string9));
                    }
                }
            }
        }
        catch (JsonParseException | IOException ex2) {
            final Exception ex;
            final Exception exception3 = ex;
            ServerStatHandler.LOGGER.error("Unable to parse Stat data from {}", this.file, exception3);
        }
    }
    
    private <T> Optional<Stat<T>> createStat(final StatType<T> type, final String id) {
        return Optional.<Identifier>ofNullable(Identifier.create(id)).flatMap(type.getRegistry()::getOrEmpty).<Stat<T>>map(type::getOrCreateStat);
    }
    
    private static CompoundTag jsonToCompound(final JsonObject jsonObject) {
        final CompoundTag compoundTag2 = new CompoundTag();
        for (final Map.Entry<String, JsonElement> entry4 : jsonObject.entrySet()) {
            final JsonElement jsonElement5 = entry4.getValue();
            if (jsonElement5.isJsonObject()) {
                compoundTag2.put(entry4.getKey(), jsonToCompound(jsonElement5.getAsJsonObject()));
            }
            else {
                if (!jsonElement5.isJsonPrimitive()) {
                    continue;
                }
                final JsonPrimitive jsonPrimitive6 = jsonElement5.getAsJsonPrimitive();
                if (!jsonPrimitive6.isNumber()) {
                    continue;
                }
                compoundTag2.putInt(entry4.getKey(), jsonPrimitive6.getAsInt());
            }
        }
        return compoundTag2;
    }
    
    protected String asString() {
        final Map<StatType<?>, JsonObject> map1 = Maps.newHashMap();
        for (final Object2IntMap.Entry<Stat<?>> entry3 : this.statMap.object2IntEntrySet()) {
            final Stat<?> stat4 = entry3.getKey();
            map1.computeIfAbsent(stat4.getType(), statType -> new JsonObject()).addProperty(ServerStatHandler.getStatId(stat4).toString(), entry3.getIntValue());
        }
        final JsonObject jsonObject2 = new JsonObject();
        for (final Map.Entry<StatType<?>, JsonObject> entry4 : map1.entrySet()) {
            jsonObject2.add(Registry.STAT_TYPE.getId(entry4.getKey()).toString(), entry4.getValue());
        }
        final JsonObject jsonObject3 = new JsonObject();
        jsonObject3.add("stats", jsonObject2);
        jsonObject3.addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return jsonObject3.toString();
    }
    
    private static <T> Identifier getStatId(final Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }
    
    public void updateStatSet() {
        this.pendingStats.addAll(this.statMap.keySet());
    }
    
    public void sendStats(final ServerPlayerEntity player) {
        final int integer2 = this.server.getTicks();
        final Object2IntMap<Stat<?>> object2IntMap3 = (Object2IntMap<Stat<?>>)new Object2IntOpenHashMap();
        if (integer2 - this.lastStatsUpdate > 300) {
            this.lastStatsUpdate = integer2;
            for (final Stat<?> stat5 : this.takePendingStats()) {
                object2IntMap3.put(stat5, this.getStat(stat5));
            }
        }
        player.networkHandler.sendPacket(new StatisticsS2CPacket(object2IntMap3));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
