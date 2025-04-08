package eu.germanrp.addon.listener;

import eu.germanrp.addon.GermanRPAddon;
import eu.germanrp.addon.common.enums.Faction;
import lombok.RequiredArgsConstructor;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static eu.germanrp.addon.common.enums.Faction.CAMORRA;

@RequiredArgsConstructor
public class FactionLanguageListener {

    private final GermanRPAddon addon;

    private final List<FactionLanguageEntry> factionLanguageEntries = List.of(
            new FactionLanguageEntry(CAMORRA, "hallo", "Ciao"),
            new FactionLanguageEntry(CAMORRA, "hey", "Ciao"),
            new FactionLanguageEntry(CAMORRA, "tschüss", "Ciao")
//            new FactionLanguageEntry(ESTABLISHMENT, "danke", "My sincere appreciation"),
//            new FactionLanguageEntry(ESTABLISHMENT, "entschuldigung", "I beg you pardon"),
//            new FactionLanguageEntry(ESTABLISHMENT, "hallo", "Hello"),
//            new FactionLanguageEntry(ESTABLISHMENT, "ja", "Yes"),
//            new FactionLanguageEntry(ESTABLISHMENT, "nein", "No"),
//            new FactionLanguageEntry(ESTABLISHMENT, "polizist", "Bobbie"),
//            new FactionLanguageEntry(ESTABLISHMENT, "selbstverständlich", "Of course"),
//            new FactionLanguageEntry(ESTABLISHMENT, "tschüss", "Goodbye"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "bitte", "por favor"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "bruder", "hermano"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "danke", "Gracias"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "entschuldigung", "Lo siento"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "frau", "señora"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "freund", "amigo"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "hallo", "Holá"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "herr", "señor"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "ich", "I"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "moment", "Momento"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "mutter", "madre"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "oma", "abuelita"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "onkel", "tío"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "opa", "abuelo"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "schwester", "hermana"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "sohn", "hijo"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "tante", "tía"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "tochter", "hija"),
//            new FactionLanguageEntry(MEDELLINKARTELL, "vater", "padre"),
//            new FactionLanguageEntry(OCALLAGHAN, "arschloch", "asshole"),
//            new FactionLanguageEntry(OCALLAGHAN, "chef", "Bórd"),
//            new FactionLanguageEntry(OCALLAGHAN, "chefin", "Bórd"),
//            new FactionLanguageEntry(OCALLAGHAN, "frau", "Bean"),
//            new FactionLanguageEntry(OCALLAGHAN, "freund", "Cara"),
//            new FactionLanguageEntry(OCALLAGHAN, "gerne", "Go sásta"),
//            new FactionLanguageEntry(OCALLAGHAN, "hallo", "Dia dhuit"),
//            new FactionLanguageEntry(OCALLAGHAN, "herr", "Mistir"),
//            new FactionLanguageEntry(OCALLAGHAN, "hurensohn", "Mac soith"),
//            new FactionLanguageEntry(OCALLAGHAN, "ich", "mé"),
//            new FactionLanguageEntry(OCALLAGHAN, "idiot", "leathcheann"),
//            new FactionLanguageEntry(OCALLAGHAN, "ja", "Tá"),
//            new FactionLanguageEntry(OCALLAGHAN, "kollege", "Comhghleacaí"),
//            new FactionLanguageEntry(OCALLAGHAN, "kollegin", "comhoibrí"),
//            new FactionLanguageEntry(OCALLAGHAN, "mutter", "Máthair"),
//            new FactionLanguageEntry(OCALLAGHAN, "nein", "Níl"),
//            new FactionLanguageEntry(OCALLAGHAN, "onkel", "Uncail"),
//            new FactionLanguageEntry(OCALLAGHAN, "tante", "Aintín"),
//            new FactionLanguageEntry(OCALLAGHAN, "vater", "Athair"),
//            new FactionLanguageEntry(YAKUZA, "danke", "Arigato"),
//            new FactionLanguageEntry(YAKUZA, "hallo", "Kon'nichiwa"),
//            new FactionLanguageEntry(YAKUZA, "natürlich", "Tozen"),
//            new FactionLanguageEntry(YAKUZA, "nein", "Ie"),
//            new FactionLanguageEntry(YAKUZA, "tschüss", "Sayonara"),
//            new FactionLanguageEntry(YAKUZA, "vielleicht", "Tabun")
    );

    @Subscribe
    public void onMessageSend(@NotNull ChatMessageSendEvent event) {
        String message = event.getMessage();

        boolean noCommandOrWhisperOrShout = !message.startsWith("/") || (message.startsWith("/w") || message.startsWith("/s") || message.startsWith("/say"));
        if (!noCommandOrWhisperOrShout) {
            return;
        }

        Faction faction = this.addon.configuration().language().get();

        List<FactionLanguageEntry> factionLanguageEntriesForSelectedFaction = this.factionLanguageEntries.stream()
                .filter(factionLanguageEntry -> factionLanguageEntry.faction() == faction)
                .toList();

        for (FactionLanguageEntry factionLanguageEntry : factionLanguageEntriesForSelectedFaction) {
            String key = factionLanguageEntry.key();
            String translation = factionLanguageEntry.translation();
            message = message.replaceAll(" " + key + " ", " " + translation + " ");
        }
    }

    private record FactionLanguageEntry(Faction faction, String key, String translation) {}
}
