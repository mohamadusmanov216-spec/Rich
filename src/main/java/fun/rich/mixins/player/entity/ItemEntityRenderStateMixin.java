package fun.rich.mixins.player.entity;

import fun.rich.utils.client.render.ItemPhysicState;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntityRenderState.class)
public class ItemEntityRenderStateMixin implements ItemPhysicState {
    @Unique
    private boolean rich$onGround;

    @Override
    public boolean rich$isOnGround() {
        return rich$onGround;
    }

    @Override
    public void rich$setOnGround(boolean onGround) {
        rich$onGround = onGround;
    }
}
