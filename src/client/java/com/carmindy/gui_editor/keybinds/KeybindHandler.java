
package com.carmindy.gui_editor.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public class KeybindHandler {

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeybinds.OPEN_EDITOR.consumeClick()) {
                System.out.println("F8 keybind detected!");
                if (client.player != null) {
                    client.player.sendSystemMessage(
                            Component.literal("GUI Editor key pressed!")
                    );
                }
            }
        });
    }
}
