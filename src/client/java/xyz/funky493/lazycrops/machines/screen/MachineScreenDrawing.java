package xyz.funky493.lazycrops.machines.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Drawing helpers shared by the two machine screens. */
public final class MachineScreenDrawing {

    public static final int ENERGY_WIDTH = 14;
    public static final int ENERGY_HEIGHT = 72;

    private MachineScreenDrawing() {
    }

    /**
     * Draws the energy gauge, filling bottom-up.
     * <p>
     * Both the destination y and the source v shift down by the unfilled amount, so the sprite
     * is cropped from the top rather than squashed.
     */
    public static void drawEnergyBar(DrawContext context, Identifier texture, int x, int y,
                                     int u, int v, long energy, long capacity) {
        if (capacity <= 0) {
            return;
        }
        long clamped = Math.max(0L, Math.min(capacity, energy));
        int filled = (int) (ENERGY_HEIGHT * clamped / capacity);
        if (filled <= 0) {
            return;
        }
        int offset = ENERGY_HEIGHT - filled;
        context.drawTexture(texture, x, y + offset, u, v + offset, ENERGY_WIDTH, filled);
    }

    public static boolean isOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static Text energyTooltip(long energy, long capacity) {
        return Text.translatable("gui.lazycrops.energy", energy, capacity);
    }

    /** Status ordinals are shared by both machines; 0 (idle) renders no tooltip line. */
    public static Text statusText(int status) {
        return switch (status) {
            case 1 -> Text.translatable("gui.lazycrops.status.working");
            case 2 -> Text.translatable("gui.lazycrops.status.no_power");
            case 3 -> Text.translatable("gui.lazycrops.status.full");
            default -> Text.translatable("gui.lazycrops.status.idle");
        };
    }
}
