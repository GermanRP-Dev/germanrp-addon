package eu.germanrp.addon.core.common.sound;

import eu.germanrp.addon.core.GermanRPAddon;
import eu.germanrp.addon.core.common.events.GermanRPAddonTickEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;

import java.util.ArrayDeque;
import java.util.Queue;

@RequiredArgsConstructor
public final class SoundSequence {

    private record Step(
            ResourceLocation location,
            float volume,
            float pitch,
            int delayTicks
    ) {
    }

    private final GermanRPAddon addon;

    private final Queue<Step> queue = new ArrayDeque<>();

    private int waitTicks = 0;

    public void enqueue(ResourceLocation location, float volume, float pitch, int delayTicks) {
        queue.add(new Step(location, volume, pitch, delayTicks));
    }

    public void enqueue(GermanRPSound sound, float volume, float pitch, int delayTicks) {
        enqueue(sound.asResourceLocation(), volume, pitch, delayTicks);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onTick(GermanRPAddonTickEvent event) {
        if (!event.isPhase(GermanRPAddonTickEvent.Phase.TICK)) {
            return;
        }

        if (waitTicks > 0) {
            waitTicks--;
            return;
        }

        val step = queue.poll();
        if (step == null) {
            return;
        }

        addon.getPlayer().playSound(step.location(), step.volume(), step.pitch());
        waitTicks = step.delayTicks();
    }

}
