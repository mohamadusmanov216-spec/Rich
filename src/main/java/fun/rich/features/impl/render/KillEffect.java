package fun.rich.features.impl.render;

import com.mojang.authlib.GameProfile;
import fun.rich.events.player.EntityDeathEvent;
import fun.rich.events.render.WorldRenderEvent;
import fun.rich.features.module.Module;
import fun.rich.features.module.ModuleCategory;
import fun.rich.features.module.setting.implement.BooleanSetting;
import fun.rich.features.module.setting.implement.ButtonSetting;
import fun.rich.features.module.setting.implement.SelectSetting;
import fun.rich.features.module.setting.implement.SliderSettings;
import fun.rich.utils.client.managers.event.EventHandler;
import fun.rich.utils.client.sound.SoundManager;
import fun.rich.utils.display.geometry.Render3D;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KillEffect extends Module {
    private final SliderSettings volume = new SliderSettings("Volume", "Death sound volume").setValue(100).range(0, 100);
    private final BooleanSetting playSound = new BooleanSetting("Play Sound", "Play death sound").setValue(true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", "Render the effect for mobs too").setValue(false);
    private final SelectSetting effectType = new SelectSetting("Effect Type", "Death animation type")
            .value("Cross", "Soul", "Heaven Beam", "Lightning Strike")
            .selected("Soul");
    private final SelectSetting soundType = new SelectSetting("Sound", "Sound played on death")
            .value(SoundManager.KILL_SOUND_PRESETS)
            .selected("Orthodox");
    private final ButtonSetting previewSound = new ButtonSetting("Preview Sound", "Preview the selected death sound")
            .setButtonName("Play")
            .setRunnable(() -> SoundManager.playSound(SoundManager.resolvePreset(soundType.getSelected()), volume.getValue() / 100F, 1F));

    private final Map<Entity, EntityRenderData> renderEntities = new ConcurrentHashMap<>();

    public KillEffect() {
        super("KillEffect", "Kill Effect", ModuleCategory.RENDER);
        setup(volume, playSound, mobs, effectType, soundType, previewSound);
    }

    private static class EntityRenderData {
        private final long timestamp;
        private final float yaw;
        private final Vec3d startPos;
        private final Entity entity;
        private final GameProfile gameProfile;
        private final EntityPose pose;
        private final OtherClientPlayerEntity fakePlayer;

        public EntityRenderData(long timestamp, float yaw, Vec3d startPos, Entity entity, OtherClientPlayerEntity fakePlayer) {
            this.timestamp = timestamp;
            this.yaw = yaw;
            this.startPos = startPos;
            this.entity = entity;
            this.gameProfile = entity instanceof PlayerEntity ? ((PlayerEntity) entity).getGameProfile() : null;
            this.pose = entity.getPose();
            this.fakePlayer = fakePlayer;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public float getYaw() {
            return yaw;
        }

        public Vec3d getStartPos() {
            return startPos;
        }

        public Entity getEntity() {
            return entity;
        }

        public GameProfile getGameProfile() {
            return gameProfile;
        }

        public EntityPose getPose() {
            return pose;
        }

        public OtherClientPlayerEntity getFakePlayer() {
            return fakePlayer;
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (mc.world == null || mc.player == null) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;
        if (!mobs.isValue() && !(entity instanceof PlayerEntity)) return;
        if (entity == mc.player || renderEntities.containsKey(entity)) return;

        if (playSound.isValue()) {
            SoundManager.playSound(SoundManager.resolvePreset(soundType.getSelected()), volume.getValue() / 100F, 1F);
        }

        OtherClientPlayerEntity fakePlayer = null;
        if (effectType.isSelected("Soul") && entity instanceof PlayerEntity playerEntity) {
            fakePlayer = new OtherClientPlayerEntity(mc.world, playerEntity.getGameProfile());
            fakePlayer.setPitch(-30.0f);
            fakePlayer.setYaw(entity.getYaw());
            fakePlayer.headYaw = entity.getYaw();
            fakePlayer.bodyYaw = entity.getYaw();
            fakePlayer.setCustomNameVisible(false);
            fakePlayer.setCustomName(Text.literal("Ghost_" + playerEntity.getGameProfile().getId()));
            mc.world.addEntity(fakePlayer);
        }

        renderEntities.put(entity, new EntityRenderData(System.currentTimeMillis(), entity.getYaw(), entity.getPos(), entity, fakePlayer));
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (mc.world == null || mc.player == null) return;

        MatrixStack stack = event.getStack();
        float tickDelta = event.getPartialTicks();
        List<Entity> entitiesToRemove = new ArrayList<>();

        renderEntities.forEach((entity, data) -> {
            float timeProgress = (System.currentTimeMillis() - data.getTimestamp()) / 3000.0f;
            if (timeProgress >= 1.0f) {
                entitiesToRemove.add(entity);
                if (data.getFakePlayer() != null) {
                    mc.world.removeEntity(data.getFakePlayer().getId(), Entity.RemovalReason.DISCARDED);
                }
                return;
            }

            if (effectType.isSelected("Cross")) {
                renderCross(data, timeProgress);
            } else if (effectType.isSelected("Soul")) {
                renderSoul(stack, tickDelta, data, timeProgress);
            } else if (effectType.isSelected("Heaven Beam")) {
                renderHeavenBeam(data, timeProgress);
            } else if (effectType.isSelected("Lightning Strike")) {
                renderLightningStrike(data, timeProgress);
            }
        });

        entitiesToRemove.forEach(renderEntities::remove);
    }

    private void renderCross(EntityRenderData data, float timeProgress) {
        int color = new Color(255, 255, 255, (int) (150 * (1 - timeProgress))).getRGB();
        float yaw = (float) Math.toRadians(data.getYaw() + 95);
        Vec3d pos = data.getStartPos();
        Render3D.drawLine(pos.add(0, 0, 0), pos.add(0, 3, 0), color, 5, true);
        float armLength = 1.0f;
        float yOffset = 2.3f;
        Vec3d start = pos.add(-armLength * Math.sin(yaw), yOffset, armLength * Math.cos(yaw));
        Vec3d end = pos.add(armLength * Math.sin(yaw), yOffset, -armLength * Math.cos(yaw));
        Render3D.drawLine(start, end, color, 5, true);
    }

    private void renderSoul(MatrixStack stack, float tickDelta, EntityRenderData data, float timeProgress) {
        float yOffset = timeProgress * 3.0f;
        int alpha = (int) (255 * (1 - timeProgress));
        Vec3d soulPos = data.getStartPos().add(0, yOffset, 0);
        Entity renderEntity = data.getEntity();
        if (data.getFakePlayer() != null) {
            renderEntity = data.getFakePlayer();
            renderEntity.setPos(soulPos.x, soulPos.y, soulPos.z);
        }
        Render3D.drawEntity(renderEntity, soulPos, data.getYaw(), alpha, stack, tickDelta);
    }

    private void renderHeavenBeam(EntityRenderData data, float timeProgress) {
        Vec3d pos = data.getStartPos();
        double radius = 0.55 + Math.sin((1.0 - timeProgress) * 18.0) * 0.08;
        double beamHeight = 8.5 - timeProgress * 2.0;

        int lineColor = new Color(255, 252, 224, (int) (220 * (1 - timeProgress))).getRGB();
        int fillColor = new Color(255, 245, 190, (int) (70 * (1 - timeProgress))).getRGB();

        Vec3d p1 = pos.add(-radius, 0, -radius);
        Vec3d p2 = pos.add(radius, 0, -radius);
        Vec3d p3 = pos.add(radius, 0, radius);
        Vec3d p4 = pos.add(-radius, 0, radius);

        Vec3d t1 = p1.add(0, beamHeight, 0);
        Vec3d t2 = p2.add(0, beamHeight, 0);
        Vec3d t3 = p3.add(0, beamHeight, 0);
        Vec3d t4 = p4.add(0, beamHeight, 0);

        Render3D.drawQuad(p1, p2, t2, t1, fillColor, true);
        Render3D.drawQuad(p2, p3, t3, t2, fillColor, true);
        Render3D.drawQuad(p3, p4, t4, t3, fillColor, true);
        Render3D.drawQuad(p4, p1, t1, t4, fillColor, true);

        Render3D.drawLine(p1, t1, lineColor, 2.5F, true);
        Render3D.drawLine(p2, t2, lineColor, 2.5F, true);
        Render3D.drawLine(p3, t3, lineColor, 2.5F, true);
        Render3D.drawLine(p4, t4, lineColor, 2.5F, true);
        Render3D.drawLine(pos.add(0, 0.1, 0), pos.add(0, beamHeight + 0.25, 0), lineColor, 3.5F, true);

        double haloRadius = radius * 1.35;
        Vec3d h1 = pos.add(-haloRadius, 0.03, -haloRadius);
        Vec3d h2 = pos.add(haloRadius, 0.03, -haloRadius);
        Vec3d h3 = pos.add(haloRadius, 0.03, haloRadius);
        Vec3d h4 = pos.add(-haloRadius, 0.03, haloRadius);
        Render3D.drawLine(h1, h2, lineColor, 1.6F, true);
        Render3D.drawLine(h2, h3, lineColor, 1.6F, true);
        Render3D.drawLine(h3, h4, lineColor, 1.6F, true);
        Render3D.drawLine(h4, h1, lineColor, 1.6F, true);
    }

    private void renderLightningStrike(EntityRenderData data, float timeProgress) {
        Vec3d origin = data.getStartPos().add(0, 0.1, 0);
        Vec3d top = origin.add(0, 8.5 - timeProgress * 1.2, 0);
        float fade = 1.0F - timeProgress;
        int coreColor = new Color(255, 255, 255, (int) (245 * fade)).getRGB();
        int glowColor = new Color(116, 204, 255, (int) (170 * fade)).getRGB();

        List<Vec3d> mainBolt = new ArrayList<>();
        mainBolt.add(top);

        int segments = 8;
        for (int i = 1; i < segments; i++) {
            float progress = i / (float) segments;
            double y = top.y - progress * (top.y - origin.y);
            double offsetScale = (1.0 - progress) * 0.45 + 0.08;
            double x = origin.x + segmentOffset(i, timeProgress, 0.8) * offsetScale;
            double z = origin.z + segmentOffset(i, timeProgress + 0.37F, 1.1) * offsetScale;
            mainBolt.add(new Vec3d(x, y, z));
        }
        mainBolt.add(origin);

        for (int i = 0; i < mainBolt.size() - 1; i++) {
            Vec3d start = mainBolt.get(i);
            Vec3d end = mainBolt.get(i + 1);
            Render3D.drawLine(start, end, glowColor, 4.2F, true);
            Render3D.drawLine(start, end, coreColor, 2.1F, true);

            if (i > 1 && i < mainBolt.size() - 2 && i % 2 == 0) {
                Vec3d branchStart = start;
                double branchLength = 0.5 + (1.0 - timeProgress) * 0.6;
                double branchX = branchStart.x + segmentOffset(i + 3, timeProgress + 0.14F, 1.7) * branchLength;
                double branchY = branchStart.y - 0.45 - i * 0.03;
                double branchZ = branchStart.z + segmentOffset(i + 5, timeProgress + 0.61F, 1.3) * branchLength;
                Vec3d branchEnd = new Vec3d(branchX, branchY, branchZ);
                Render3D.drawLine(branchStart, branchEnd, glowColor, 2.3F, true);
                Render3D.drawLine(branchStart, branchEnd, coreColor, 1.2F, true);
            }
        }

        double ringRadius = 0.45 + (1.0 - timeProgress) * 0.75;
        Vec3d last = null;
        for (int i = 0; i <= 12; i++) {
            double angle = Math.PI * 2.0 * i / 12.0;
            Vec3d point = origin.add(Math.cos(angle) * ringRadius, 0.02, Math.sin(angle) * ringRadius);
            if (last != null) {
                Render3D.drawLine(last, point, glowColor, 1.7F, true);
            }
            last = point;
        }
    }

    private double segmentOffset(int segment, float timeProgress, double frequency) {
        return Math.sin(segment * 1.35 + timeProgress * 18.0 * frequency) * 0.7
                + Math.cos(segment * 0.75 + timeProgress * 11.0 * frequency) * 0.3;
    }
}
