package xyz.funky493.lazycrops.machines;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType;
import xyz.funky493.lazycrops.machines.screen.EssenceExtractorScreenHandler;
import xyz.funky493.lazycrops.machines.screen.HarvesterScreenHandler;

/**
 * Holder for the machine screen handler types. This stays on the common side -- only the
 * {@code HandledScreens.register} calls are client-only.
 * <p>
 * Extended types are used so the block position travels to the client with the open packet.
 */
public class LazyScreenHandlers {

    public static final ScreenHandlerType<HarvesterScreenHandler> HARVESTER =
            new ExtendedScreenHandlerType<>(HarvesterScreenHandler::new);

    public static final ScreenHandlerType<EssenceExtractorScreenHandler> ESSENCE_EXTRACTOR =
            new ExtendedScreenHandlerType<>(EssenceExtractorScreenHandler::new);
}
