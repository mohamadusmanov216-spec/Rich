package fun.rich.mixins.player.entity;

import fun.rich.features.impl.render.ItemPhysic;
import fun.rich.utils.client.render.ItemPhysicState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @Shadow
    @Final
    private Random random;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;F)V", at = @At("TAIL"))
    private void rich$captureGroundState(ItemEntity itemEntity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
        ((ItemPhysicState) state).rich$setOnGround(itemEntity.isOnGround());
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void rich$renderItemPhysic(ItemEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ItemPhysic itemPhysic = ItemPhysic.getInstance();
        if (!itemPhysic.isState() || state.itemRenderState.isEmpty()) {
            return;
        }

        Vector3f scale = state.itemRenderState.getTransformation().scale;
        float scaleY = scale.y();
        float speed = itemPhysic.getSpinSpeed();
        boolean onGround = ((ItemPhysicState) state).rich$isOnGround();

        matrices.push();
        if (onGround) {
            float rotation = ItemEntity.getRotation(state.age * speed, state.uniqueOffset);
            matrices.translate(0.0F, 0.02F + 0.05F * scaleY, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(rotation));
        } else {
            float bob = itemPhysic.isNoBob() ? 0.02F : MathHelper.sin(state.age / 10.0F + state.uniqueOffset) * 0.1F + 0.1F;
            float rotation = ItemEntity.getRotation(state.age * speed, state.uniqueOffset);
            matrices.translate(0.0F, bob + 0.25F * scaleY, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation));
        }

        ItemEntityRenderer.renderStack(matrices, vertexConsumers, light, state, random);
        matrices.pop();
        ci.cancel();
    }
}
