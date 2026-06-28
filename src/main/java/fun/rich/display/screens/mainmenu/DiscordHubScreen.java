package fun.rich.display.screens.mainmenu;

import fun.rich.Rich;
import fun.rich.common.discord.DiscordManager;
import fun.rich.utils.client.desktop.DesktopActions;
import fun.rich.utils.display.font.Fonts;
import fun.rich.utils.display.interfaces.QuickImports;
import fun.rich.utils.display.shape.ShapeProperties;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;

public class DiscordHubScreen extends Screen implements QuickImports {
    private final Screen parent;
    private String feedback = "Discord RPC status from the current desktop session.";
    private long feedbackTime;

    public DiscordHubScreen(Screen parent) {
        super(Text.literal("Discord Hub"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int panelWidth = 230;
        int panelHeight = 145;
        int px = width / 2 - panelWidth / 2;
        int py = height / 2 - panelHeight / 2;

        rectangle.render(ShapeProperties.create(context.getMatrices(), px, py, panelWidth, panelHeight)
                .round(10)
                .outlineColor(new Color(95, 95, 110, 160).getRGB())
                .color(
                        new Color(25, 25, 30, 225).getRGB(),
                        new Color(32, 32, 38, 225).getRGB(),
                        new Color(20, 20, 26, 225).getRGB(),
                        new Color(32, 32, 38, 225).getRGB())
                .build());

        Fonts.getSize(22, Fonts.Type.BOLD).drawCenteredString(context.getMatrices(), "Discord Hub", width / 2, py + 14, -1);

        DiscordManager manager = Rich.getInstance().getDiscordManager();
        boolean connected = manager != null && manager.getInfo() != null && !"Unknown".equalsIgnoreCase(manager.getInfo().userName());
        String status = connected ? "Connected as: " + manager.getInfo().userName() : "Discord is not detected yet";
        String sub = connected ? "User ID: " + manager.getInfo().userId() : "Open Discord desktop app and reopen this panel";

        Fonts.getSize(14, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), status, width / 2, py + 38, new Color(220, 220, 220).getRGB());
        Fonts.getSize(12, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), sub, width / 2, py + 50, new Color(150, 150, 160).getRGB());
        Fonts.getSize(12, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), "Voice calls inside Minecraft are not in this build yet.", width / 2, py + 64, new Color(180, 130, 130).getRGB());

        drawButton(context, px + 15, py + 82, 95, 18, "Open App");
        drawButton(context, px + 120, py + 82, 95, 18, "Open Server");
        drawButton(context, px + 15, py + 106, 95, 18, "Copy ID");
        drawButton(context, px + 120, py + 106, 95, 18, "Back");

        if (!feedback.isEmpty() && System.currentTimeMillis() - feedbackTime < 5000L) {
            Fonts.getSize(12, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), feedback, width / 2, py + 132, new Color(170, 170, 180).getRGB());
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int panelWidth = 230;
        int panelHeight = 145;
        int px = width / 2 - panelWidth / 2;
        int py = height / 2 - panelHeight / 2;

        if (hover(mouseX, mouseY, px + 15, py + 82, 95, 18)) {
            setFeedback(DesktopActions.openLink("discord://-/channels/@me") ? "Discord app open request sent." : "Failed to open Discord app.");
            return true;
        }
        if (hover(mouseX, mouseY, px + 120, py + 82, 95, 18)) {
            setFeedback(DesktopActions.openLink("https://discord.gg/zYctK4mjZZ") ? "Discord server opened." : "Failed to open Discord server.");
            return true;
        }
        if (hover(mouseX, mouseY, px + 15, py + 106, 95, 18)) {
            DiscordManager manager = Rich.getInstance().getDiscordManager();
            String userId = manager != null && manager.getInfo() != null ? manager.getInfo().userId() : "";
            setFeedback(!userId.isEmpty() && DesktopActions.copyText(userId) ? "Discord user ID copied." : "No Discord user ID available yet.");
            return true;
        }
        if (hover(mouseX, mouseY, px + 120, py + 106, 95, 18)) {
            close();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    private void drawButton(DrawContext context, float x, float y, float w, float h, String label) {
        rectangle.render(ShapeProperties.create(context.getMatrices(), x, y, w, h)
                .round(5)
                .outlineColor(new Color(90, 95, 115, 155).getRGB())
                .color(
                        new Color(48, 53, 78, 180).getRGB(),
                        new Color(71, 77, 112, 180).getRGB(),
                        new Color(53, 58, 90, 180).getRGB(),
                        new Color(71, 77, 112, 180).getRGB())
                .build());
        Fonts.getSize(14, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), label, x + w / 2F, y + 7, -1);
    }

    private boolean hover(double mouseX, double mouseY, float x, float y, float w, float h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private void setFeedback(String feedback) {
        this.feedback = feedback;
        this.feedbackTime = System.currentTimeMillis();
    }
}
