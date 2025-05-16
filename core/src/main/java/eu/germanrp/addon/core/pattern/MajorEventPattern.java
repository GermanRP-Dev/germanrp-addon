package eu.germanrp.addon.core.pattern;

import java.util.regex.Pattern;

public class MajorEventPattern {
    public static final Pattern START_BOMBE_PATTERN = Pattern.compile(
            "^► \\[Darklist] - (\\w{3,16}|\\[GR]\\w{3,16})",
            Pattern.CANON_EQ
    );
    public static final Pattern APOTEKENRAUB_PATTERN = Pattern.compile(
            "^► Die Fraktion (.+?) hat den Apothekenraub gestartet!$",
            Pattern.CANON_EQ
    );
    public static final Pattern SHOPRAUB_PATTERN = Pattern.compile(
            "^► Die Fraktion (.+?) hat den Apothekenraub gestartet!$",
            Pattern.CANON_EQ
    );
    public static final Pattern HACKANGRIFF_PATTERN = Pattern.compile(
            "^► Die Fraktion (.+?) hat den Apothekenraub gestartet!$",
            Pattern.CANON_EQ
    );
    public static final Pattern JUWELENRAUB_PATTERN = Pattern.compile(
            "^► Die Fraktion (.+?) hat den Apothekenraub gestartet!$",
            Pattern.CANON_EQ
    );
}
