package eu.germanrp.addon.core.listener;

import eu.germanrp.addon.api.models.SkillXP;
import eu.germanrp.addon.core.GermanRPAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ActionBarReceiveEvent;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;

import static eu.germanrp.addon.core.common.GlobalRegexRegistry.SKILL_EXPERIENCE;

public class SkillXPListener {

    @Subscribe
    public void onActionMessage(ActionBarReceiveEvent e) {
        GermanRPAddon main = GermanRPAddon.getInstance();
        if (e.phase() != Phase.PRE) {
            return;
        }

        if (main.configuration().skillXP().get() != SkillXP.PERCENT) {
            return;
        }

        String message = e.getMessage().toString().replace("literal{", "").replace("}", "");
        Matcher matcher = SKILL_EXPERIENCE.getPattern().matcher(message);
        if (!matcher.find()) {
            return;
        }

        int maxSkillXP = Integer.parseInt(matcher.group(4));
        double curenXP = Double.parseDouble(matcher.group(3));
        double gainedXP = Double.parseDouble(matcher.group(2));
        DecimalFormat df = new DecimalFormat("#.##");
        String prefix = matcher.group(1);
        String percentGained = df.format(gainedXP / maxSkillXP * 100);
        if(Objects.equals(percentGained, "0")) percentGained = new  DecimalFormat("#.###").format(gainedXP / maxSkillXP * 100);
        e.setMessage(Component.text(prefix +  percentGained + "% Skill XP (" + df.format(curenXP / maxSkillXP * 100) + "%/100%)"));
    }
}
