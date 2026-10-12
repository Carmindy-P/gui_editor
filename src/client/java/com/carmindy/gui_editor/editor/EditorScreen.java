
package com.carmindy.gui_editor.editor;

import com.carmindy.gui_editor.library.ScreenRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EditorScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 180;
    private static final int ITEM_HEIGHT = 24;
    private static final int CANVAS_TOP = 70;
    private static final int CANVAS_BOTTOM_MARGIN = 20;

    private static final int CREATIVE_WIDTH = 195;
    private static final int CREATIVE_HEIGHT = 136;

    private boolean sidebarOpen = true;
    private boolean libraryOpen = true;

    private int scrollOffset = 0;
    private String selectedScreenClass = null;

    public EditorScreen() {
        super(Component.literal("GUI Editor"));
    }

    private List<ScreenRegistry.DetectedScreen> getVisibleScreens() {
        return new ArrayList<>(ScreenRegistry.getScreens());
    }

    private ScreenRegistry.DetectedScreen getSelectedScreen() {
        if (selectedScreenClass == null) {
            return null;
        }

        for (ScreenRegistry.DetectedScreen detected : ScreenRegistry.getScreens()) {
            if (detected.className().equals(selectedScreenClass)) {
                return detected;
            }
        }

        return null;
    }

    @Override
    protected void init() {
        addRenderableWidget(
                Button.builder(
                        Component.literal("☰"),
                        button -> {
                            sidebarOpen = !sidebarOpen;
                            rebuildWidgets();
                        }
                ).bounds(10, 10, 30, 25).build()
        );

        if (!sidebarOpen) {
            return;
        }

        addRenderableWidget(
                Button.builder(
                        Component.literal("GUI Library"),
                        button -> {
                            libraryOpen = !libraryOpen;
                            rebuildWidgets();
                        }
                ).bounds(10, 45, SIDEBAR_WIDTH - 20, 25).build()
        );

        if (!libraryOpen) {
            return;
        }

        int y = 80 - scrollOffset;
        int bottomLimit = this.height - 10;

        for (ScreenRegistry.DetectedScreen detected : getVisibleScreens()) {
            if (y + ITEM_HEIGHT > 80 && y < bottomLimit) {
                String className = detected.className();

                addRenderableWidget(
                        Button.builder(
                                Component.literal(
                                        (className.equals(selectedScreenClass) ? "> " : "")
                                                + formatScreenName(detected.displayName())
                                ),
                                button -> {
                                    selectedScreenClass = className;
                                    rebuildWidgets();
                                }
                        ).bounds(
                                10,
                                y,
                                SIDEBAR_WIDTH - 20,
                                ITEM_HEIGHT
                        ).build()
                );
            }

            y += ITEM_HEIGHT + 3;
        }
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.fill(0, 0, this.width, this.height, 0xFF101010);

        if (sidebarOpen) {
            graphics.fill(0, 0, SIDEBAR_WIDTH, this.height, 0xFF202020);
            graphics.fill(0, 75, SIDEBAR_WIDTH, this.height, 0xFF181818);
        }

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int canvasLeft = sidebarOpen ? SIDEBAR_WIDTH : 0;
        int canvasWidth = Math.max(1, this.width - canvasLeft);

        graphics.text(this.font, "GUI Editor", canvasLeft + 12, 12, 0xFFFFFFFF);

        ScreenRegistry.DetectedScreen selected = getSelectedScreen();

        graphics.text(
                this.font,
                selected == null
                        ? "Select a screen from the library"
                        : "Selected: " + formatScreenName(selected.displayName()),
                canvasLeft + 12,
                32,
                0xFFAAAAAA
        );

        if (selected == null) {
            graphics.text(
                    this.font,
                    "No screen selected",
                    canvasLeft + 12,
                    52,
                    0xFFFFFF00
            );
            return;
        }

        graphics.text(
                this.font,
                "Detected widgets: " + selected.getElements().size(),
                canvasLeft + 12,
                52,
                0xFFFFFF00
        );

        Screen originalScreen = selected.getOriginalScreen();

        if (originalScreen == null) {
            graphics.text(
                    this.font,
                    "Original screen is no longer available",
                    canvasLeft + 12,
                    76,
                    0xFFFF7777
            );
            return;
        }

        int canvasHeight = Math.max(
                1,
                this.height - CANVAS_TOP - CANVAS_BOTTOM_MARGIN
        );

        int originalWidth = Math.max(1, selected.getScreenWidth());
        int originalHeight = Math.max(1, selected.getScreenHeight());

        float scale = Math.min(
                (float) canvasWidth / originalWidth,
                (float) canvasHeight / originalHeight
        );
        scale = Math.min(1.0f, scale);

        int previewWidth = Math.max(1, Math.round(originalWidth * scale));
        int previewHeight = Math.max(1, Math.round(originalHeight * scale));

        int previewLeft = canvasLeft + (canvasWidth - previewWidth) / 2;
        int previewTop = CANVAS_TOP + (canvasHeight - previewHeight) / 2;

        graphics.fill(
                previewLeft - 2,
                previewTop - 2,
                previewLeft + previewWidth + 2,
                previewTop + previewHeight + 2,
                0xFF555555
        );

        graphics.fill(
                previewLeft,
                previewTop,
                previewLeft + previewWidth,
                previewTop + previewHeight,
                0xFF000000
        );

        graphics.pose().pushMatrix();

        try {
            graphics.pose().translate(previewLeft, previewTop);
            graphics.pose().scale(scale, scale);

            int previewMouseX = originalWidth / 2;
            int previewMouseY = originalHeight / 2;

            if (originalScreen instanceof CreativeModeInventoryScreen creative) {
                drawCreativeBackground(
                        graphics,
                        creative,
                        previewMouseX,
                        previewMouseY
                );
            } else if (originalScreen instanceof AbstractContainerScreen<?> container) {
                drawContainerBackground(graphics, container);
            }

            // Render contents, items, and widgets.
            // Do not call extractBackground() here; it can trigger a second blur.
            originalScreen.extractRenderState(
                    graphics,
                    previewMouseX,
                    previewMouseY,
                    partialTick
            );
        } finally {
            graphics.pose().popMatrix();
        }
    }

    private void drawContainerBackground(
            GuiGraphicsExtractor graphics,
            AbstractContainerScreen<?> screen
    ) {
        // Vanilla's container background is intentionally not fabricated here.
        // Slot rectangles are not a substitute for the screen's actual texture.
    }

    private void drawCreativeBackground(
            GuiGraphicsExtractor graphics,
            CreativeModeInventoryScreen screen,
            int mouseX,
            int mouseY
    ) {
        CreativeModeTab selectedTab = getSelectedCreativeTab();

        if (selectedTab == null) {
            return;
        }

        int left = (screen.width - CREATIVE_WIDTH) / 2;
        int top = (screen.height - CREATIVE_HEIGHT) / 2;

        // Unselected tabs are behind the main inventory background.
        for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
            if (tab != selectedTab) {
                drawCreativeTab(graphics, tab, false, left, top);
            }
        }

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                selectedTab.getBackgroundTexture(),
                left,
                top,
                0.0F,
                0.0F,
                CREATIVE_WIDTH,
                CREATIVE_HEIGHT,
                256,
                256
        );

        // Selected tab is drawn in front of the background.
        drawCreativeTab(graphics, selectedTab, true, left, top);

        // Draw the screen's search field without calling its full background renderer.
        // The search field itself is accessed reflectively because it is private.
        drawCreativeSearchBox(graphics, screen, mouseX, mouseY);
    }

    private CreativeModeTab getSelectedCreativeTab() {
        try {
            Field field = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");
            field.setAccessible(true);
            return (CreativeModeTab) field.get(null);
        } catch (ReflectiveOperationException exception) {
            return CreativeModeTabs.getDefaultTab();
        }
    }

    private void drawCreativeSearchBox(
            GuiGraphicsExtractor graphics,
            CreativeModeInventoryScreen screen,
            int mouseX,
            int mouseY
    ) {
        try {
            Field field = CreativeModeInventoryScreen.class.getDeclaredField("searchBox");
            field.setAccessible(true);

            Object value = field.get(screen);
            if (value instanceof net.minecraft.client.gui.components.EditBox searchBox
                    && searchBox.visible) {
                searchBox.extractRenderState(graphics, mouseX, mouseY, 0.0F);
            }
        } catch (ReflectiveOperationException ignored) {
            // Search field rendering is optional; don't break the editor preview.
        }
    }

    private void drawCreativeTab(
            GuiGraphicsExtractor graphics,
            CreativeModeTab tab,
            boolean selected,
            int left,
            int top
    ) {
        boolean isTop = tab.row() == CreativeModeTab.Row.TOP;
        int column = tab.column();

        int xOffset = tab.isAlignedRight()
                ? CREATIVE_WIDTH - 27 * (7 - column) + 1
                : 27 * column;

        int x = left + xOffset;
        int y = top - (isTop ? 28 : -(CREATIVE_HEIGHT - 4));

        // Vanilla's sprites are numbered 1 through 7.
        int spriteIndex = Mth.clamp(column, 0, 6) + 1;
        String position = isTop ? "top" : "bottom";
        String state = selected ? "selected" : "unselected";

        Identifier sprite = Identifier.withDefaultNamespace(
                "container/creative_inventory/tab_"
                        + position + "_" + state + "_" + spriteIndex
        );

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprite,
                x,
                y,
                26,
                32
        );

        graphics.item(
                tab.getIconItem(),
                x + 5,
                y + (isTop ? 9 : 7)
        );
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollX,
            double scrollY
    ) {
        if (sidebarOpen && libraryOpen && mouseX < SIDEBAR_WIDTH) {
            int contentHeight = getVisibleScreens().size() * (ITEM_HEIGHT + 3);
            int visibleHeight = Math.max(0, this.height - 90);
            int maxScroll = Math.max(0, contentHeight - visibleHeight);

            scrollOffset -= (int) (scrollY * 20);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

            rebuildWidgets();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private String formatScreenName(String name) {
        return name
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .replaceAll("([A-Z])([A-Z][a-z])", "$1 $2");
    }
}
