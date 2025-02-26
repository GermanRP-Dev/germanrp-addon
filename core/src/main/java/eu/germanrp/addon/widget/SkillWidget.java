package eu.germanrp.addon.widget;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.enums.LevelEnum;
import eu.germanrp.addon.widget.category.GermanRPCategory;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ActionBarReceiveEvent;

import java.text.DecimalFormat;

public class SkillWidget extends TextHudWidget<TextHudWidgetConfig> {

    private final GermanRPAddon addon;
    private TextLine line;
    private boolean skillUpdate = false;
    private String skillName;
    private LevelEnum levelEnum = LevelEnum.CURRENTANDMAX;
    private double xpAmount;
    private double neededXPAmount;
    private int ticks;

    public SkillWidget(GermanRPAddon addon, GermanRPCategory category) {

        super("skill");
        this.addon = addon;
        bindCategory(category);
        setIcon(Icon.texture(ResourceLocation.create("germanrpaddon", "textures/level.png")));
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        line = super.createLine(Component.translatable("germanrpaddon.widget.skill"), "Sozial-Skill (0/100)");
        line.setState(State.HIDDEN);
    }

    @Override
    public boolean isVisibleInGame() {
        return true;
    }

    @Override
    public void onTick(boolean isEditorContext) {
        ticks++;
        if (ticks == 10) {
            if (levelEnum != addon.configuration().widgets().skillSetting().get()) {
                levelEnum = addon.configuration().widgets().skillSetting().get();
                skillUpdate = true;
            }
        }
        if (skillUpdate) {
            if (line.state() == State.HIDDEN && skillName != null) {
                line.setState(State.VISIBLE);
            }
            skillUpdate = false;
            DecimalFormat df = new DecimalFormat("#0.00");
            DecimalFormat dfneeded = new DecimalFormat("#0");
            if (levelEnum == LevelEnum.CURRENTANDMAX) {
                line.updateAndFlush(skillName + "-Skill (" + df.format(xpAmount) + "/" + dfneeded.format(neededXPAmount) + ")");
            }
            if (levelEnum == LevelEnum.NEEDED) {
                line.updateAndFlush(skillName + "-Skill (" + df.format(neededXPAmount - xpAmount) + ")");
            }
            if (skillName == null) {
                line.updateAndFlush("Sozial-Skill (0/100)");
            }
        }
    }

    @Subscribe
    public void onActionBarReceive(ActionBarReceiveEvent event) {

        Component eventMessage = event.getMessage();
        if (eventMessage == null) {
            return;
        }
        if (!eventMessage.toString().contains("Skill")) {
            return;
        }

        String message = eventMessage.toString().replace("literal{§7+", "").replace("}", "");

        if (message.startsWith("§3")) {
            skillName = "Combat";
        }
        if (message.startsWith("§2")) {
            skillName = "Farming";
        }
        if (message.startsWith("§a")) {
            skillName = "Gärtner";
        }
        if (message.startsWith("§d")) {
            skillName = "Sozial";
        }
        if (message.startsWith("§9")) {
            skillName = "Fischer";
        }
        if (message.startsWith("§6")) {
            skillName = "Bergbau";
        }
        if (message.startsWith("§e")) {
            skillName = "Kochen";
        }

        if (!message.contains("Skill")) {
            return;
        }
        System.out.println("Debug: Contains \"Skill\"");
        for (String s : message.split("\\(")) {
            if (!s.contains("(")) {
                if (s.contains("/")) {
                    message = s.replace(")", "");
                }
            }
        }
        int i = 0;
        for (String s : message.split("/")) {
            if (!s.equals("/")) {
                i++;
                if (i == 1) {
                    xpAmount = Double.parseDouble(s);
                }
                if (i == 2) {
                    neededXPAmount = Double.parseDouble(s);
                }
            }
        }
        skillUpdate = true;
    }
}

