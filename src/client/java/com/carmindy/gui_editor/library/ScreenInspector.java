
package com.carmindy.gui_editor.library;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ScreenInspector {

    private ScreenInspector() {
    }

    public static List<GuiElement> inspect(Screen screen) {
        List<GuiElement> elements = new ArrayList<>();
        int index = 0;

        for (GuiEventListener child : screen.children()) {
            if (!(child instanceof AbstractWidget widget)) {
                continue;
            }

            String className = widget.getClass()
                    .getName()
                    .toLowerCase(Locale.ROOT);

            String type = widget.getClass().getSimpleName();
            String renderStyle;

            if (className.contains("icon")
                    || className.contains("imagebutton")
                    || className.contains("spritebutton")) {
                renderStyle = "icon";
            } else if (className.contains("plaintext")
                    || className.contains("link")) {
                renderStyle = "text";
            } else if (widget instanceof Button) {
                renderStyle = "button";
            } else {
                renderStyle = "custom";
            }

            elements.add(new GuiElement(
                    screen.getClass().getName() + ":widget:" + index,
                    type,
                    widget.getMessage().getString(),
                    widget.getX(),
                    widget.getY(),
                    widget.getWidth(),
                    widget.getHeight(),
                    renderStyle,
                    GuiElement.Category.WIDGET,
                    widget
            ));

            index++;
        }

        return elements;
    }

    public static void addSpecialElement(
            List<GuiElement> elements,
            String id,
            String type,
            String label,
            int x,
            int y,
            int width,
            int height,
            String renderStyle,
            GuiElement.Category category,
            Object originalReference
    ) {
        elements.add(new GuiElement(
                id,
                type,
                label,
                x,
                y,
                width,
                height,
                renderStyle,
                category,
                originalReference
        ));
    }
}
