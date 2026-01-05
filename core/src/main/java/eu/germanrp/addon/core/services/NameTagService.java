package eu.germanrp.addon.core.services;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
public class NameTagService {

    private final Set<String> members = new HashSet<>();
    private final Set<String> darklist = new HashSet<>();
    private final Set<String> bounties = new HashSet<>();
    private final Set<String> wantedPlayers = new HashSet<>();

}
