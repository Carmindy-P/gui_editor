
package com.carmindy.gui_editor.keybinds;

import com.carmindy.gui_editor.editor.EditorScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class KeybindHandler {

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeybinds.OPEN_EDITOR.consumeClick()) {
                Minecraft.getInstance().gui.setScreen(new EditorScreen());
            }
        });
    }
}
