package net.minecraft.util;

import java.text.ParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Iterators;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;
import java.io.BufferedReader;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonParseException;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.io.Reader;
import java.util.List;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Calendar;
import java.util.Date;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.authlib.Agent;
import com.mojang.authlib.ProfileLookupCallback;
import com.google.gson.GsonBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.ParameterizedType;
import java.io.File;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.GameProfile;
import java.util.Deque;
import java.util.UUID;
import java.util.Map;
import java.text.SimpleDateFormat;

public class UserCache
{
    public static final SimpleDateFormat EXPIRATION_DATE_FORMAT;
    private static boolean useRemote;
    private final Map<String, Entry> byName;
    private final Map<UUID, Entry> byUuid;
    private final Deque<GameProfile> byAccessTime;
    private final GameProfileRepository profileRepository;
    protected final Gson gson;
    private final File cacheFile;
    private static final ParameterizedType ENTRY_LIST_TYPE;
    
    public UserCache(final GameProfileRepository profileRepository, final File file) {
        this.byName = Maps.newHashMap();
        this.byUuid = Maps.newHashMap();
        this.byAccessTime = Lists.newLinkedList();
        this.profileRepository = profileRepository;
        this.cacheFile = file;
        final GsonBuilder gsonBuilder3 = new GsonBuilder();
        gsonBuilder3.registerTypeHierarchyAdapter(Entry.class, new JsonConverter());
        this.gson = gsonBuilder3.create();
        this.load();
    }
    
