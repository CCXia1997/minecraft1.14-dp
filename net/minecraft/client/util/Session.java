package net.minecraft.client.util;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import com.mojang.util.UUIDTypeAdapter;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Session
{
    private final String username;
    private final String uuid;
    private final String accessToken;
    private final AccountType accountType;
    
    public Session(final String username, final String uuid, final String accessToken, final String string4) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.accountType = AccountType.byName(string4);
    }
    
    public String getSessionId() {
        return "token:" + this.accessToken + ":" + this.uuid;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public GameProfile getProfile() {
        try {
            final UUID uUID1 = UUIDTypeAdapter.fromString(this.getUuid());
            return new GameProfile(uUID1, this.getUsername());
        }
        catch (IllegalArgumentException illegalArgumentException1) {
            return new GameProfile((UUID)null, this.getUsername());
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum AccountType
    {
        LEGACY("legacy"), 
        MOJANG("mojang");
        
        private static final Map<String, AccountType> c;
        private final String d;
        
        private AccountType(final String string1) {
            this.d = string1;
        }
        
        @Nullable
        public static AccountType byName(final String string) {
            return AccountType.c.get(string.toLowerCase(Locale.ROOT));
        }
        
        static {
            c = Arrays.<AccountType>stream(values()).collect(Collectors.toMap(accountType -> accountType.d, Function.<AccountType>identity()));
        }
    }
}
