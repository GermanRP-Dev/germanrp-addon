package eu.germanrp.addon.core.services;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class NameTagService {

    private final List<String> members = new ArrayList<>();
    private final List<String> darklist = new ArrayList<>();
    private final List<String> bounties = new ArrayList<>();
    private final List<String> wantedPlayers = new ArrayList<>();
}
