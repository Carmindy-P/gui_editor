package com.carmindy.gui_editor.client;

import com.carmindy.gui_editor.keybinds.KeybindHandler;
import com.carmindy.gui_editor.keybinds.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;

public class GUI_EditorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybinds.initialize();
        KeybindHandler.initialize();

        System.out.println("GUI Editor initialized!");
    }
}