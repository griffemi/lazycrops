package xyz.funky493.lazycrops.machines.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.machines.EssenceExtractorBlockEntity;

public class EssenceExtractorScreen extends HandledScreen<EssenceExtractorScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("lazycrops", "textures/gui/essence_extractor.png");

    private static final int ENERGY_X = 9;
    private static final int ENERGY_Y = 18;
    private static final int ARROW_X = 144;
    private static final int ARROW_Y = 46;
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 16;

    public EssenceExtractorScreen(EssenceExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Wider than the usual 176 because a 6-wide input grid and a 3-wide output grid do not
        // both fit. HandledScreen centres on these fields, so a custom size is fine.
        this.backgroundWidth = 230;
        this.backgroundHeight = 222;
        this.playerInventoryTitleX = 34;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        MachineScreenDrawing.drawEnergyBar(context, TEXTURE, this.x + ENERGY_X, this.y + ENERGY_Y,
                230, 0, this.handler.getEnergy(), EssenceExtractorBlockEntity.CAPACITY);

        int progress = this.handler.getProgress() * ARROW_WIDTH / this.handler.getMaxProgress();
        if (progress > 0) {
            context.drawTexture(TEXTURE, this.x + ARROW_X, this.y + ARROW_Y, 230, 80, progress, ARROW_HEIGHT);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        if (MachineScreenDrawing.isOver(mouseX, mouseY, this.x + ENERGY_X, this.y + ENERGY_Y,
                MachineScreenDrawing.ENERGY_WIDTH, MachineScreenDrawing.ENERGY_HEIGHT)) {
            context.drawTooltip(this.textRenderer,
                    java.util.List.of(
                            MachineScreenDrawing.energyTooltip(this.handler.getEnergy(), EssenceExtractorBlockEntity.CAPACITY),
                            MachineScreenDrawing.statusText(this.handler.getStatus())),
                    mouseX, mouseY);
        }

        if (MachineScreenDrawing.isOver(mouseX, mouseY, this.x + ARROW_X, this.y + ARROW_Y, ARROW_WIDTH, ARROW_HEIGHT)) {
            context.drawTooltip(this.textRenderer,
                    java.util.List.of(Text.translatable("gui.lazycrops.extract_chance")),
                    mouseX, mouseY);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
