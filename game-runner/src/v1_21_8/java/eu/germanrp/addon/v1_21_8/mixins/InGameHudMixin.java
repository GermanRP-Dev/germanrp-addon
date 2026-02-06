package eu.germanrp.addon.v1_21_8.mixins;

import eu.germanrp.addon.core.GermanRPAddon;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Unique
    private static final int EXTRA_ROW_OFFSET = 10;
    @Unique
    private static final int ICON_SIZE = 9;
    @Unique
    private static final int ICON_SPACING = 8;

    @Unique
    private static final ResourceLocation HYDRATION_EMPTY_SPRITE =
            ResourceLocation.parse("germanrpaddon:hud/hydration_empty");

    @Unique
    private static final ResourceLocation HYDRATION_HALF_SPRITE =
            ResourceLocation.parse("germanrpaddon:hud/hydration_half");

    @Unique
    private static final ResourceLocation HYDRATION_FULL_SPRITE =
            ResourceLocation.parse("germanrpaddon:hud/hydration_full");

    @Shadow
    private int tickCount;

    @Final
    @Shadow
    private RandomSource random;

    @Inject(method = "renderFood", at = @At("TAIL"))
    private void germanrpaddon$renderFoodBar(
            GuiGraphics guiGraphics,
            Player player,
            int top,
            int right,
            CallbackInfo ci
    ) {
        double hydration = GermanRPAddon.getInstance().getPlayer().getHydration();
        if (Double.isNaN(hydration)) {
            return;
        }

        double clampedHydration = Math.clamp(hydration, 0.0, 100.0);
        int hydrationLevel = (int) (clampedHydration / 100.0 * 20.0);
        boolean shouldJiggle = clampedHydration <= 0.0 && this.tickCount % (hydrationLevel * 3 + 1) == 0;
        int rowTop = top - EXTRA_ROW_OFFSET;

        for (int i = 0; i < 10; i++) {
            int iconTop = rowTop;
            if (shouldJiggle) {
                iconTop += this.random.nextInt(3) - 1;
            }

            int x = right - i * ICON_SPACING - ICON_SIZE;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HYDRATION_EMPTY_SPRITE, x, iconTop, ICON_SIZE, ICON_SIZE);

            int iconLevel = i * 2 + 1;
            if (iconLevel < hydrationLevel) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HYDRATION_FULL_SPRITE, x, iconTop, ICON_SIZE, ICON_SIZE);
            } else if (iconLevel == hydrationLevel) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HYDRATION_HALF_SPRITE, x, iconTop, ICON_SIZE, ICON_SIZE);
            }
        }
    }

    @Inject(method = "getAirBubbleYLine", at = @At("RETURN"), cancellable = true)
    private void germanrpaddon$offsetAirBubbles(int left, int y, CallbackInfoReturnable<Integer> ci) {
        if (Double.isNaN(GermanRPAddon.getInstance().getPlayer().getHydration())) {
            return;
        }

        ci.setReturnValue(ci.getReturnValueI() - EXTRA_ROW_OFFSET);
    }

}
