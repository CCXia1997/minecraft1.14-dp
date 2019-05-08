package net.minecraft.text;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.event.ClickEvent;

public class Style
{
    private Style parent;
    private TextFormat color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private String insertion;
    private static final Style ROOT;
    
    @Nullable
    public TextFormat getColor() {
        return (this.color == null) ? this.getParent().getColor() : this.color;
    }
    
    public boolean isBold() {
        return (this.bold == null) ? this.getParent().isBold() : this.bold;
    }
    
    public boolean isItalic() {
        return (this.italic == null) ? this.getParent().isItalic() : this.italic;
    }
    
    public boolean isStrikethrough() {
        return (this.strikethrough == null) ? this.getParent().isStrikethrough() : this.strikethrough;
    }
    
    public boolean isUnderlined() {
        return (this.underline == null) ? this.getParent().isUnderlined() : this.underline;
    }
    
    public boolean isObfuscated() {
        return (this.obfuscated == null) ? this.getParent().isObfuscated() : this.obfuscated;
    }
    
    public boolean isEmpty() {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underline == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
    }
    
    @Nullable
    public ClickEvent getClickEvent() {
        return (this.clickEvent == null) ? this.getParent().getClickEvent() : this.clickEvent;
    }
    
    @Nullable
    public HoverEvent getHoverEvent() {
        return (this.hoverEvent == null) ? this.getParent().getHoverEvent() : this.hoverEvent;
    }
    
    @Nullable
    public String getInsertion() {
        return (this.insertion == null) ? this.getParent().getInsertion() : this.insertion;
    }
    
    public Style setColor(final TextFormat textFormat) {
        this.color = textFormat;
        return this;
    }
    
    public Style setBold(final Boolean boolean1) {
        this.bold = boolean1;
        return this;
    }
    
    public Style setItalic(final Boolean boolean1) {
        this.italic = boolean1;
        return this;
    }
    
    public Style setStrikethrough(final Boolean boolean1) {
        this.strikethrough = boolean1;
        return this;
    }
    
    public Style setUnderline(final Boolean boolean1) {
        this.underline = boolean1;
        return this;
    }
    
    public Style setObfuscated(final Boolean boolean1) {
        this.obfuscated = boolean1;
        return this;
    }
    
    public Style setClickEvent(final ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }
    
    public Style setHoverEvent(final HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }
    
    public Style setInsertion(final String string) {
        this.insertion = string;
        return this;
    }
    
    public Style setParent(final Style style) {
        this.parent = style;
        return this;
    }
    
    public String getFormatString() {
        if (!this.isEmpty()) {
            final StringBuilder stringBuilder1 = new StringBuilder();
            if (this.getColor() != null) {
                stringBuilder1.append(this.getColor());
            }
            if (this.isBold()) {
                stringBuilder1.append(TextFormat.r);
            }
            if (this.isItalic()) {
                stringBuilder1.append(TextFormat.u);
            }
            if (this.isUnderlined()) {
                stringBuilder1.append(TextFormat.t);
            }
            if (this.isObfuscated()) {
                stringBuilder1.append(TextFormat.q);
            }
            if (this.isStrikethrough()) {
                stringBuilder1.append(TextFormat.s);
            }
            return stringBuilder1.toString();
        }
        if (this.parent != null) {
            return this.parent.getFormatString();
        }
        return "";
    }
    
    private Style getParent() {
        return (this.parent == null) ? Style.ROOT : this.parent;
    }
    
