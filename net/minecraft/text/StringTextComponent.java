package net.minecraft.text;

public class StringTextComponent extends AbstractTextComponent
{
    private final String text;
    
    public StringTextComponent(final String string) {
        this.text = string;
    }
    
    public String getTextField() {
        return this.text;
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    @Override
    public StringTextComponent copyShallow() {
        return new StringTextComponent(this.text);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof StringTextComponent) {
            final StringTextComponent stringTextComponent2 = (StringTextComponent)o;
            return this.text.equals(stringTextComponent2.getTextField()) && super.equals(o);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}
