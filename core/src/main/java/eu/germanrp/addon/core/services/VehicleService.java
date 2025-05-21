package eu.germanrp.addon.core.services;

import lombok.Getter;
import lombok.Setter;
import net.labymod.api.Laby;

@Getter
@Setter
public class VehicleService {

    private boolean cruiseControlEnabled = false;
    private int cruiseControlSpeed = 50;

    public void toggleCruiseControl() {
        cruiseControlEnabled = !cruiseControlEnabled;
    }

    public void applyCruiseControlSpeed() {
        Laby.references().chatExecutor().chat("/tempomat " + cruiseControlSpeed, false);
    }

}
