package fun.rich.features.impl.sound;

import fun.rich.features.module.Module;
import fun.rich.features.module.ModuleCategory;
import fun.rich.features.module.setting.implement.ButtonSetting;
import fun.rich.features.module.setting.implement.SelectSetting;
import fun.rich.features.module.setting.implement.SliderSettings;
import fun.rich.utils.client.Instance;
import fun.rich.utils.client.sound.SoundManager;

public class SoundBoard extends Module {
    private final SliderSettings uiVolume = new SliderSettings("UI Volume", "Volume for menu and category sounds")
            .range(0, 100)
            .setValue(100);

    private final SelectSetting openGuiSound = new SelectSetting("Open GUI", "Sound played when opening the GUI")
            .value(SoundManager.UI_SOUND_PRESETS)
            .selected("Rich Open");
    private final ButtonSetting previewOpenGui = new ButtonSetting("Preview Open", "Preview the GUI open sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(openGuiSound.getSelected()), uiVolume.getValue() / 100F, 1F));

    private final SelectSetting closeGuiSound = new SelectSetting("Close GUI", "Sound played when closing the GUI")
            .value(SoundManager.UI_SOUND_PRESETS)
            .selected("Rich Close");
    private final ButtonSetting previewCloseGui = new ButtonSetting("Preview Close", "Preview the GUI close sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(closeGuiSound.getSelected()), uiVolume.getValue() / 100F, 1F));

    private final SelectSetting enableModuleSound = new SelectSetting("Enable Module", "Sound played when enabling modules")
            .value(SoundManager.MODULE_SOUND_PRESETS)
            .selected("Rich Enable");
    private final ButtonSetting previewEnableModule = new ButtonSetting("Preview Enable", "Preview the enable-module sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(enableModuleSound.getSelected()), uiVolume.getValue() / 100F, 1F));

    private final SelectSetting disableModuleSound = new SelectSetting("Disable Module", "Sound played when disabling modules")
            .value(SoundManager.MODULE_SOUND_PRESETS)
            .selected("Rich Disable");
    private final ButtonSetting previewDisableModule = new ButtonSetting("Preview Disable", "Preview the disable-module sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(disableModuleSound.getSelected()), uiVolume.getValue() / 100F, 1F));

    private final SelectSetting categorySound = new SelectSetting("Category Click", "Sound played when switching categories")
            .value(SoundManager.UI_SOUND_PRESETS)
            .selected("Category");
    private final ButtonSetting previewCategory = new ButtonSetting("Preview Category", "Preview the category switch sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(categorySound.getSelected()), uiVolume.getValue() / 100F, 1F));

    private final SelectSetting accountSwitchSound = new SelectSetting("Account Switch", "Sound played when selecting accounts")
            .value(SoundManager.ACCOUNT_SOUND_PRESETS)
            .selected("Account Switch");
    private final ButtonSetting previewAccountSwitch = new ButtonSetting("Preview Account", "Preview the account switch sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(accountSwitchSound.getSelected()), uiVolume.getValue() / 100F, 1F));

    public SoundBoard() {
        super("SoundBoard", "Sound Board", ModuleCategory.SOUNDS);
        setup(
                uiVolume,
                openGuiSound, previewOpenGui,
                closeGuiSound, previewCloseGui,
                enableModuleSound, previewEnableModule,
                disableModuleSound, previewDisableModule,
                categorySound, previewCategory,
                accountSwitchSound, previewAccountSwitch
        );
    }

    public static SoundBoard getInstance() {
        return Instance.get(SoundBoard.class);
    }

    public SliderSettings getUiVolume() {
        return uiVolume;
    }

    public SelectSetting getOpenGuiSound() {
        return openGuiSound;
    }

    public SelectSetting getCloseGuiSound() {
        return closeGuiSound;
    }

    public SelectSetting getEnableModuleSound() {
        return enableModuleSound;
    }

    public SelectSetting getDisableModuleSound() {
        return disableModuleSound;
    }

    public SelectSetting getCategorySound() {
        return categorySound;
    }

    public SelectSetting getAccountSwitchSound() {
        return accountSwitchSound;
    }
}
