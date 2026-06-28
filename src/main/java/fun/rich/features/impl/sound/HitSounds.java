package fun.rich.features.impl.sound;

import fun.rich.events.player.AttackEvent;
import fun.rich.features.module.Module;
import fun.rich.features.module.ModuleCategory;
import fun.rich.features.module.setting.implement.BooleanSetting;
import fun.rich.features.module.setting.implement.ButtonSetting;
import fun.rich.features.module.setting.implement.SelectSetting;
import fun.rich.features.module.setting.implement.SliderSettings;
import fun.rich.utils.client.Instance;
import fun.rich.utils.client.managers.event.EventHandler;
import fun.rich.utils.client.sound.SoundManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class HitSounds extends Module {
    private final SelectSetting sound = new SelectSetting("Hit Sound", "Sound played when you hit something")
            .value(SoundManager.HIT_SOUND_PRESETS)
            .selected("Bonk");
    private final SliderSettings volume = new SliderSettings("Volume", "Hit sound volume")
            .range(0, 100)
            .setValue(100);
    private final SliderSettings pitch = new SliderSettings("Pitch", "Hit sound pitch")
            .range(0.5F, 2.0F)
            .setValue(1.0F);
    private final BooleanSetting playersOnly = new BooleanSetting("Players Only", "Play hit sounds only on players")
            .setValue(false);
    private final ButtonSetting preview = new ButtonSetting("Preview", "Preview the selected hit sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(sound.getSelected()), volume.getValue() / 100F, pitch.getValue()));

    public HitSounds() {
        super("HitSounds", "Hit Sounds", ModuleCategory.SOUNDS);
        setup(sound, volume, pitch, playersOnly, preview);
    }

    public static HitSounds getInstance() {
        return Instance.get(HitSounds.class);
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (mc.player == null || mc.world == null || event.getEntity() == null) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (playersOnly.isValue() && !(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        SoundManager.playSound(SoundManager.resolvePreset(sound.getSelected()), volume.getValue() / 100F, pitch.getValue());
    }
}
