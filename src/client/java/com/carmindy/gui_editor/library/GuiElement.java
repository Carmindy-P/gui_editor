
package com.carmindy.gui_editor.library;

import net.minecraft.client.gui.components.AbstractWidget;

public class GuiElement {

    public enum Category {
        WIDGET,
        CREATIVE_TAB,
        ARMOR_SLOT,
        OFFHAND_SLOT,
        INVENTORY_SLOT,
        BACKGROUND,
        OTHER
    }

    private final String id;
    private final String type;
    private final String label;
    private final int width;
    private final int height;
    private final String renderStyle;
    private final Category category;

    private final AbstractWidget originalWidget;
    private final Object originalReference;

    private int x;
    private int y;

    public GuiElement(
            String id,
            String type,
            String label,
            int x,
            int y,
            int width,
            int height,
            String renderStyle,
            AbstractWidget originalWidget
    ) {
        this(
                id,
                type,
                label,
                x,
                y,
                width,
                height,
                renderStyle,
                Category.WIDGET,
                originalWidget,
                originalWidget
        );
    }

    public GuiElement(
            String id,
            String type,
            String label,
            int x,
            int y,
            int width,
            int height,
            String renderStyle,
            Category category,
            Object originalReference
    ) {
        this(
                id,
                type,
                label,
                x,
                y,
                width,
                height,
                renderStyle,
                category,
                originalReference instanceof AbstractWidget widget ? widget : null,
                originalReference
        );
    }

    private GuiElement(
            String id,
            String type,
            String label,
            int x,
            int y,
            int width,
            int height,
            String renderStyle,
            Category category,
            AbstractWidget originalWidget,
            Object originalReference
    ) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.renderStyle = renderStyle;
        this.category = category;
        this.originalWidget = originalWidget;
        this.originalReference = originalReference;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getRenderStyle() {
        return renderStyle;
    }

    public Category getCategory() {
        return category;
    }

    public AbstractWidget getOriginalWidget() {
        return originalWidget;
    }

    public Object getOriginalReference() {
        return originalReference;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
