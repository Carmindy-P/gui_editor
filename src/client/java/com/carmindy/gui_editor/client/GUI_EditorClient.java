package com.carmindy.gui_editor.client;

import com.carmindy.gui_editor.keybinds.KeybindHandler;
import com.carmindy.gui_editor.keybinds.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import com.carmindy.gui_editor.library.ScreenRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class GUI_EditorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybinds.initialize();
        KeybindHandler.initialize();
        ScreenEvents.AFTER_INIT.register(
                (client, screen, scaledWidth, scaledHeight) -> {
                    ScreenRegistry.register(screen);
                }
        );
        System.out.println("GUI Editor initialized!");
    }
}