    @Override
    public String toString() {
        return "Style{hasParent=" + (this.parent != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underline + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Style) {
            final Style style2 = (Style)o;
            if (this.isBold() == style2.isBold() && this.getColor() == style2.getColor() && this.isItalic() == style2.isItalic() && this.isObfuscated() == style2.isObfuscated() && this.isStrikethrough() == style2.isStrikethrough() && this.isUnderlined() == style2.isUnderlined()) {
                if (this.getClickEvent() != null) {
                    if (!this.getClickEvent().equals(style2.getClickEvent())) {
                        return false;
                    }
                }
                else if (style2.getClickEvent() != null) {
                    return false;
                }
                if (this.getHoverEvent() != null) {
                    if (!this.getHoverEvent().equals(style2.getHoverEvent())) {
                        return false;
                    }
                }
                else if (style2.getHoverEvent() != null) {
                    return false;
                }
                if ((this.getInsertion() == null) ? (style2.getInsertion() == null) : this.getInsertion().equals(style2.getInsertion())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.bold, this.italic, this.underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion);
    }
    
    public Style clone() {
        final Style style1 = new Style();
        style1.bold = this.bold;
        style1.italic = this.italic;
        style1.strikethrough = this.strikethrough;
        style1.underline = this.underline;
        style1.obfuscated = this.obfuscated;
        style1.color = this.color;
        style1.clickEvent = this.clickEvent;
        style1.hoverEvent = this.hoverEvent;
        style1.parent = this.parent;
        style1.insertion = this.insertion;
        return style1;
    }
    
    public Style copy() {
        final Style style1 = new Style();
        style1.setBold(this.isBold());
        style1.setItalic(this.isItalic());
        style1.setStrikethrough(this.isStrikethrough());
        style1.setUnderline(this.isUnderlined());
        style1.setObfuscated(this.isObfuscated());
        style1.setColor(this.getColor());
        style1.setClickEvent(this.getClickEvent());
        style1.setHoverEvent(this.getHoverEvent());
        style1.setInsertion(this.getInsertion());
        return style1;
    }
    
    static {
        ROOT = new Style() {
            @Nullable
            @Override
            public TextFormat getColor() {
                return null;
            }
            
            @Override
            public boolean isBold() {
                return false;
            }
            
            @Override
            public boolean isItalic() {
                return false;
            }
            
            @Override
            public boolean isStrikethrough() {
                return false;
            }
            
            @Override
            public boolean isUnderlined() {
                return false;
            }
            
            @Override
            public boolean isObfuscated() {
                return false;
            }
            
            @Nullable
            @Override
            public ClickEvent getClickEvent() {
                return null;
            }
            
            @Nullable
            @Override
            public HoverEvent getHoverEvent() {
                return null;
            }
            
            @Nullable
            @Override
            public String getInsertion() {
                return null;
            }
            
            @Override
            public Style setColor(final TextFormat textFormat) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setBold(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setItalic(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setStrikethrough(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setUnderline(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setObfuscated(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setClickEvent(final ClickEvent clickEvent) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setHoverEvent(final HoverEvent hoverEvent) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setParent(final Style style) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String toString() {
                return "Style.ROOT";
            }
            
            @Override
            public Style clone() {
                return this;
            }
            
            @Override
            public Style copy() {
                return this;
            }
            
            @Override
            public String getFormatString() {
                return "";
            }
        };
    }
    
    public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style>
    {
        @Nullable
        public Style a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            if (!functionJson.isJsonObject()) {
                return null;
            }
            final Style style4 = new Style();
            final JsonObject jsonObject5 = functionJson.getAsJsonObject();
            if (jsonObject5 == null) {
                return null;
            }
            if (jsonObject5.has("bold")) {
                style4.bold = jsonObject5.get("bold").getAsBoolean();
            }
            if (jsonObject5.has("italic")) {
                style4.italic = jsonObject5.get("italic").getAsBoolean();
            }
            if (jsonObject5.has("underlined")) {
                style4.underline = jsonObject5.get("underlined").getAsBoolean();
            }
            if (jsonObject5.has("strikethrough")) {
                style4.strikethrough = jsonObject5.get("strikethrough").getAsBoolean();
            }
            if (jsonObject5.has("obfuscated")) {
                style4.obfuscated = jsonObject5.get("obfuscated").getAsBoolean();
            }
            if (jsonObject5.has("color")) {
                style4.color = context.<TextFormat>deserialize(jsonObject5.get("color"), TextFormat.class);
            }
            if (jsonObject5.has("insertion")) {
                style4.insertion = jsonObject5.get("insertion").getAsString();
            }
            if (jsonObject5.has("clickEvent")) {
                final JsonObject jsonObject6 = jsonObject5.getAsJsonObject("clickEvent");
                if (jsonObject6 != null) {
                    final JsonPrimitive jsonPrimitive7 = jsonObject6.getAsJsonPrimitive("action");
                    final ClickEvent.Action action8 = (jsonPrimitive7 == null) ? null : ClickEvent.Action.get(jsonPrimitive7.getAsString());
                    final JsonPrimitive jsonPrimitive8 = jsonObject6.getAsJsonPrimitive("value");
                    final String string10 = (jsonPrimitive8 == null) ? null : jsonPrimitive8.getAsString();
                    if (action8 != null && string10 != null && action8.isSafe()) {
                        style4.clickEvent = new ClickEvent(action8, string10);
                    }
                }
            }
            if (jsonObject5.has("hoverEvent")) {
                final JsonObject jsonObject6 = jsonObject5.getAsJsonObject("hoverEvent");
                if (jsonObject6 != null) {
                    final JsonPrimitive jsonPrimitive7 = jsonObject6.getAsJsonPrimitive("action");
                    final HoverEvent.Action action9 = (jsonPrimitive7 == null) ? null : HoverEvent.Action.get(jsonPrimitive7.getAsString());
                    final TextComponent textComponent9 = context.<TextComponent>deserialize(jsonObject6.get("value"), TextComponent.class);
                    if (action9 != null && textComponent9 != null && action9.isSafe()) {
                        style4.hoverEvent = new HoverEvent(action9, textComponent9);
                    }
                }
            }
            return style4;
        }
        
        @Nullable
        public JsonElement a(final Style entry, final Type unused, final JsonSerializationContext context) {
            if (entry.isEmpty()) {
                return null;
            }
            final JsonObject jsonObject4 = new JsonObject();
            if (entry.bold != null) {
                jsonObject4.addProperty("bold", entry.bold);
            }
            if (entry.italic != null) {
                jsonObject4.addProperty("italic", entry.italic);
            }
            if (entry.underline != null) {
                jsonObject4.addProperty("underlined", entry.underline);
            }
            if (entry.strikethrough != null) {
                jsonObject4.addProperty("strikethrough", entry.strikethrough);
            }
            if (entry.obfuscated != null) {
                jsonObject4.addProperty("obfuscated", entry.obfuscated);
            }
            if (entry.color != null) {
                jsonObject4.add("color", context.serialize(entry.color));
            }
            if (entry.insertion != null) {
                jsonObject4.add("insertion", context.serialize(entry.insertion));
            }
            if (entry.clickEvent != null) {
                final JsonObject jsonObject5 = new JsonObject();
                jsonObject5.addProperty("action", entry.clickEvent.getAction().getName());
                jsonObject5.addProperty("value", entry.clickEvent.getValue());
                jsonObject4.add("clickEvent", jsonObject5);
            }
            if (entry.hoverEvent != null) {
                final JsonObject jsonObject5 = new JsonObject();
                jsonObject5.addProperty("action", entry.hoverEvent.getAction().getName());
                jsonObject5.add("value", context.serialize(entry.hoverEvent.getValue()));
                jsonObject4.add("hoverEvent", jsonObject5);
            }
            return jsonObject4;
        }
    }
}
