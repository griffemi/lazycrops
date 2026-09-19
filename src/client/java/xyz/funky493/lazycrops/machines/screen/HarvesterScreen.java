package xyz.funky493.lazycrops.machines.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.machines.HarvesterBlockEntity;

public class HarvesterScreen extends HandledScreen<HarvesterScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("resourcecrops", "textures/gui/harvester.png");

    private static final int ENERGY_X = 10;
    private static final int ENERGY_Y = 18;
    private static final int BUTTON_X = 144;
    private static final int BUTTON_Y = 44;

    private ButtonWidget radiusButton;

    public HarvesterScreen(HarvesterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 204;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.radiusButton = ButtonWidget.builder(radiusLabel(), button -> cycleRadius())
                .dimensions(this.x + BUTTON_X, this.y + BUTTON_Y, 24, 20)
                .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.translatable("gui.resourcecrops.radius.tooltip")))
                .build();
        this.addDrawableChild(this.radiusButton);
    }

    private Text radiusLabel() {
        int side = this.handler.getRadius() * 2 + 1;
        return Text.translatable("gui.resourcecrops.radius", side, side);
    }

    /**
     * Cycles 3x3 -> 5x5 -> 7x7 -> 9x9 -> 3x3.
     * <p>
     * clickButton runs the handler locally as well as sending the packet; the client copy is
     * built with an empty context so only the server actually applies the change.
     */
    private void cycleRadius() {
        if (this.client == null || this.client.interactionManager == null) {
            return;
        }
        this.client.interactionManager.clickButton(this.handler.syncId, this.handler.getRadius() % 4);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        MachineScreenDrawing.drawEnergyBar(context, TEXTURE, this.x + ENERGY_X, this.y + ENERGY_Y,
                176, 0, this.handler.getEnergy(), HarvesterBlockEntity.CAPACITY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        // The authoritative radius arrives via the property sync, so relabel every frame.
        if (this.radiusButton != null) {
            this.radiusButton.setMessage(radiusLabel());
        }

        if (MachineScreenDrawing.isOver(mouseX, mouseY, this.x + ENERGY_X, this.y + ENERGY_Y,
                MachineScreenDrawing.ENERGY_WIDTH, MachineScreenDrawing.ENERGY_HEIGHT)) {
            context.drawTooltip(this.textRenderer,
                    java.util.List.of(
                            MachineScreenDrawing.energyTooltip(this.handler.getEnergy(), HarvesterBlockEntity.CAPACITY),
                            MachineScreenDrawing.statusText(this.handler.getStatus())),
                    mouseX, mouseY);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
