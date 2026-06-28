package fun.rich.display.screens.clickgui.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;

import fun.rich.features.module.ModuleCategory;
import fun.rich.features.module.ShowcaseCatalog;
import fun.rich.display.screens.clickgui.MenuScreen;
import fun.rich.display.screens.clickgui.components.implement.category.CategoryComponent;
import fun.rich.display.screens.clickgui.components.implement.settings.TextComponent;
import fun.rich.utils.interactions.inv.InventoryFlowManager;
import fun.rich.display.screens.clickgui.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

@Setter
@Accessors(chain = true)
public class CategoryContainerComponent extends AbstractComponent {
    private final List<CategoryComponent> categoryComponents = new ArrayList<>();

    public void initializeCategoryComponents() {
        categoryComponents.clear();
        for (ModuleCategory category : ShowcaseCatalog.visibleCategories()) {
            categoryComponents.add(new CategoryComponent(category));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float startY = y + 24F;
        float availableHeight = Math.max(1F, MenuScreen.INSTANCE.height - 42F);
        int count = Math.max(1, categoryComponents.size());
        float iconHeight = 17F;
        float gap = count > 1 ? Math.max(3F, (availableHeight - iconHeight * count) / (count - 1F)) : 0F;
        float offset = 0F;

        for (CategoryComponent component : categoryComponents) {
            component.x = x + 6;
            component.y = startY + offset;
            component.width = 28;
            component.height = (int) iconHeight;
            component.render(context, mouseX, mouseY, delta);
            offset += iconHeight + gap;
        }
    }

    @Override
    public void tick() {
        if (TextComponent.typing || SearchComponent.typing) InventoryFlowManager.unPressMoveKeys();
        else InventoryFlowManager.updateMoveKeys();
        categoryComponents.forEach(AbstractComponent::tick);
        super.tick();
    }

    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseScrolled(mouseX, mouseY, amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }
}
