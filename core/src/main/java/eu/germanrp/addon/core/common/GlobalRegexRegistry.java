package eu.germanrp.addon.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Getter
@AllArgsConstructor
public enum GlobalRegexRegistry {

    TITLE_FACTION_MEMBER_LIST(compile("^ {1,18}► Fraktionsmitglieder ")),
    TITLE_WANTED_LIST(compile("^ {1,18}FAHNDUNGSLISTE")),
    BOUNTY_ADD(compile("^► \\[✦] Neuer Auftragsmord: (\\w{3,16}|\\[GR]\\w{3,16})")),
    BOUNTY_REMOVE(compile("► \\[✦] Die Fraktion .+? hat (?:sich das Kopfgeld von |)(\\w{3,16}|\\[GR]\\w{3,16}) (?:ausgeschaltet|geholt)!$")),
    BOUNTY_MEMBER_WANTED_LIST_ENTRY(compile("^ {4}[►»] (\\w{3,16}|\\[GR]\\w{3,16})")),
    DARK_LIST_ENTRY(compile("^► \\[Darklist] - (\\w{3,16}|\\[GR]\\w{3,16}) \\(.+\\)$")),
    DARK_LIST_ADD(compile("^► \\[Darklist] (?:\\w{3,16}|\\[GR]\\w{3,16}) hat (\\w{3,16}|\\[GR]\\w{3,16}) auf die Darklist gesetzt!$")),
    DARK_LIST_REMOVE(compile("^► \\[Darklist] (\\w{3,16}|\\[GR]\\w{3,16}) hat (\\w{3,16}|\\[GR]\\w{3,16}) von der Darklist gelöscht!$")),
    GRAFFITI_ADD(compile("^► \\[✦] (?:\\[GR])?(?<player>\\w{3,16}) hat (?:ein|das) Graffiti(?: angebracht)? \\((\\S*\\s*\\S*)\\)(?:!| aufgefrischt!)$")),
    GRAFFITI_TIME(compile("^► Diese Stelle kann noch nicht wieder besprüht werden \\((?:(?<minutes>\\d+)\\s+Minuten?)?(?:\\s*(?<seconds>\\d+)\\s+Sekunden?)?\\)$")),
    PLANT_HARVEST(compile("^► Du hast \\S* (\\S+) mit \\d+(?: Stück|x|g)? Erlös geerntet\\.$")),
    WANTED_ADD(compile("^► \\[✦] Neue Fahndung: (\\w{3,16}|\\[GR]\\w{3,16})")),
    WANTED_REMOVE(compile("► \\[✦] (\\w{3,16}|\\[GR]\\w{3,16}) hat die Fahndung von (\\w{3,16}|\\[GR]\\w{3,16}) beendet. \\(.+?\\)$")),
    WANTED_INJAILED(compile("^► \\[✦] (\\w{3,16}|\\[GR]\\w{3,16}) wurde von (\\w{3,16}|\\[GR]\\w{3,16}) inhaftiert!$")),
    CRUISE_CONTROL_START(compile("^► Tempomat auf \\d+ km/h eingestellt\\.$")),
    XP_READER_STATS(compile("^► \\[System] - XP: (\\d+)/(\\d+)$")),
    FRAKTION_NAME_STATS(compile("^► \\[System] - Fraktion: (.+?)$")),
    XP_ADD_CHAT(compile("^ \\+(\\d+) Erfahrungspunkte(| \\(x2\\)| \\(x3 Gameboost\\))$")),
    PANIC_DEACTIVATE(compile("^► \\[✦] (\\w{3,16}|\\[GR]\\w{3,16}) hat den Panicbutton deaktiviert\\.$")),
    SKILL_EXPERIENCE(compile("^(§.\\+§.)(\\d{0,3}\\.\\d{0,2}) Skill XP \\((\\d{0,6}\\.\\d{0,2})\\/(\\d{0,6})\\)$")),
    POPPY_MESSAGE(compile("^ \\+ (\\d+) Mohn \\((\\d+)\\)$")),
    ;

    private final Pattern pattern;
}
