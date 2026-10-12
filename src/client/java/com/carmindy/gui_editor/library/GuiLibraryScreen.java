
package com.carmindy.gui_editor.library;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuiLibraryScreen extends Screen {

    public GuiLibraryScreen() {
        super(Component.literal("GUI Library"));
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.fill(0, 0, this.width, this.height, 0xFF000000);

        graphics.text(
                this.font,
                "GUI Library",
                20,
                20,
                0xFFFFFFFF
        );

        graphics.text(
                this.font,
                "Detected interfaces will appear here.",
                20,
                45,
                0xFFAAAAAA
        );

        super.extractRenderState(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }
}