    private static GameProfile findProfileByName(final GameProfileRepository repository, final String name) {
        final GameProfile[] arr3 = { null };
        final ProfileLookupCallback profileLookupCallback4 = (ProfileLookupCallback)new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(final GameProfile profile) {
                arr3[0] = profile;
            }
            
            public void onProfileLookupFailed(final GameProfile profile, final Exception exception) {
                arr3[0] = null;
            }
        };
        repository.findProfilesByNames(new String[] { name }, Agent.MINECRAFT, profileLookupCallback4);
        if (!shouldUseRemote() && arr3[0] == null) {
            final UUID uUID5 = PlayerEntity.getUuidFromProfile(new GameProfile((UUID)null, name));
            final GameProfile gameProfile6 = new GameProfile(uUID5, name);
            profileLookupCallback4.onProfileLookupSucceeded(gameProfile6);
        }
        return arr3[0];
    }
    
    public static void setUseRemote(final boolean value) {
        UserCache.useRemote = value;
    }
    
    private static boolean shouldUseRemote() {
        return UserCache.useRemote;
    }
    
    public void add(final GameProfile gameProfile) {
        this.add(gameProfile, null);
    }
    
    private void add(final GameProfile profile, Date date) {
        final UUID uUID3 = profile.getId();
        if (date == null) {
            final Calendar calendar4 = Calendar.getInstance();
            calendar4.setTime(new Date());
            calendar4.add(2, 1);
            date = calendar4.getTime();
        }
        final Entry entry4 = new Entry(profile, date);
        if (this.byUuid.containsKey(uUID3)) {
            final Entry entry5 = this.byUuid.get(uUID3);
            this.byName.remove(entry5.getProfile().getName().toLowerCase(Locale.ROOT));
            this.byAccessTime.remove(profile);
        }
        this.byName.put(profile.getName().toLowerCase(Locale.ROOT), entry4);
        this.byUuid.put(uUID3, entry4);
        this.byAccessTime.addFirst(profile);
        this.save();
    }
    
    @Nullable
    public GameProfile findByName(final String string) {
        final String string2 = string.toLowerCase(Locale.ROOT);
        Entry entry3 = this.byName.get(string2);
        if (entry3 != null && new Date().getTime() >= entry3.expirationDate.getTime()) {
            this.byUuid.remove(entry3.getProfile().getId());
            this.byName.remove(entry3.getProfile().getName().toLowerCase(Locale.ROOT));
            this.byAccessTime.remove(entry3.getProfile());
            entry3 = null;
        }
        if (entry3 != null) {
            final GameProfile gameProfile4 = entry3.getProfile();
            this.byAccessTime.remove(gameProfile4);
            this.byAccessTime.addFirst(gameProfile4);
        }
        else {
            final GameProfile gameProfile4 = findProfileByName(this.profileRepository, string2);
            if (gameProfile4 != null) {
                this.add(gameProfile4);
                entry3 = this.byName.get(string2);
            }
        }
        this.save();
        return (entry3 == null) ? null : entry3.getProfile();
    }
    
    @Nullable
    public GameProfile getByUuid(final UUID uUID) {
        final Entry entry2 = this.byUuid.get(uUID);
        return (entry2 == null) ? null : entry2.getProfile();
    }
    
    private Entry getEntry(final UUID uUID) {
        final Entry entry2 = this.byUuid.get(uUID);
        if (entry2 != null) {
            final GameProfile gameProfile3 = entry2.getProfile();
            this.byAccessTime.remove(gameProfile3);
            this.byAccessTime.addFirst(gameProfile3);
        }
        return entry2;
    }
    
    public void load() {
        BufferedReader bufferedReader1 = null;
        try {
            bufferedReader1 = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);
            final List<Entry> list2 = JsonHelper.<List<Entry>>deserialize(this.gson, bufferedReader1, UserCache.ENTRY_LIST_TYPE);
            this.byName.clear();
            this.byUuid.clear();
            this.byAccessTime.clear();
            if (list2 != null) {
                for (final Entry entry4 : Lists.<Entry>reverse(list2)) {
                    if (entry4 != null) {
                        this.add(entry4.getProfile(), entry4.getExpirationDate());
                    }
                }
            }
        }
        catch (FileNotFoundException ex) {}
        catch (JsonParseException ex2) {}
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader1);
        }
    }
    
    public void save() {
        final String string1 = this.gson.toJson(this.getLastAccessedEntries(1000));
        BufferedWriter bufferedWriter2 = null;
        try {
            bufferedWriter2 = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);
            bufferedWriter2.write(string1);
        }
        catch (FileNotFoundException fileNotFoundException3) {}
        catch (IOException iOException3) {}
        finally {
            IOUtils.closeQuietly((Writer)bufferedWriter2);
        }
    }
    
    private List<Entry> getLastAccessedEntries(final int integer) {
        final List<Entry> list2 = Lists.newArrayList();
        final List<GameProfile> list3 = Lists.newArrayList(Iterators.limit(this.byAccessTime.iterator(), integer));
        for (final GameProfile gameProfile5 : list3) {
            final Entry entry6 = this.getEntry(gameProfile5.getId());
            if (entry6 == null) {
                continue;
            }
            list2.add(entry6);
        }
        return list2;
    }
    
    static {
        EXPIRATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        ENTRY_LIST_TYPE = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { Entry.class };
            }
            
            @Override
            public Type getRawType() {
                return List.class;
            }
            
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    class JsonConverter implements JsonDeserializer<Entry>, JsonSerializer<Entry>
    {
        private JsonConverter() {
        }
        
        public JsonElement a(final Entry entry, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("name", entry.getProfile().getName());
            final UUID uUID5 = entry.getProfile().getId();
            jsonObject4.addProperty("uuid", (uUID5 == null) ? "" : uUID5.toString());
            jsonObject4.addProperty("expiresOn", UserCache.EXPIRATION_DATE_FORMAT.format(entry.getExpirationDate()));
            return jsonObject4;
        }
        
        public Entry a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            if (!functionJson.isJsonObject()) {
                return null;
            }
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final JsonElement jsonElement5 = jsonObject4.get("name");
            final JsonElement jsonElement6 = jsonObject4.get("uuid");
            final JsonElement jsonElement7 = jsonObject4.get("expiresOn");
            if (jsonElement5 == null || jsonElement6 == null) {
                return null;
            }
            final String string8 = jsonElement6.getAsString();
            final String string9 = jsonElement5.getAsString();
            Date date10 = null;
            if (jsonElement7 != null) {
                try {
                    date10 = UserCache.EXPIRATION_DATE_FORMAT.parse(jsonElement7.getAsString());
                }
                catch (ParseException parseException11) {
                    date10 = null;
                }
            }
            if (string9 == null || string8 == null) {
                return null;
            }
            UUID uUID11;
            try {
                uUID11 = UUID.fromString(string8);
            }
            catch (Throwable throwable12) {
                return null;
            }
            return new Entry(new GameProfile(uUID11, string9), date10);
        }
    }
    
    class Entry
    {
        private final GameProfile profile;
        private final Date expirationDate;
        
        private Entry(final GameProfile gameProfile, final Date date) {
            this.profile = gameProfile;
            this.expirationDate = date;
        }
        
        public GameProfile getProfile() {
            return this.profile;
        }
        
        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }
}
