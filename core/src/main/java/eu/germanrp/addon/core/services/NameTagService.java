package eu.germanrp.addon.core.services;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class NameTagService {

    @Getter
    private final List<String> members = new ArrayList<>();
    @Getter
    private final List<String> darklist = new ArrayList<>();
    @Getter
    private final List<String> bounties = new ArrayList<>();
    @Getter
    private final List<String> wantedPlayers = new ArrayList<>();
}
