package net.minecraft.network;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.net.IDN;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerAddress
{
    private final String address;
    private final int port;
    
    private ServerAddress(final String address, final int integer) {
        this.address = address;
        this.port = integer;
    }
    
    public String getAddress() {
        try {
            return IDN.toASCII(this.address);
        }
        catch (IllegalArgumentException illegalArgumentException1) {
            return "";
        }
    }
    
    public int getPort() {
        return this.port;
    }
    
    public static ServerAddress parse(final String address) {
        if (address == null) {
            return null;
        }
        String[] arr2 = address.split(":");
        if (address.startsWith("[")) {
            final int integer3 = address.indexOf("]");
            if (integer3 > 0) {
                final String string4 = address.substring(1, integer3);
                String string5 = address.substring(integer3 + 1).trim();
                if (string5.startsWith(":") && !string5.isEmpty()) {
                    string5 = string5.substring(1);
                    arr2 = new String[] { string4, string5 };
                }
                else {
                    arr2 = new String[] { string4 };
                }
            }
        }
        if (arr2.length > 2) {
            arr2 = new String[] { address };
        }
        String string6 = arr2[0];
        int integer4 = (arr2.length > 1) ? portOrDefault(arr2[1], 25565) : 25565;
        if (integer4 == 25565) {
            final String[] arr3 = resolveSrv(string6);
            string6 = arr3[0];
            integer4 = portOrDefault(arr3[1], 25565);
        }
        return new ServerAddress(string6, integer4);
    }
    
    private static String[] resolveSrv(final String address) {
        try {
            final String string2 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            final Hashtable<String, String> hashtable3 = new Hashtable<String, String>();
            hashtable3.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable3.put("java.naming.provider.url", "dns:");
            hashtable3.put("com.sun.jndi.dns.timeout.retries", "1");
            final DirContext dirContext4 = new InitialDirContext(hashtable3);
            final Attributes attributes5 = dirContext4.getAttributes("_minecraft._tcp." + address, new String[] { "SRV" });
            final String[] arr6 = attributes5.get("srv").get().toString().split(" ", 4);
            return new String[] { arr6[3], arr6[2] };
        }
        catch (Throwable throwable2) {
            return new String[] { address, Integer.toString(25565) };
        }
    }
    
    private static int portOrDefault(final String port, final int def) {
        try {
            return Integer.parseInt(port.trim());
        }
        catch (Exception ex) {
            return def;
        }
    }
}
