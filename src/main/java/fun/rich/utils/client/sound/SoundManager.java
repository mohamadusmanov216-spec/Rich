package fun.rich.utils.client.sound;

import antidaunleak.api.annotation.Native;
import fun.rich.features.impl.sound.SoundBoard;
import fun.rich.utils.display.interfaces.QuickImports;
import fun.rich.utils.interactions.interact.PlayerInteractionHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SoundManager implements QuickImports {
    public String[] UI_SOUND_PRESETS = {"Rich Open", "Rich Close", "Category", "Pling", "Orb", "Bell", "None"};
    public String[] MODULE_SOUND_PRESETS = {"Rich Enable", "Rich Disable", "Pling", "Bell", "Orb", "Pop", "None"};
    public String[] HIT_SOUND_PRESETS = {"Bonk", "Bring", "Magic Squash", "Meow", "Nya", "Pop", "Soft", "Squash", "Uwu", "Boykisser", "Click", "Glass", "Moan", "Crit", "None"};
    public String[] KILL_SOUND_PRESETS = {"Orthodox", "Lightning", "Bell", "Wither", "Beacon", "None"};
    public String[] ACCOUNT_SOUND_PRESETS = {"Account Switch", "Pling", "Bell", "Orb", "Pop", "None"};

    public SoundEvent OPEN_GUI = SoundEvent.of(Identifier.of("minecraft:gui_open"));
    public SoundEvent CLOSE_GUI = SoundEvent.of(Identifier.of("minecraft:gui_close"));
    public SoundEvent ENABLE_MODULE = SoundEvent.of(Identifier.of("minecraft:module_enable"));
    public SoundEvent DISABLE_MODULE = SoundEvent.of(Identifier.of("minecraft:module_disable"));
    public SoundEvent CATEGORY_CLICK = SoundEvent.of(Identifier.of("minecraft:category_click"));
    public SoundEvent ORTHODOX = SoundEvent.of(Identifier.of("minecraft:kolokolnia_kill"));
    public SoundEvent ACCOUNT_SWITCH = SoundEvent.of(Identifier.of("minecraft:accountswitch"));

    Map<String, Identifier> presetIds = new LinkedHashMap<>();

    public void init() {
        Registry.register(Registries.SOUND_EVENT, OPEN_GUI.id(), OPEN_GUI);
        Registry.register(Registries.SOUND_EVENT, CLOSE_GUI.id(), CLOSE_GUI);
        Registry.register(Registries.SOUND_EVENT, ENABLE_MODULE.id(), ENABLE_MODULE);
        Registry.register(Registries.SOUND_EVENT, DISABLE_MODULE.id(), DISABLE_MODULE);
        Registry.register(Registries.SOUND_EVENT, CATEGORY_CLICK.id(), CATEGORY_CLICK);
        Registry.register(Registries.SOUND_EVENT, ORTHODOX.id(), ORTHODOX);
        Registry.register(Registries.SOUND_EVENT, ACCOUNT_SWITCH.id(), ACCOUNT_SWITCH);

        registerPreset("Rich Open", OPEN_GUI.id());
        registerPreset("Rich Close", CLOSE_GUI.id());
        registerPreset("Rich Enable", ENABLE_MODULE.id());
        registerPreset("Rich Disable", DISABLE_MODULE.id());
        registerPreset("Category", CATEGORY_CLICK.id());
        registerPreset("Orthodox", ORTHODOX.id());
        registerPreset("Account Switch", ACCOUNT_SWITCH.id());
        registerPreset("Pling", Identifier.of("minecraft:block.note_block.pling"));
        registerPreset("Orb", Identifier.of("minecraft:entity.experience_orb.pickup"));
        registerPreset("Bell", Identifier.of("minecraft:block.bell.use"));
        registerPreset("Pop", Identifier.of("minecraft:entity.item.pickup"));
        registerPreset("Crit", Identifier.of("minecraft:entity.player.attack.crit"));
        registerPreset("Arrow", Identifier.of("minecraft:entity.arrow.hit_player"));
        registerPreset("Trident", Identifier.of("minecraft:item.trident.hit"));
        registerPreset("Lightning", Identifier.of("minecraft:entity.lightning_bolt.impact"));
        registerPreset("Wither", Identifier.of("minecraft:entity.wither.spawn"));
        registerPreset("Beacon", Identifier.of("minecraft:block.beacon.activate"));
        registerPreset("Bonk", Identifier.of("minecraft:hit_bonk"));
        registerPreset("Bring", Identifier.of("minecraft:hit_bring"));
        registerPreset("Magic Squash", Identifier.of("minecraft:hit_magic_squash"));
        registerPreset("Meow", Identifier.of("minecraft:hit_meow"));
        registerPreset("Nya", Identifier.of("minecraft:hit_nya"));
        registerPreset("Soft", Identifier.of("minecraft:hit_soft"));
        registerPreset("Squash", Identifier.of("minecraft:hit_squash"));
        registerPreset("Uwu", Identifier.of("minecraft:hit_uwu"));
        registerPreset("Boykisser", Identifier.of("minecraft:hit_boykisser"));
        registerPreset("Click", Identifier.of("minecraft:hit_click"));
        registerPreset("Glass", Identifier.of("minecraft:hit_glass"));
        registerPreset("Moan", Identifier.of("minecraft:hit_moan"));
    }

    public void playSound(SoundEvent sound) {
        playSound(sound, 1, 1);
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (sound != null && !PlayerInteractionHelper.nullCheck()) {
            mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.BLOCKS, volume, pitch);
        }
    }

    public void playGuiOpen() {
        SoundBoard soundBoard = getSoundBoard();
        String preset = soundBoard != null ? soundBoard.getOpenGuiSound().getSelected() : "Rich Open";
        float volume = soundBoard != null ? soundBoard.getUiVolume().getValue() / 100F : 1F;
        playSound(resolvePreset(preset), volume, 1F);
    }

    public void playGuiClose() {
        SoundBoard soundBoard = getSoundBoard();
        String preset = soundBoard != null ? soundBoard.getCloseGuiSound().getSelected() : "Rich Close";
        float volume = soundBoard != null ? soundBoard.getUiVolume().getValue() / 100F : 1F;
        playSound(resolvePreset(preset), volume, 1F);
    }

    public void playCategoryClick() {
        SoundBoard soundBoard = getSoundBoard();
        String preset = soundBoard != null ? soundBoard.getCategorySound().getSelected() : "Category";
        float volume = soundBoard != null ? soundBoard.getUiVolume().getValue() / 100F : 1F;
        playSound(resolvePreset(preset), volume, 1F);
    }

    public void playModuleEnable(float volume) {
        SoundBoard soundBoard = getSoundBoard();
        String preset = soundBoard != null ? soundBoard.getEnableModuleSound().getSelected() : "Rich Enable";
        playSound(resolvePreset(preset), volume, 1F);
    }

    public void playModuleDisable(float volume) {
        SoundBoard soundBoard = getSoundBoard();
        String preset = soundBoard != null ? soundBoard.getDisableModuleSound().getSelected() : "Rich Disable";
        playSound(resolvePreset(preset), volume, 1F);
    }

    public void playAccountSwitch() {
        SoundBoard soundBoard = getSoundBoard();
        String preset = soundBoard != null ? soundBoard.getAccountSwitchSound().getSelected() : "Account Switch";
        float volume = soundBoard != null ? soundBoard.getUiVolume().getValue() / 100F : 1F;
        playSound(resolvePreset(preset), volume, 1F);
    }

    public SoundEvent resolvePreset(String preset) {
        if (preset == null || preset.equalsIgnoreCase("None")) {
            return null;
        }

        Identifier id = presetIds.get(preset);
        return id == null ? null : SoundEvent.of(id);
    }

    private void registerPreset(String preset, Identifier id) {
        presetIds.put(preset, id);
    }

    private SoundBoard getSoundBoard() {
        try {
            return SoundBoard.getInstance();
        } catch (Exception ignored) {
            return null;
        }
    }
}
