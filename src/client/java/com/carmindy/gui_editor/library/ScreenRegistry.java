
package com.carmindy.gui_editor.library;

import com.carmindy.gui_editor.editor.EditorScreen;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ScreenRegistry {

    private static final Map<String, DetectedScreen> SCREENS =
            new LinkedHashMap<>();

    private ScreenRegistry() {
    }

    public static void register(Screen screen) {
        if (screen instanceof EditorScreen) {
            return;
        }

        String className = screen.getClass().getName();
        String displayName = screen.getClass().getSimpleName();

        List<GuiElement> elements = ScreenInspector.inspect(screen);

        DetectedScreen detected = SCREENS.get(className);

        if (detected == null) {
            detected = new DetectedScreen(
                    className,
                    displayName,
                    screen.width,
                    screen.height,
                    elements,
                    screen
            );

            SCREENS.put(className, detected);
        } else {
            detected.setScreenSize(screen.width, screen.height);
            detected.setElements(elements);
            detected.setOriginalScreen(screen);
        }

        System.out.println(
                "[GUI Editor] Saved "
                        + elements.size()
                        + " widgets for "
                        + displayName
                        + " ("
                        + screen.width
                        + "x"
                        + screen.height
                        + ")"
        );
    }

    public static Collection<DetectedScreen> getScreens() {
        return SCREENS.values();
    }

    public static final class DetectedScreen {

        private final String className;
        private final String displayName;
        private Screen originalScreen;
        private int screenWidth;
        private int screenHeight;
        private List<GuiElement> elements = new ArrayList<>();

        public DetectedScreen(
                String className,
                String displayName,
                int screenWidth,
                int screenHeight,
                List<GuiElement> elements,
                Screen originalScreen
        ) {
            this.className = className;
            this.displayName = displayName;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.elements = new ArrayList<>(elements);
            this.originalScreen = originalScreen;
        }

        public Screen getOriginalScreen() {
            return originalScreen;
        }

        public void setOriginalScreen(Screen originalScreen) {
            this.originalScreen = originalScreen;
        }

        public String className() {
            return className;
        }

        public String displayName() {
            return displayName;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public void setScreenSize(int width, int height) {
            this.screenWidth = width;
            this.screenHeight = height;
        }

        public List<GuiElement> getElements() {
            return elements;
        }

        public void setElements(List<GuiElement> elements) {
            this.elements = new ArrayList<>(elements);
        }
    }
}
