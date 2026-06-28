package fun.rich.features.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ModuleCategory {
    GUI("GUI"),
    SOUNDS("Sounds"),
    VISUALS_RICH("Visuals (Rich)"),
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    MISC("Misc"),
    CONFIGS("Configs"),
    AUTOBUY("AutoBuy");

    final String readableName;
}
