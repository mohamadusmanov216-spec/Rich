package fun.rich.display.screens.mainmenu;

import fun.rich.utils.client.desktop.DesktopActions;
import fun.rich.utils.display.font.Fonts;
import fun.rich.utils.display.interfaces.QuickImports;
import fun.rich.utils.display.shape.ShapeProperties;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.Color;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MusicHubScreen extends Screen implements QuickImports {
    private final Screen parent;
    private TextFieldWidget input;
    private String feedback = "Paste a URL or type a song name.";
    private long feedbackTime;

    public MusicHubScreen(Screen parent) {
        super(Text.literal("Music Hub"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        input = new TextFieldWidget(textRenderer, width / 2 - 110, height / 2 - 38, 220, 20, Text.literal(""));
        input.setMaxLength(256);
        input.setPlaceholder(Text.literal("Song title or direct link"));
        addSelectableChild(input);
        setInitialFocus(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int panelWidth = 250;
        int panelHeight = 150;
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

        Fonts.getSize(22, Fonts.Type.BOLD).drawCenteredString(context.getMatrices(), "Music Hub", width / 2, py + 14, -1);
        Fonts.getSize(12, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), "This build opens external music providers from your query.", width / 2, py + 30, new Color(170, 170, 180).getRGB());

        input.setX(width / 2 - 110);
        input.setY(py + 42);
        input.render(context, mouseX, mouseY, delta);

        drawButton(context, px + 15, py + 74, 105, 18, "Open Link");
        drawButton(context, px + 130, py + 74, 105, 18, "YouTube");
        drawButton(context, px + 15, py + 98, 105, 18, "SoundCloud");
        drawButton(context, px + 130, py + 98, 105, 18, "Yandex Music");
        drawButton(context, px + 72, py + 122, 105, 18, "Back");

        if (!feedback.isEmpty() && System.currentTimeMillis() - feedbackTime < 5000L) {
            Fonts.getSize(12, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), feedback, width / 2, py + 144, new Color(170, 170, 180).getRGB());
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (input != null && input.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int panelWidth = 250;
        int panelHeight = 150;
        int px = width / 2 - panelWidth / 2;
        int py = height / 2 - panelHeight / 2;
        String value = input == null ? "" : input.getText().trim();

        if (hover(mouseX, mouseY, px + 15, py + 74, 105, 18)) {
            if (value.isEmpty()) {
                setFeedback("Enter a song title or direct link first.");
            } else if (value.startsWith("http://") || value.startsWith("https://")) {
                setFeedback(DesktopActions.openLink(value) ? "Link opened." : "Failed to open link.");
            } else {
                setFeedback(DesktopActions.openLink("https://www.youtube.com/results?search_query=" + encode(value)) ? "Opened YouTube search." : "Failed to open search.");
            }
            return true;
        }
        if (hover(mouseX, mouseY, px + 130, py + 74, 105, 18)) {
            setFeedback(openSearch("https://www.youtube.com/results?search_query=", value, "YouTube"));
            return true;
        }
        if (hover(mouseX, mouseY, px + 15, py + 98, 105, 18)) {
            setFeedback(openSearch("https://soundcloud.com/search?q=", value, "SoundCloud"));
            return true;
        }
        if (hover(mouseX, mouseY, px + 130, py + 98, 105, 18)) {
            setFeedback(openSearch("https://music.yandex.ru/search?text=", value, "Yandex Music"));
            return true;
        }
        if (hover(mouseX, mouseY, px + 72, py + 122, 105, 18)) {
            close();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return input != null && input.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            close();
            return true;
        }
        return input != null && input.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
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

    private String openSearch(String baseUrl, String value, String provider) {
        if (value.isEmpty()) {
            return "Enter a song title first.";
        }
        return DesktopActions.openLink(baseUrl + encode(value)) ? provider + " search opened." : "Failed to open " + provider + ".";
    }

    private String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    private void setFeedback(String feedback) {
        this.feedback = feedback;
        this.feedbackTime = System.currentTimeMillis();
    }
}
