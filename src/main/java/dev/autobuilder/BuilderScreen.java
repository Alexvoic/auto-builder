package dev.autobuilder;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class BuilderScreen extends Screen {
    private ButtonWidget toggle, delayBtn, perTickBtn, reachBtn;

    public BuilderScreen() { super(Text.literal("Auto Builder")); }

    @Override
    protected void init() {
        int x = width / 2 - 100, y = height / 2 - 50;
        toggle = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoBuilder.enabled = !AutoBuilder.enabled; refresh();
        }).dimensions(x, y, 200, 20).build());

        delayBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoBuilder.delay = (AutoBuilder.delay + 1) % 11; refresh();
        }).dimensions(x, y + 25, 200, 20).build());

        perTickBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoBuilder.perTick = AutoBuilder.perTick % 8 + 1; refresh();
        }).dimensions(x, y + 50, 200, 20).build());

        reachBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoBuilder.reach += 0.5; if (AutoBuilder.reach > 4.5) AutoBuilder.reach = 1.0; refresh();
        }).dimensions(x, y + 75, 200, 20).build());
        refresh();
    }

    private void refresh() {
        toggle.setMessage(Text.literal("Auto Builder: " + (AutoBuilder.enabled ? "§aON" : "§cOFF")));
        delayBtn.setMessage(Text.literal("Delay (ticks): " + AutoBuilder.delay));
        perTickBtn.setMessage(Text.literal("Blocks per action: " + AutoBuilder.perTick));
        reachBtn.setMessage(Text.literal("Reach: " + AutoBuilder.reach));
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        // super.render draws the background itself in 1.21.x (calling it twice crashes)
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 75, 0xFFFFFFFF);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(input);
    }

    @Override public boolean shouldPause() { return false; }
}
