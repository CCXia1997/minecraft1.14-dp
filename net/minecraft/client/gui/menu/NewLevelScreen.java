package net.minecraft.client.gui.menu;

import net.minecraft.world.level.LevelProperties;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.datafixers.NbtOps;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.StringUtils;
import java.util.Random;
import net.minecraft.util.FileNameUtil;
import net.minecraft.client.gui.Element;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class NewLevelScreen extends Screen
{
    private final Screen parent;
    private TextFieldWidget textFieldLevelName;
    private TextFieldWidget textFieldSeed;
    private String e;
    private String gameMode;
    private String g;
    private boolean structures;
    private boolean commandsAllowed;
    private boolean j;
    private boolean enableBonusItems;
    private boolean l;
    private boolean isCreatingLevel;
    private boolean n;
    private ButtonWidget buttonCreateLevel;
    private ButtonWidget buttonGameModeSwitch;
    private ButtonWidget buttonMoreOptions;
    private ButtonWidget buttonGenerateStructures;
    private ButtonWidget buttonGenerateBonusItems;
    private ButtonWidget buttonMapTypeSwitch;
    private ButtonWidget buttonCommandsAllowed;
    private ButtonWidget buttonCustomizeType;
    private String w;
    private String x;
    private String seed;
    private String levelName;
    private int generatorType;
    public CompoundTag generatorOptionsTag;
    
    public NewLevelScreen(final Screen screen) {
        super(new TranslatableTextComponent("selectWorld.create", new Object[0]));
        this.gameMode = "survival";
        this.structures = true;
        this.generatorOptionsTag = new CompoundTag();
        this.parent = screen;
        this.seed = "";
        this.levelName = I18n.translate("selectWorld.newWorld");
    }
    
    @Override
    public void tick() {
        this.textFieldLevelName.tick();
        this.textFieldSeed.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        (this.textFieldLevelName = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterName"))).setText(this.levelName);
        this.textFieldLevelName.setChangedListener(string -> {
            this.levelName = string;
            this.buttonCreateLevel.active = !this.textFieldLevelName.getText().isEmpty();
            this.a();
            return;
        });
        this.children.add(this.textFieldLevelName);
        this.buttonGameModeSwitch = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
            if ("survival".equals(this.gameMode)) {
                if (!this.j) {
                    this.commandsAllowed = false;
                }
                this.l = false;
                this.gameMode = "hardcore";
                this.l = true;
                this.buttonCommandsAllowed.active = false;
                this.buttonGenerateBonusItems.active = false;
                this.b();
            }
            else if ("hardcore".equals(this.gameMode)) {
                if (!this.j) {
                    this.commandsAllowed = true;
                }
                this.l = false;
                this.gameMode = "creative";
                this.b();
                this.l = false;
                this.buttonCommandsAllowed.active = true;
                this.buttonGenerateBonusItems.active = true;
            }
            else {
                if (!this.j) {
                    this.commandsAllowed = false;
                }
                this.gameMode = "survival";
                this.b();
                this.buttonCommandsAllowed.active = true;
                this.buttonGenerateBonusItems.active = true;
                this.l = false;
            }
            this.b();
            return;
        }));
        (this.textFieldSeed = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterSeed"))).setText(this.seed);
        this.textFieldSeed.setChangedListener(string -> this.seed = this.textFieldSeed.getText());
        this.children.add(this.textFieldSeed);
        this.buttonGenerateStructures = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures"), buttonWidget -> {
            this.structures = !this.structures;
            this.b();
            return;
        }));
        this.buttonGenerateStructures.visible = false;
        this.buttonMapTypeSwitch = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType"), buttonWidget -> {
            ++this.generatorType;
            if (this.generatorType >= LevelGeneratorType.TYPES.length) {
                this.generatorType = 0;
            }
            while (!this.d()) {
                ++this.generatorType;
                if (this.generatorType >= LevelGeneratorType.TYPES.length) {
                    this.generatorType = 0;
                }
            }
            this.generatorOptionsTag = new CompoundTag();
            this.b();
            this.a(this.n);
            return;
        }));
        this.buttonMapTypeSwitch.visible = false;
        this.buttonCustomizeType = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType"), buttonWidget -> {
            if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT) {
                this.minecraft.openScreen(new CustomizeFlatLevelScreen(this, this.generatorOptionsTag));
            }
            if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.BUFFET) {
                this.minecraft.openScreen(new CustomizeBuffetLevelScreen(this, this.generatorOptionsTag));
            }
            return;
        }));
        this.buttonCustomizeType.visible = false;
        this.buttonCommandsAllowed = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
            this.j = true;
            this.commandsAllowed = !this.commandsAllowed;
            this.b();
            return;
        }));
        this.buttonCommandsAllowed.visible = false;
        this.buttonGenerateBonusItems = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems"), buttonWidget -> {
            this.enableBonusItems = !this.enableBonusItems;
            this.b();
            return;
        }));
        this.buttonGenerateBonusItems.visible = false;
        this.buttonMoreOptions = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions"), buttonWidget -> this.e()));
        this.buttonCreateLevel = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create"), buttonWidget -> this.createLevel()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.a(this.n);
        this.setInitialFocus(this.textFieldLevelName);
        this.a();
        this.b();
    }
    
    private void a() {
        this.e = this.textFieldLevelName.getText().trim();
        if (this.e.length() == 0) {
            this.e = "World";
        }
        try {
            this.e = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.e, "");
        }
        catch (Exception exception3) {
            this.e = "World";
            try {
                this.e = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.e, "");
            }
            catch (Exception exception2) {
                throw new RuntimeException("Could not create save folder", exception2);
            }
        }
    }
    
    private void b() {
        this.buttonGameModeSwitch.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
        this.w = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line1");
        this.x = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line2");
        this.buttonGenerateStructures.setMessage(I18n.translate("selectWorld.mapFeatures") + ' ' + I18n.translate(this.structures ? "options.on" : "options.off"));
        this.buttonGenerateBonusItems.setMessage(I18n.translate("selectWorld.bonusItems") + ' ' + I18n.translate((this.enableBonusItems && !this.l) ? "options.on" : "options.off"));
        this.buttonMapTypeSwitch.setMessage(I18n.translate("selectWorld.mapType") + ' ' + I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey()));
        this.buttonCommandsAllowed.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate((this.commandsAllowed && !this.l) ? "options.on" : "options.off"));
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    private void createLevel() {
        this.minecraft.openScreen(null);
        if (this.isCreatingLevel) {
            return;
        }
        this.isCreatingLevel = true;
        long long1 = new Random().nextLong();
        final String string3 = this.textFieldSeed.getText();
        if (!StringUtils.isEmpty((CharSequence)string3)) {
            try {
                final long long2 = Long.parseLong(string3);
                if (long2 != 0L) {
                    long1 = long2;
                }
            }
            catch (NumberFormatException numberFormatException4) {
                long1 = string3.hashCode();
            }
        }
        final LevelInfo levelInfo4 = new LevelInfo(long1, GameMode.byName(this.gameMode), this.structures, this.l, LevelGeneratorType.TYPES[this.generatorType]);
        levelInfo4.setGeneratorOptions((JsonElement)Dynamic.convert((DynamicOps)NbtOps.INSTANCE, (DynamicOps)JsonOps.INSTANCE, this.generatorOptionsTag));
        if (this.enableBonusItems && !this.l) {
            levelInfo4.setBonusChest();
        }
        if (this.commandsAllowed && !this.l) {
            levelInfo4.enableCommands();
        }
        this.minecraft.startIntegratedServer(this.e, this.textFieldLevelName.getText().trim(), levelInfo4);
    }
    
    private boolean d() {
        final LevelGeneratorType levelGeneratorType1 = LevelGeneratorType.TYPES[this.generatorType];
        return levelGeneratorType1 != null && levelGeneratorType1.isVisible() && (levelGeneratorType1 != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES || Screen.hasShiftDown());
    }
    
    private void e() {
        this.a(!this.n);
    }
    
    private void a(final boolean boolean1) {
        this.n = boolean1;
        if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            this.buttonGameModeSwitch.visible = !this.n;
            this.buttonGameModeSwitch.active = false;
            if (this.g == null) {
                this.g = this.gameMode;
            }
            this.gameMode = "spectator";
            this.buttonGenerateStructures.visible = false;
            this.buttonGenerateBonusItems.visible = false;
            this.buttonMapTypeSwitch.visible = this.n;
            this.buttonCommandsAllowed.visible = false;
            this.buttonCustomizeType.visible = false;
        }
        else {
            this.buttonGameModeSwitch.visible = !this.n;
            this.buttonGameModeSwitch.active = true;
            if (this.g != null) {
                this.gameMode = this.g;
                this.g = null;
            }
            this.buttonGenerateStructures.visible = (this.n && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED);
            this.buttonGenerateBonusItems.visible = this.n;
            this.buttonMapTypeSwitch.visible = this.n;
            this.buttonCommandsAllowed.visible = this.n;
            this.buttonCustomizeType.visible = (this.n && LevelGeneratorType.TYPES[this.generatorType].isCustomizable());
        }
        this.b();
        this.textFieldSeed.setVisible(this.n);
        this.textFieldLevelName.setVisible(!this.n);
        if (this.n) {
            this.buttonMoreOptions.setMessage(I18n.translate("gui.done"));
        }
        else {
            this.buttonMoreOptions.setMessage(I18n.translate("selectWorld.moreWorldOptions"));
        }
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.createLevel();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, -1);
        if (this.n) {
            this.drawString(this.font, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.font, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
            if (this.buttonGenerateStructures.visible) {
                this.drawString(this.font, I18n.translate("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
            }
            if (this.buttonCommandsAllowed.visible) {
                this.drawString(this.font, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
            }
            this.textFieldSeed.render(mouseX, mouseY, delta);
            if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
                this.font.drawStringBounded(I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey()), this.buttonMapTypeSwitch.x + 2, this.buttonMapTypeSwitch.y + 22, this.buttonMapTypeSwitch.getWidth(), 10526880);
            }
        }
        else {
            this.drawString(this.font, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.font, I18n.translate("selectWorld.resultFolder") + " " + this.e, this.width / 2 - 100, 85, -6250336);
            this.textFieldLevelName.render(mouseX, mouseY, delta);
            this.drawCenteredString(this.font, this.w, this.width / 2, 137, -6250336);
            this.drawCenteredString(this.font, this.x, this.width / 2, 149, -6250336);
        }
        super.render(mouseX, mouseY, delta);
    }
    
    public void recreateLevel(final LevelProperties levelProperties) {
        this.levelName = levelProperties.getLevelName();
        this.seed = levelProperties.getSeed() + "";
        final LevelGeneratorType levelGeneratorType2 = (levelProperties.getGeneratorType() == LevelGeneratorType.CUSTOMIZED) ? LevelGeneratorType.DEFAULT : levelProperties.getGeneratorType();
        this.generatorType = levelGeneratorType2.getId();
        this.generatorOptionsTag = levelProperties.getGeneratorOptions();
        this.structures = levelProperties.hasStructures();
        this.commandsAllowed = levelProperties.areCommandsAllowed();
        if (levelProperties.isHardcore()) {
            this.gameMode = "hardcore";
        }
        else if (levelProperties.getGameMode().isSurvivalLike()) {
            this.gameMode = "survival";
        }
        else if (levelProperties.getGameMode().isCreative()) {
            this.gameMode = "creative";
        }
    }
}
