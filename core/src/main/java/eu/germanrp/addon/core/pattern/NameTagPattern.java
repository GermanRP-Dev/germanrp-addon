package eu.germanrp.addon.core.pattern;

import java.util.regex.Pattern;

public final class NameTagPattern {

    public static final Pattern DARKLIST_PATTERN = Pattern.compile(
            "^► \\[Darklist] - (\\w{3,16}|\\[GR]\\w{3,16})",
            Pattern.CANON_EQ
    );
    public static final Pattern BOUNTY_MEMBER_WANTEDS_PATTERN = Pattern.compile(
            "^ {4}[►»] (\\w{3,16}|\\[GR]\\w{3,16})",
            Pattern.CANON_EQ
    );
    public static final Pattern ADD_BOUNTY_PATTERN = Pattern.compile(
            "^► \\[✦] Neuer Auftragsmord: (\\w{3,16}|\\[GR]\\w{3,16})",
            Pattern.CANON_EQ
    );
    public static final Pattern REMOVE_DARKLIST_PATTERN = Pattern.compile(
            "^► \\[Darklist] (\\w{3,16}|\\[GR]\\w{3,16}) hat (\\w{3,16}|\\[GR]\\w{3,16}) von der Darklist gelöscht!$",
            Pattern.CANON_EQ
    );
    public static final Pattern ADD_DARKLIST_PATTERN = Pattern.compile(
            "^► \\[Darklist] (\\w{3,16}|\\[GR]\\w{3,16}) hat (\\w{3,16}|\\[GR]\\w{3,16}) auf die Darklist gesetzt!$",
            Pattern.CANON_EQ
    );
    public static final Pattern ADD_WANTEDS_PATTERN = Pattern.compile(
            "► \\[✦] Neue Fahndung: (\\w{3,16}|\\[GR]\\w{3,16})",
            Pattern.CANON_EQ
    );
    public static final Pattern REMOVE_WANTEDS_PATTERN = Pattern.compile(
            "► \\[✦] (\\w{3,16}|\\[GR]\\w{3,16}) hat die Fahndung von (\\w{3,16}|\\[GR]\\w{3,16}) gelöscht",
            Pattern.CANON_EQ
    );
    public static final Pattern FRAKTIONSMITGLIEDER_TITLE = Pattern.compile(
            "^ {1,18}► Fraktionsmitglieder ",
            Pattern.CANON_EQ
    );
    public static final Pattern FAHNDUNGSLISTE_TITLE = Pattern.compile(
            "^ {1,18}FAHNDUNGSLISTE",
            Pattern.CANON_EQ
    );

    private NameTagPattern() {
        throw new UnsupportedOperationException("This class should not be instantiated");
    }
}
