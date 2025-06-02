package eu.germanrp.addon.core.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DefaultAddonVariables implements AddonVariables {

    @Getter
    private final List<String> members = new ArrayList<>();
    @Getter
    private final List<String> darklist = new ArrayList<>();
    @Getter
    private final List<String> bounties = new ArrayList<>();
    @Getter
    private final List<String> wantedPlayers = new ArrayList<>();
}
