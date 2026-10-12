
package com.carmindy.gui_editor.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ModKeybinds {

    public static KeyMapping OPEN_EDITOR;

    public static void initialize() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("gui_editor", "general")
        );

        OPEN_EDITOR = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.gui_editor.open_editor",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_R,
                        category
                )
        );
    }
}
