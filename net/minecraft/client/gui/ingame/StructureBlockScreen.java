package net.minecraft.client.gui.ingame;

import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.Element;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.block.Blocks;
import java.text.DecimalFormat;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BlockMirror;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class StructureBlockScreen extends Screen
{
    private final StructureBlockBlockEntity structureBlock;
    private BlockMirror mirror;
    private BlockRotation rotation;
    private StructureBlockMode mode;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private TextFieldWidget inputName;
    private TextFieldWidget inputPosX;
    private TextFieldWidget inputPosY;
    private TextFieldWidget inputPosZ;
    private TextFieldWidget inputSizeX;
    private TextFieldWidget inputSizeY;
    private TextFieldWidget inputSizeZ;
    private TextFieldWidget inputIntegrity;
    private TextFieldWidget inputSeed;
    private TextFieldWidget inputMetadata;
    private ButtonWidget buttonDone;
    private ButtonWidget buttonCancel;
    private ButtonWidget buttonSave;
    private ButtonWidget buttonLoad;
    private ButtonWidget buttonRotate0;
    private ButtonWidget buttonRotate90;
    private ButtonWidget buttonRotate180;
    private ButtonWidget buttonRotate270;
    private ButtonWidget buttonMode;
    private ButtonWidget buttonDetect;
    private ButtonWidget buttonEntities;
    private ButtonWidget buttonMirror;
    private ButtonWidget buttonShowAir;
    private ButtonWidget buttonShowBoundingBox;
    private final DecimalFormat decimalFormat;
    
    public StructureBlockScreen(final StructureBlockBlockEntity structureBlock) {
        super(new TranslatableTextComponent(Blocks.lX.getTranslationKey(), new Object[0]));
        this.mirror = BlockMirror.NONE;
        this.rotation = BlockRotation.ROT_0;
        this.mode = StructureBlockMode.d;
        this.decimalFormat = new DecimalFormat("0.0###");
        this.structureBlock = structureBlock;
        this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    }
    
    @Override
    public void tick() {
        this.inputName.tick();
        this.inputPosX.tick();
        this.inputPosY.tick();
        this.inputPosZ.tick();
        this.inputSizeX.tick();
        this.inputSizeY.tick();
        this.inputSizeZ.tick();
        this.inputIntegrity.tick();
        this.inputSeed.tick();
        this.inputMetadata.tick();
    }
    
    private void done() {
        if (this.a(StructureBlockBlockEntity.Action.a)) {
            this.minecraft.openScreen(null);
        }
    }
    
    private void cancel() {
        this.structureBlock.setMirror(this.mirror);
        this.structureBlock.setRotation(this.rotation);
        this.structureBlock.setMode(this.mode);
        this.structureBlock.setIgnoreEntities(this.ignoreEntities);
        this.structureBlock.setShowAir(this.showAir);
        this.structureBlock.setShowBoundingBox(this.showBoundingBox);
        this.minecraft.openScreen(null);
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.buttonDone = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.done()));
        this.buttonCancel = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.cancel()));
        this.buttonSave = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.save"), buttonWidget -> {
            if (this.structureBlock.getMode() == StructureBlockMode.a) {
                this.a(StructureBlockBlockEntity.Action.b);
                this.minecraft.openScreen(null);
            }
            return;
        }));
        this.buttonLoad = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.load"), buttonWidget -> {
            if (this.structureBlock.getMode() == StructureBlockMode.b) {
                this.a(StructureBlockBlockEntity.Action.c);
                this.minecraft.openScreen(null);
            }
            return;
        }));
        this.buttonMode = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", buttonWidget -> {
            this.structureBlock.cycleMode();
            this.updateMode();
            return;
        }));
        this.buttonDetect = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 100, 120, 50, 20, I18n.translate("structure_block.button.detect_size"), buttonWidget -> {
            if (this.structureBlock.getMode() == StructureBlockMode.a) {
                this.a(StructureBlockBlockEntity.Action.d);
                this.minecraft.openScreen(null);
            }
            return;
        }));
        this.buttonEntities = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", buttonWidget -> {
            this.structureBlock.setIgnoreEntities(!this.structureBlock.shouldIgnoreEntities());
            this.updateIgnoreEntitiesButton();
            return;
        }));
        this.buttonMirror = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 20, 185, 40, 20, "MIRROR", buttonWidget -> {
            switch (this.structureBlock.getMirror()) {
                case NONE: {
                    this.structureBlock.setMirror(BlockMirror.LEFT_RIGHT);
                    break;
                }
                case LEFT_RIGHT: {
                    this.structureBlock.setMirror(BlockMirror.FRONT_BACK);
                    break;
                }
                case FRONT_BACK: {
                    this.structureBlock.setMirror(BlockMirror.NONE);
                    break;
                }
            }
            this.updateMirrorButton();
            return;
        }));
        this.buttonShowAir = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", buttonWidget -> {
            this.structureBlock.setShowAir(!this.structureBlock.shouldShowAir());
            this.updateShowAirButton();
            return;
        }));
        this.buttonShowBoundingBox = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", buttonWidget -> {
            this.structureBlock.setShowBoundingBox(!this.structureBlock.shouldShowBoundingBox());
            this.updateShowBoundingBoxButton();
            return;
        }));
        this.buttonRotate0 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.ROT_0);
            this.updateRotationButton();
            return;
        }));
        this.buttonRotate90 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.ROT_90);
            this.updateRotationButton();
            return;
        }));
        this.buttonRotate180 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 1 + 20, 185, 40, 20, "180", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.ROT_180);
            this.updateRotationButton();
            return;
        }));
        this.buttonRotate270 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.ROT_270);
            this.updateRotationButton();
            return;
        }));
        (this.inputName = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.translate("structure_block.structure_name")) {
            @Override
            public boolean charTyped(final char chr, final int keyCode) {
                return Screen.this.isValidCharacterForName(this.getText(), chr, this.getCursor()) && super.charTyped(chr, keyCode);
            }
        }).setMaxLength(64);
        this.inputName.setText(this.structureBlock.getStructureName());
        this.children.add(this.inputName);
        final BlockPos blockPos1 = this.structureBlock.getOffset();
        (this.inputPosX = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 80, 20, I18n.translate("structure_block.position.x"))).setMaxLength(15);
        this.inputPosX.setText(Integer.toString(blockPos1.getX()));
        this.children.add(this.inputPosX);
        (this.inputPosY = new TextFieldWidget(this.font, this.width / 2 - 72, 80, 80, 20, I18n.translate("structure_block.position.y"))).setMaxLength(15);
        this.inputPosY.setText(Integer.toString(blockPos1.getY()));
        this.children.add(this.inputPosY);
        (this.inputPosZ = new TextFieldWidget(this.font, this.width / 2 + 8, 80, 80, 20, I18n.translate("structure_block.position.z"))).setMaxLength(15);
        this.inputPosZ.setText(Integer.toString(blockPos1.getZ()));
        this.children.add(this.inputPosZ);
        final BlockPos blockPos2 = this.structureBlock.getSize();
        (this.inputSizeX = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.translate("structure_block.size.x"))).setMaxLength(15);
        this.inputSizeX.setText(Integer.toString(blockPos2.getX()));
        this.children.add(this.inputSizeX);
        (this.inputSizeY = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.translate("structure_block.size.y"))).setMaxLength(15);
        this.inputSizeY.setText(Integer.toString(blockPos2.getY()));
        this.children.add(this.inputSizeY);
        (this.inputSizeZ = new TextFieldWidget(this.font, this.width / 2 + 8, 120, 80, 20, I18n.translate("structure_block.size.z"))).setMaxLength(15);
        this.inputSizeZ.setText(Integer.toString(blockPos2.getZ()));
        this.children.add(this.inputSizeZ);
        (this.inputIntegrity = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.translate("structure_block.integrity.integrity"))).setMaxLength(15);
        this.inputIntegrity.setText(this.decimalFormat.format(this.structureBlock.getIntegrity()));
        this.children.add(this.inputIntegrity);
        (this.inputSeed = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.translate("structure_block.integrity.seed"))).setMaxLength(31);
        this.inputSeed.setText(Long.toString(this.structureBlock.getSeed()));
        this.children.add(this.inputSeed);
        (this.inputMetadata = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 240, 20, I18n.translate("structure_block.custom_data"))).setMaxLength(128);
        this.inputMetadata.setText(this.structureBlock.getMetadata());
        this.children.add(this.inputMetadata);
        this.mirror = this.structureBlock.getMirror();
        this.updateMirrorButton();
        this.rotation = this.structureBlock.getRotation();
        this.updateRotationButton();
        this.mode = this.structureBlock.getMode();
        this.updateMode();
        this.ignoreEntities = this.structureBlock.shouldIgnoreEntities();
        this.updateIgnoreEntitiesButton();
        this.showAir = this.structureBlock.shouldShowAir();
        this.updateShowAirButton();
        this.showBoundingBox = this.structureBlock.shouldShowBoundingBox();
        this.updateShowBoundingBoxButton();
        this.setInitialFocus(this.inputName);
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.inputName.getText();
        final String string5 = this.inputPosX.getText();
        final String string6 = this.inputPosY.getText();
        final String string7 = this.inputPosZ.getText();
        final String string8 = this.inputSizeX.getText();
        final String string9 = this.inputSizeY.getText();
        final String string10 = this.inputSizeZ.getText();
        final String string11 = this.inputIntegrity.getText();
        final String string12 = this.inputSeed.getText();
        final String string13 = this.inputMetadata.getText();
        this.init(client, width, height);
        this.inputName.setText(string4);
        this.inputPosX.setText(string5);
        this.inputPosY.setText(string6);
        this.inputPosZ.setText(string7);
        this.inputSizeX.setText(string8);
        this.inputSizeY.setText(string9);
        this.inputSizeZ.setText(string10);
        this.inputIntegrity.setText(string11);
        this.inputSeed.setText(string12);
        this.inputMetadata.setText(string13);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    private void updateIgnoreEntitiesButton() {
        final boolean boolean1 = !this.structureBlock.shouldIgnoreEntities();
        if (boolean1) {
            this.buttonEntities.setMessage(I18n.translate("options.on"));
        }
        else {
            this.buttonEntities.setMessage(I18n.translate("options.off"));
        }
    }
    
    private void updateShowAirButton() {
        final boolean boolean1 = this.structureBlock.shouldShowAir();
        if (boolean1) {
            this.buttonShowAir.setMessage(I18n.translate("options.on"));
        }
        else {
            this.buttonShowAir.setMessage(I18n.translate("options.off"));
        }
    }
    
    private void updateShowBoundingBoxButton() {
        final boolean boolean1 = this.structureBlock.shouldShowBoundingBox();
        if (boolean1) {
            this.buttonShowBoundingBox.setMessage(I18n.translate("options.on"));
        }
        else {
            this.buttonShowBoundingBox.setMessage(I18n.translate("options.off"));
        }
    }
    
    private void updateMirrorButton() {
        final BlockMirror blockMirror1 = this.structureBlock.getMirror();
        switch (blockMirror1) {
            case NONE: {
                this.buttonMirror.setMessage("|");
                break;
            }
            case LEFT_RIGHT: {
                this.buttonMirror.setMessage("< >");
                break;
            }
            case FRONT_BACK: {
                this.buttonMirror.setMessage("^ v");
                break;
            }
        }
    }
    
    private void updateRotationButton() {
        this.buttonRotate0.active = true;
        this.buttonRotate90.active = true;
        this.buttonRotate180.active = true;
        this.buttonRotate270.active = true;
        switch (this.structureBlock.getRotation()) {
            case ROT_0: {
                this.buttonRotate0.active = false;
                break;
            }
            case ROT_180: {
                this.buttonRotate180.active = false;
                break;
            }
            case ROT_270: {
                this.buttonRotate270.active = false;
                break;
            }
            case ROT_90: {
                this.buttonRotate90.active = false;
                break;
            }
        }
    }
    
    private void updateMode() {
        this.inputName.setVisible(false);
        this.inputPosX.setVisible(false);
        this.inputPosY.setVisible(false);
        this.inputPosZ.setVisible(false);
        this.inputSizeX.setVisible(false);
        this.inputSizeY.setVisible(false);
        this.inputSizeZ.setVisible(false);
        this.inputIntegrity.setVisible(false);
        this.inputSeed.setVisible(false);
        this.inputMetadata.setVisible(false);
        this.buttonSave.visible = false;
        this.buttonLoad.visible = false;
        this.buttonDetect.visible = false;
        this.buttonEntities.visible = false;
        this.buttonMirror.visible = false;
        this.buttonRotate0.visible = false;
        this.buttonRotate90.visible = false;
        this.buttonRotate180.visible = false;
        this.buttonRotate270.visible = false;
        this.buttonShowAir.visible = false;
        this.buttonShowBoundingBox.visible = false;
        switch (this.structureBlock.getMode()) {
            case a: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputSizeX.setVisible(true);
                this.inputSizeY.setVisible(true);
                this.inputSizeZ.setVisible(true);
                this.buttonSave.visible = true;
                this.buttonDetect.visible = true;
                this.buttonEntities.visible = true;
                this.buttonShowAir.visible = true;
                break;
            }
            case b: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputIntegrity.setVisible(true);
                this.inputSeed.setVisible(true);
                this.buttonLoad.visible = true;
                this.buttonEntities.visible = true;
                this.buttonMirror.visible = true;
                this.buttonRotate0.visible = true;
                this.buttonRotate90.visible = true;
                this.buttonRotate180.visible = true;
                this.buttonRotate270.visible = true;
                this.buttonShowBoundingBox.visible = true;
                this.updateRotationButton();
                break;
            }
            case c: {
                this.inputName.setVisible(true);
                break;
            }
            case d: {
                this.inputMetadata.setVisible(true);
                break;
            }
        }
        this.buttonMode.setMessage(I18n.translate("structure_block.mode." + this.structureBlock.getMode().asString()));
    }
    
    private boolean a(final StructureBlockBlockEntity.Action action) {
        final BlockPos blockPos2 = new BlockPos(this.parseInt(this.inputPosX.getText()), this.parseInt(this.inputPosY.getText()), this.parseInt(this.inputPosZ.getText()));
        final BlockPos blockPos3 = new BlockPos(this.parseInt(this.inputSizeX.getText()), this.parseInt(this.inputSizeY.getText()), this.parseInt(this.inputSizeZ.getText()));
        final float float4 = this.parseFloat(this.inputIntegrity.getText());
        final long long5 = this.parseLong(this.inputSeed.getText());
        this.minecraft.getNetworkHandler().sendPacket(new UpdateStructureBlockC2SPacket(this.structureBlock.getPos(), action, this.structureBlock.getMode(), this.inputName.getText(), blockPos2, blockPos3, this.structureBlock.getMirror(), this.structureBlock.getRotation(), this.inputMetadata.getText(), this.structureBlock.shouldIgnoreEntities(), this.structureBlock.shouldShowAir(), this.structureBlock.shouldShowBoundingBox(), float4, long5));
        return true;
    }
    
    private long parseLong(final String string) {
        try {
            return Long.valueOf(string);
        }
        catch (NumberFormatException numberFormatException2) {
            return 0L;
        }
    }
    
    private float parseFloat(final String string) {
        try {
            return Float.valueOf(string);
        }
        catch (NumberFormatException numberFormatException2) {
            return 1.0f;
        }
    }
    
    private int parseInt(final String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException2) {
            return 0;
        }
    }
    
    @Override
    public void onClose() {
        this.cancel();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.done();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        final StructureBlockMode structureBlockMode4 = this.structureBlock.getMode();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
        if (structureBlockMode4 != StructureBlockMode.d) {
            this.drawString(this.font, I18n.translate("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
            this.inputName.render(mouseX, mouseY, delta);
        }
        if (structureBlockMode4 == StructureBlockMode.b || structureBlockMode4 == StructureBlockMode.a) {
            this.drawString(this.font, I18n.translate("structure_block.position"), this.width / 2 - 153, 70, 10526880);
            this.inputPosX.render(mouseX, mouseY, delta);
            this.inputPosY.render(mouseX, mouseY, delta);
            this.inputPosZ.render(mouseX, mouseY, delta);
            final String string5 = I18n.translate("structure_block.include_entities");
            final int integer6 = this.font.getStringWidth(string5);
            this.drawString(this.font, string5, this.width / 2 + 154 - integer6, 150, 10526880);
        }
        if (structureBlockMode4 == StructureBlockMode.a) {
            this.drawString(this.font, I18n.translate("structure_block.size"), this.width / 2 - 153, 110, 10526880);
            this.inputSizeX.render(mouseX, mouseY, delta);
            this.inputSizeY.render(mouseX, mouseY, delta);
            this.inputSizeZ.render(mouseX, mouseY, delta);
            final String string5 = I18n.translate("structure_block.detect_size");
            final int integer6 = this.font.getStringWidth(string5);
            this.drawString(this.font, string5, this.width / 2 + 154 - integer6, 110, 10526880);
            final String string6 = I18n.translate("structure_block.show_air");
            final int integer7 = this.font.getStringWidth(string6);
            this.drawString(this.font, string6, this.width / 2 + 154 - integer7, 70, 10526880);
        }
        if (structureBlockMode4 == StructureBlockMode.b) {
            this.drawString(this.font, I18n.translate("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
            this.inputIntegrity.render(mouseX, mouseY, delta);
            this.inputSeed.render(mouseX, mouseY, delta);
            final String string5 = I18n.translate("structure_block.show_boundingbox");
            final int integer6 = this.font.getStringWidth(string5);
            this.drawString(this.font, string5, this.width / 2 + 154 - integer6, 70, 10526880);
        }
        if (structureBlockMode4 == StructureBlockMode.d) {
            this.drawString(this.font, I18n.translate("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
            this.inputMetadata.render(mouseX, mouseY, delta);
        }
        final String string5 = "structure_block.mode_info." + structureBlockMode4.asString();
        this.drawString(this.font, I18n.translate(string5), this.width / 2 - 153, 174, 10526880);
        super.render(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
