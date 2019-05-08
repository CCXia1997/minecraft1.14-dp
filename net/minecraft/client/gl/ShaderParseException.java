package net.minecraft.client.gl;

import org.apache.commons.lang3.StringUtils;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class ShaderParseException extends IOException
{
    private final List<JsonStackTrace> traces;
    private final String message;
    
    public ShaderParseException(final String message) {
        (this.traces = Lists.newArrayList()).add(new JsonStackTrace());
        this.message = message;
    }
    
    public ShaderParseException(final String message, final Throwable cause) {
        super(cause);
        (this.traces = Lists.newArrayList()).add(new JsonStackTrace());
        this.message = message;
    }
    
    public void addFaultyElement(final String jsonKey) {
        this.traces.get(0).add(jsonKey);
    }
    
    public void addFaultyFile(final String path) {
        this.traces.get(0).fileName = path;
        this.traces.add(0, new JsonStackTrace());
    }
    
    @Override
    public String getMessage() {
        return "Invalid " + this.traces.get(this.traces.size() - 1) + ": " + this.message;
    }
    
    public static ShaderParseException wrap(final Exception cause) {
        if (cause instanceof ShaderParseException) {
            return (ShaderParseException)cause;
        }
        String string2 = cause.getMessage();
        if (cause instanceof FileNotFoundException) {
            string2 = "File not found";
        }
        return new ShaderParseException(string2, cause);
    }
    
    @Environment(EnvType.CLIENT)
    public static class JsonStackTrace
    {
        @Nullable
        private String fileName;
        private final List<String> faultyElements;
        
        private JsonStackTrace() {
            this.faultyElements = Lists.newArrayList();
        }
        
        private void add(final String element) {
            this.faultyElements.add(0, element);
        }
        
        public String joinStackTrace() {
            return StringUtils.join((Iterable)this.faultyElements, "->");
        }
        
        @Override
        public String toString() {
            if (this.fileName != null) {
                if (this.faultyElements.isEmpty()) {
                    return this.fileName;
                }
                return this.fileName + " " + this.joinStackTrace();
            }
            else {
                if (this.faultyElements.isEmpty()) {
                    return "(Unknown file)";
                }
                return "(Unknown file) " + this.joinStackTrace();
            }
        }
    }
}
