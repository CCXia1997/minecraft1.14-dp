package net.minecraft.data;

import com.google.common.hash.Hashing;
import java.io.BufferedWriter;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;
import java.nio.file.Path;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import java.io.IOException;
import com.google.common.hash.HashFunction;

public interface DataProvider
{
    public static final HashFunction SHA1 = Hashing.sha1();
    
    void run(final DataCache arg1) throws IOException;
    
    String getName();
    
    default void writeToPath(final Gson gson, final DataCache cache, final JsonElement output, final Path path) throws IOException {
        final String string5 = gson.toJson(output);
        final String string6 = DataProvider.SHA1.hashUnencodedChars(string5).toString();
        if (!Objects.equals(cache.getOldSha1(path), string6) || !Files.exists(path)) {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            try (final BufferedWriter bufferedWriter7 = Files.newBufferedWriter(path)) {
                bufferedWriter7.write(string5);
            }
        }
        cache.updateSha1(path, string6);
    }
}
