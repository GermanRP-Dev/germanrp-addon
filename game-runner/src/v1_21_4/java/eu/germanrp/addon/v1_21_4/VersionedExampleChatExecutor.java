package eu.germanrp.addon.v1_21_4;

import eu.germanrp.addon.core.executor.ExampleChatExecutor;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import javax.inject.Singleton;

@Singleton
@Implements(ExampleChatExecutor.class)
public class VersionedExampleChatExecutor implements ExampleChatExecutor {

    @Override
    public void sendHelloWorld() {
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Hello, World!"));
    }

}
