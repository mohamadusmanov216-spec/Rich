package fun.rich.features.module;

import fun.rich.features.impl.player.FreeLook;
import fun.rich.features.impl.render.*;

import java.util.List;
import java.util.Set;

public final class ShowcaseCatalog {
    private static final List<ModuleCategory> VISIBLE_CATEGORIES = List.of(
            ModuleCategory.GUI,
            ModuleCategory.SOUNDS,
            ModuleCategory.VISUALS_RICH,
            ModuleCategory.COMBAT,
            ModuleCategory.MOVEMENT,
            ModuleCategory.RENDER,
            ModuleCategory.PLAYER,
            ModuleCategory.MISC,
            ModuleCategory.CONFIGS,
            ModuleCategory.AUTOBUY
    );

    private static final Set<Class<? extends Module>> GUI_MODULES = Set.of(
            Hud.class,
            BetterMinecraft.class,
            CrossHair.class,
            CameraSettings.class
    );

    private static final Set<Class<? extends Module>> RICH_VISUAL_MODULES = Set.of(
            AspectRatio.class,
            BlockOverlay.class,
            FreeLook.class,
            JumpCircle.class,
            KillEffect.class,
            NoRender.class,
            SwingAnimation.class,
            Trails.class,
            ViewModel.class,
            WorldParticles.class,
            WorldTweaks.class
    );

    private ShowcaseCatalog() {
    }

    public static List<ModuleCategory> visibleCategories() {
        return VISIBLE_CATEGORIES;
    }

    public static boolean isSearchable(ModuleCategory category) {
        return category != ModuleCategory.CONFIGS && category != ModuleCategory.AUTOBUY;
    }

    public static boolean matches(ModuleCategory category, Module module) {
        return switch (category) {
            case GUI -> GUI_MODULES.contains(module.getClass());
            case SOUNDS -> module.getCategory() == ModuleCategory.SOUNDS;
            case VISUALS_RICH -> RICH_VISUAL_MODULES.contains(module.getClass());
            default -> module.getCategory() == category;
        };
    }
}
