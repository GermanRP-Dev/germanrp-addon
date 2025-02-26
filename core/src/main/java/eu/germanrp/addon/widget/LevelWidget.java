package eu.germanrp.addon.widget;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.enums.LevelEnum;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

public class LevelWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private TextLine line;
    private int xpAmount = -1;
    private int neededXPAmount = 0;
    private LevelEnum levelEnum = LevelEnum.CURRENTANDMAX;
    private boolean levelUpdate;
    private boolean justJoined;
    private boolean wasAFK;
    private int lines = 0;
    private int ticks;

    public LevelWidget(GermanRPAddon addon, GermanRPCategory category) {
        super("level");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/level.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.level"), "0/0 XP");
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        ticks++;
        if (ticks == 10) {
            LevelEnum newLevelEnum = addon.configuration().widgets().levelSetting().get();
            if (levelEnum != newLevelEnum) {
                levelUpdate = true;
                levelEnum = newLevelEnum;
            }
            ticks = 0;
        }
        if (levelUpdate) {
            levelUpdate = false;
            if (levelEnum == LevelEnum.CURRENTANDMAX) {
                line.updateAndFlush(xpAmount + "/" + neededXPAmount + " XP");
            }
            if (levelEnum == LevelEnum.NEEDED) {
                line.updateAndFlush(neededXPAmount - xpAmount + " XP");
            }

            if (xpAmount > neededXPAmount) {
                justJoined = true;
                Laby.references().chatExecutor().chat("/level");
            }
        }
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        String message = event.chatMessage().getPlainText(); // +10 Erfahrungspunkte (x2)

        if (message.equals("                    Willkommen auf GERMANRP.")) {
            xpAmount = 0;
            justJoined = true;
            Laby.references().chatExecutor().chat("/level");
            return;
        }

        if (message.startsWith(" +") && message.contains("Erfahrungspunkte")) {

            String replacedMessage = message.replace(" ", "").replace("+", "").replace("Erfahrungspunkte", "").replace("(x2)", "").replace("(x3)", "");
            int messageXPAmount = Integer.parseInt(replacedMessage);
            if (message.contains("(x2)")) {
                messageXPAmount = (messageXPAmount * 2);
            }
            if (message.contains("(x3)")) {
                messageXPAmount = (messageXPAmount * 3);
            }
            xpAmount = (xpAmount + messageXPAmount);
            levelUpdate = true;
        }
        if (message.startsWith("      ➥ Erfahrungspunkte: ")) {
            String messageTwo = message.replace("      ➥ Erfahrungspunkte: ", "");
            int i = 0;
            for (String s : messageTwo.split("/")) {
                if (!s.equals("/")) {
                    i++;
                    if (i == 1) {
                        xpAmount = Integer.parseInt(s);
                    }
                    if (i == 2) {
                        neededXPAmount = Integer.parseInt(s);
                    }
                    levelUpdate = true;
                }
            }
        }
        if (justJoined) {
            lines++;
            if (message.equals("► [System] Du bist jetzt wieder anwesend.")) {
                lines = 7;
                wasAFK = true;
                event.setCancelled(true);
            }
            if (lines > 7) {
                event.setCancelled(true);
            }
            if (lines > 14) {
                if (!wasAFK) {
                    justJoined = false;
                    lines = 0;
                } else if (lines == 15) {
                    Laby.references().chatExecutor().chat("/afk");
                }

                if (lines == 19) {
                    justJoined = false;
                    wasAFK = false;
                    lines = 0;
                }
            }
        }
    }
}
