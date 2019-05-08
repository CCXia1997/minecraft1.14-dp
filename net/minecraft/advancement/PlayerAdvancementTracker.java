package net.minecraft.advancement;

import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.network.Packet;
import java.util.Collection;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.google.common.base.Charsets;
import java.io.FileOutputStream;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Comparator;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.DataFixTypes;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import com.google.gson.internal.Streams;
import com.mojang.datafixers.types.JsonOps;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.Criterions;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Set;
import java.io.File;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancementTracker
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE;
    private final MinecraftServer server;
    private final File advancementFile;
    private final Map<Advancement, AdvancementProgress> advancementToProgress;
    private final Set<Advancement> visibleAdvancements;
    private final Set<Advancement> visibilityUpdates;
    private final Set<Advancement> progressUpdates;
    private ServerPlayerEntity owner;
    @Nullable
    private Advancement currentDisplayTab;
    private boolean dirty;
    
    public PlayerAdvancementTracker(final MinecraftServer minecraftServer, final File file, final ServerPlayerEntity serverPlayerEntity) {
        this.advancementToProgress = Maps.newLinkedHashMap();
        this.visibleAdvancements = Sets.newLinkedHashSet();
        this.visibilityUpdates = Sets.newLinkedHashSet();
        this.progressUpdates = Sets.newLinkedHashSet();
        this.dirty = true;
        this.server = minecraftServer;
        this.advancementFile = file;
        this.owner = serverPlayerEntity;
        this.load();
    }
    
    public void setOwner(final ServerPlayerEntity serverPlayerEntity) {
        this.owner = serverPlayerEntity;
    }
    
    public void clearCriterions() {
        for (final Criterion<?> criterion2 : Criterions.getAllCriterions()) {
            criterion2.endTracking(this);
        }
    }
    
    public void reload() {
        this.clearCriterions();
        this.advancementToProgress.clear();
        this.visibleAdvancements.clear();
        this.visibilityUpdates.clear();
        this.progressUpdates.clear();
        this.dirty = true;
        this.currentDisplayTab = null;
        this.load();
    }
    
    private void beginTrackingAllAdvancements() {
        for (final Advancement advancement2 : this.server.getAdvancementManager().getAdvancements()) {
            this.beginTracking(advancement2);
        }
    }
    
    private void updateCompleted() {
        final List<Advancement> list1 = Lists.newArrayList();
        for (final Map.Entry<Advancement, AdvancementProgress> entry3 : this.advancementToProgress.entrySet()) {
            if (entry3.getValue().isDone()) {
                list1.add(entry3.getKey());
                this.progressUpdates.add(entry3.getKey());
            }
        }
        for (final Advancement advancement3 : list1) {
            this.updateDisplay(advancement3);
        }
    }
    
    private void rewardEmptyAdvancements() {
        for (final Advancement advancement2 : this.server.getAdvancementManager().getAdvancements()) {
            if (advancement2.getCriteria().isEmpty()) {
                this.grantCriterion(advancement2, "");
                advancement2.getRewards().apply(this.owner);
            }
        }
    }
    
    private void load() {
        if (this.advancementFile.isFile()) {
            try (final JsonReader jsonReader1 = new JsonReader(new StringReader(Files.toString(this.advancementFile, StandardCharsets.UTF_8)))) {
                jsonReader1.setLenient(false);
                Dynamic<JsonElement> dynamic3 = (Dynamic<JsonElement>)new Dynamic((DynamicOps)JsonOps.INSTANCE, Streams.parse(jsonReader1));
                if (!dynamic3.get("DataVersion").asNumber().isPresent()) {
                    dynamic3 = (Dynamic<JsonElement>)dynamic3.set("DataVersion", dynamic3.createInt(1343));
                }
                dynamic3 = (Dynamic<JsonElement>)this.server.getDataFixer().update(DataFixTypes.i.getTypeReference(), (Dynamic)dynamic3, dynamic3.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
                dynamic3 = (Dynamic<JsonElement>)dynamic3.remove("DataVersion");
                final Map<Identifier, AdvancementProgress> map4 = PlayerAdvancementTracker.GSON.<Map<Identifier, AdvancementProgress>>getAdapter(PlayerAdvancementTracker.JSON_TYPE).fromJsonTree((JsonElement)dynamic3.getValue());
                if (map4 == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                final Stream<Map.Entry<Identifier, AdvancementProgress>> stream5 = map4.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                for (final Map.Entry<Identifier, AdvancementProgress> entry7 : stream5.collect(Collectors.<Map.Entry<Identifier, AdvancementProgress>>toList())) {
                    final Advancement advancement8 = this.server.getAdvancementManager().get(entry7.getKey());
                    if (advancement8 == null) {
                        PlayerAdvancementTracker.LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry7.getKey(), this.advancementFile);
                    }
                    else {
                        this.initProgress(advancement8, entry7.getValue());
                    }
                }
            }
            catch (JsonParseException jsonParseException1) {
                PlayerAdvancementTracker.LOGGER.error("Couldn't parse player advancements in {}", this.advancementFile, jsonParseException1);
            }
            catch (IOException iOException1) {
                PlayerAdvancementTracker.LOGGER.error("Couldn't access player advancements in {}", this.advancementFile, iOException1);
            }
        }
        this.rewardEmptyAdvancements();
        this.updateCompleted();
        this.beginTrackingAllAdvancements();
    }
    
    public void save() {
        final Map<Identifier, AdvancementProgress> map1 = Maps.newHashMap();
        for (final Map.Entry<Advancement, AdvancementProgress> entry3 : this.advancementToProgress.entrySet()) {
            final AdvancementProgress advancementProgress4 = entry3.getValue();
            if (advancementProgress4.isAnyObtained()) {
                map1.put(entry3.getKey().getId(), advancementProgress4);
            }
        }
        if (this.advancementFile.getParentFile() != null) {
            this.advancementFile.getParentFile().mkdirs();
        }
        final JsonElement jsonElement2 = PlayerAdvancementTracker.GSON.toJsonTree(map1);
        jsonElement2.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        try (final OutputStream outputStream3 = new FileOutputStream(this.advancementFile);
             final Writer writer5 = new OutputStreamWriter(outputStream3, Charsets.UTF_8.newEncoder())) {
            PlayerAdvancementTracker.GSON.toJson(jsonElement2, writer5);
        }
        catch (IOException iOException3) {
            PlayerAdvancementTracker.LOGGER.error("Couldn't save player advancements to {}", this.advancementFile, iOException3);
        }
    }
    
    public boolean grantCriterion(final Advancement advancement, final String criterion) {
        boolean boolean3 = false;
        final AdvancementProgress advancementProgress4 = this.getProgress(advancement);
        final boolean boolean4 = advancementProgress4.isDone();
        if (advancementProgress4.obtain(criterion)) {
            this.endTrackingCompleted(advancement);
            this.progressUpdates.add(advancement);
            boolean3 = true;
            if (!boolean4 && advancementProgress4.isDone()) {
                advancement.getRewards().apply(this.owner);
                if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat() && this.owner.world.getGameRules().getBoolean("announceAdvancements")) {
                    this.server.getPlayerManager().sendToAll(new TranslatableTextComponent("chat.type.advancement." + advancement.getDisplay().getFrame().getId(), new Object[] { this.owner.getDisplayName(), advancement.getTextComponent() }));
                }
            }
        }
        if (advancementProgress4.isDone()) {
            this.updateDisplay(advancement);
        }
        return boolean3;
    }
    
    public boolean revokeCriterion(final Advancement advancement, final String criterion) {
        boolean boolean3 = false;
        final AdvancementProgress advancementProgress4 = this.getProgress(advancement);
        if (advancementProgress4.reset(criterion)) {
            this.beginTracking(advancement);
            this.progressUpdates.add(advancement);
            boolean3 = true;
        }
        if (!advancementProgress4.isAnyObtained()) {
            this.updateDisplay(advancement);
        }
        return boolean3;
    }
    
    private void beginTracking(final Advancement advancement) {
        final AdvancementProgress advancementProgress2 = this.getProgress(advancement);
        if (advancementProgress2.isDone()) {
            return;
        }
        for (final Map.Entry<String, AdvancementCriterion> entry4 : advancement.getCriteria().entrySet()) {
            final CriterionProgress criterionProgress5 = advancementProgress2.getCriterionProgress(entry4.getKey());
            if (criterionProgress5 != null) {
                if (criterionProgress5.isObtained()) {
                    continue;
                }
                final CriterionConditions criterionConditions6 = entry4.getValue().getConditions();
                if (criterionConditions6 == null) {
                    continue;
                }
                final Criterion<CriterionConditions> criterion7 = Criterions.<CriterionConditions>getById(criterionConditions6.getId());
                if (criterion7 == null) {
                    continue;
                }
                criterion7.beginTrackingCondition(this, new Criterion.ConditionsContainer<CriterionConditions>(criterionConditions6, advancement, entry4.getKey()));
            }
        }
    }
    
    private void endTrackingCompleted(final Advancement advancement) {
        final AdvancementProgress advancementProgress2 = this.getProgress(advancement);
        for (final Map.Entry<String, AdvancementCriterion> entry4 : advancement.getCriteria().entrySet()) {
            final CriterionProgress criterionProgress5 = advancementProgress2.getCriterionProgress(entry4.getKey());
            if (criterionProgress5 != null) {
                if (!criterionProgress5.isObtained() && !advancementProgress2.isDone()) {
                    continue;
                }
                final CriterionConditions criterionConditions6 = entry4.getValue().getConditions();
                if (criterionConditions6 == null) {
                    continue;
                }
                final Criterion<CriterionConditions> criterion7 = Criterions.<CriterionConditions>getById(criterionConditions6.getId());
                if (criterion7 == null) {
                    continue;
                }
                criterion7.endTrackingCondition(this, new Criterion.ConditionsContainer<CriterionConditions>(criterionConditions6, advancement, entry4.getKey()));
            }
        }
    }
    
    public void sendUpdate(final ServerPlayerEntity serverPlayerEntity) {
        if (this.dirty || !this.visibilityUpdates.isEmpty() || !this.progressUpdates.isEmpty()) {
            final Map<Identifier, AdvancementProgress> map2 = Maps.newHashMap();
            final Set<Advancement> set3 = Sets.newLinkedHashSet();
            final Set<Identifier> set4 = Sets.newLinkedHashSet();
            for (final Advancement advancement6 : this.progressUpdates) {
                if (this.visibleAdvancements.contains(advancement6)) {
                    map2.put(advancement6.getId(), this.advancementToProgress.get(advancement6));
                }
            }
            for (final Advancement advancement6 : this.visibilityUpdates) {
                if (this.visibleAdvancements.contains(advancement6)) {
                    set3.add(advancement6);
                }
                else {
                    set4.add(advancement6.getId());
                }
            }
            if (this.dirty || !map2.isEmpty() || !set3.isEmpty() || !set4.isEmpty()) {
                serverPlayerEntity.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(this.dirty, set3, set4, map2));
                this.visibilityUpdates.clear();
                this.progressUpdates.clear();
            }
        }
        this.dirty = false;
    }
    
    public void setDisplayTab(@Nullable final Advancement advancement) {
        final Advancement advancement2 = this.currentDisplayTab;
        if (advancement != null && advancement.getParent() == null && advancement.getDisplay() != null) {
            this.currentDisplayTab = advancement;
        }
        else {
            this.currentDisplayTab = null;
        }
        if (advancement2 != this.currentDisplayTab) {
            this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket((this.currentDisplayTab == null) ? null : this.currentDisplayTab.getId()));
        }
    }
    
    public AdvancementProgress getProgress(final Advancement advancement) {
        AdvancementProgress advancementProgress2 = this.advancementToProgress.get(advancement);
        if (advancementProgress2 == null) {
            advancementProgress2 = new AdvancementProgress();
            this.initProgress(advancement, advancementProgress2);
        }
        return advancementProgress2;
    }
    
    private void initProgress(final Advancement advancement, final AdvancementProgress advancementProgress) {
        advancementProgress.init(advancement.getCriteria(), advancement.getRequirements());
        this.advancementToProgress.put(advancement, advancementProgress);
    }
    
    private void updateDisplay(final Advancement advancement) {
        final boolean boolean2 = this.canSee(advancement);
        final boolean boolean3 = this.visibleAdvancements.contains(advancement);
        if (boolean2 && !boolean3) {
            this.visibleAdvancements.add(advancement);
            this.visibilityUpdates.add(advancement);
            if (this.advancementToProgress.containsKey(advancement)) {
                this.progressUpdates.add(advancement);
            }
        }
        else if (!boolean2 && boolean3) {
            this.visibleAdvancements.remove(advancement);
            this.visibilityUpdates.add(advancement);
        }
        if (boolean2 != boolean3 && advancement.getParent() != null) {
            this.updateDisplay(advancement.getParent());
        }
        for (final Advancement advancement2 : advancement.getChildren()) {
            this.updateDisplay(advancement2);
        }
    }
    
    private boolean canSee(Advancement advancement) {
        for (int integer2 = 0; advancement != null && integer2 <= 2; advancement = advancement.getParent(), ++integer2) {
            if (integer2 == 0 && this.hasChildrenDone(advancement)) {
                return true;
            }
            if (advancement.getDisplay() == null) {
                return false;
            }
            final AdvancementProgress advancementProgress3 = this.getProgress(advancement);
            if (advancementProgress3.isDone()) {
                return true;
            }
            if (advancement.getDisplay().isHidden()) {
                return false;
            }
        }
        return false;
    }
    
    private boolean hasChildrenDone(final Advancement advancement) {
        final AdvancementProgress advancementProgress2 = this.getProgress(advancement);
        if (advancementProgress2.isDone()) {
            return true;
        }
        for (final Advancement advancement2 : advancement.getChildren()) {
            if (this.hasChildrenDone(advancement2)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(Identifier.class, new Identifier.Serializer()).setPrettyPrinting().create();
        JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {};
    }
}
