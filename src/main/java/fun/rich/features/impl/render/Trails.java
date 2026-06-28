package fun.rich.features.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.rich.common.repository.friend.FriendUtils;
import fun.rich.events.player.TickEvent;
import fun.rich.events.render.WorldRenderEvent;
import fun.rich.features.module.Module;
import fun.rich.features.module.ModuleCategory;
import fun.rich.features.module.setting.implement.MultiSelectSetting;
import fun.rich.utils.client.managers.event.EventHandler;
import fun.rich.utils.display.color.ColorAssist;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Trails extends Module {
    private static final long TRAIL_LIFETIME_MS = 250L;
    private static final double MIN_DISTANCE = 0.01D;

    private final MultiSelectSetting targets = new MultiSelectSetting("Targets", "Who should have motion trails")
            .value("Players", "Friends", "Self")
            .selected("Friends", "Self");

    private final Map<UUID, List<TrailPoint>> trails = new ConcurrentHashMap<>();

    public Trails() {
        super("Trails", "Trails", ModuleCategory.RENDER);
        setup(targets);
    }

    @Override
    public void activate() {
        trails.clear();
    }

    @Override
    public void deactivate() {
        trails.clear();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) {
            trails.clear();
            return;
        }

        long now = System.currentTimeMillis();
        Set<UUID> validPlayers = new HashSet<>();
        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (!shouldRenderTrails(player)) {
                continue;
            }
            validPlayers.add(player.getUuid());
        }

        trails.entrySet().removeIf(entry -> {
            if (!validPlayers.contains(entry.getKey())) {
                return true;
            }

            entry.getValue().removeIf(point -> point.isExpired(now));
            return entry.getValue().isEmpty();
        });
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (mc.world == null || mc.player == null) {
            trails.clear();
            return;
        }

        long now = System.currentTimeMillis();
        MatrixStack stack = event.getStack();
        float partialTicks = event.getPartialTicks();

        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (!shouldRenderTrails(player)) {
                continue;
            }

            List<TrailPoint> playerTrail = trails.computeIfAbsent(player.getUuid(), uuid -> new ArrayList<>());
            playerTrail.removeIf(point -> point.isExpired(now));

            Vec3d interpolatedPos = player.getLerpedPos(partialTicks);
            if (playerTrail.isEmpty() || playerTrail.get(playerTrail.size() - 1).pos.distanceTo(interpolatedPos) >= MIN_DISTANCE) {
                playerTrail.add(new TrailPoint(interpolatedPos, getTrailColor(player), now));
            }

            renderTrail(stack, player, playerTrail, now);
        }
    }

    private void renderTrail(MatrixStack stack, AbstractClientPlayerEntity player, List<TrailPoint> playerTrail, long now) {
        if (playerTrail.size() < 2) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        float playerHeight = player.getHeight();

        for (TrailPoint point : playerTrail) {
            float age = (float) (now - point.time) / (float) TRAIL_LIFETIME_MS;
            float alpha = Math.max(0.01F, 1.0F - Math.min(1.0F, age));
            int color = ColorAssist.multAlpha(point.color, alpha);

            buffer.vertex(stack.peek().getPositionMatrix(), (float) point.pos.x, (float) (point.pos.y + playerHeight), (float) point.pos.z).color(color);
            buffer.vertex(stack.peek().getPositionMatrix(), (float) point.pos.x, (float) point.pos.y, (float) point.pos.z).color(color);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthFunc(GL11.GL_LESS);
        RenderSystem.disableBlend();
    }

    private int getTrailColor(AbstractClientPlayerEntity player) {
        return FriendUtils.isFriend(player) ? ColorAssist.getFriendColor() : ColorAssist.getClientColor();
    }

    private boolean shouldRenderTrails(AbstractClientPlayerEntity player) {
        if (!player.isAlive() || isGhostPlayer(player)) {
            return false;
        }

        if (player == mc.player) {
            if (mc.options.getPerspective() == Perspective.FIRST_PERSON) {
                return false;
            }
            return targets.isSelected("Self");
        }

        if (FriendUtils.isFriend(player) && targets.isSelected("Friends")) {
            return true;
        }

        return targets.isSelected("Players");
    }

    private boolean isGhostPlayer(AbstractClientPlayerEntity player) {
        if (player.getCustomName() != null) {
            String name = player.getCustomName().getString();
            if (name != null && name.startsWith("Ghost_")) {
                return true;
            }
        }

        return "OtherClientPlayerEntity".equals(player.getClass().getSimpleName()) && player.getPitch() == -30.0F;
    }

    private record TrailPoint(Vec3d pos, int color, long time) {
        private boolean isExpired(long now) {
            return now - time > TRAIL_LIFETIME_MS;
        }
    }
}
