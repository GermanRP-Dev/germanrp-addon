//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.germanrp.addon.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.labymod.api.client.component.ComponentService;
import net.labymod.api.client.component.format.TextColor;

@Getter
@AllArgsConstructor
public enum NameTag {

    NONE(ComponentService.parseTextColor("black")),
    BLACK(ComponentService.parseTextColor("black")),
    DARKBLUE(ComponentService.parseTextColor("dark_blue")),
    DARKGREEN(ComponentService.parseTextColor("dark_green")),
    DARKPURPLE(ComponentService.parseTextColor("dark_purple")),
    GOLD(ComponentService.parseTextColor("gold")),
    GRAY(ComponentService.parseTextColor("gray")),
    DARKGRAY(ComponentService.parseTextColor("dark_gray")),
    BLUE(ComponentService.parseTextColor("blue")),
    GREEN(ComponentService.parseTextColor("green")),
    AQUA(ComponentService.parseTextColor("aqua")),
    LIGHTPURPLE(ComponentService.parseTextColor("light_purple")),
    YELLOW(ComponentService.parseTextColor("yellow")),
    WHITE(ComponentService.parseTextColor("white"));

    private final TextColor textColor;

}
