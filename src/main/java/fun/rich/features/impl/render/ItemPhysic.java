package fun.rich.features.impl.render;

import fun.rich.features.module.Module;
import fun.rich.features.module.ModuleCategory;
import fun.rich.features.module.setting.implement.BooleanSetting;
import fun.rich.features.module.setting.implement.SliderSettings;
import fun.rich.utils.client.Instance;

public class ItemPhysic extends Module {
    private final BooleanSetting noBob = new BooleanSetting("No Bob", "Disable the floating bob animation while items are airborne")
            .setValue(false);
    private final SliderSettings spinSpeed = new SliderSettings("Spin Speed", "Rotation speed for dropped items")
            .range(0.2F, 2.0F)
            .setValue(1.0F);

    public ItemPhysic() {
        super("ItemPhysic", "Item Physic", ModuleCategory.RENDER);
        setup(noBob, spinSpeed);
    }

    public static ItemPhysic getInstance() {
        return Instance.get(ItemPhysic.class);
    }

    public boolean isNoBob() {
        return noBob.isValue();
    }

    public float getSpinSpeed() {
        return spinSpeed.getValue();
    }
}
