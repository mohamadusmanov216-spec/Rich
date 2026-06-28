package fun.rich.utils.client.desktop;

import lombok.experimental.UtilityClass;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

@UtilityClass
public class DesktopActions {
    public boolean openLink(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean copyText(String text) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
