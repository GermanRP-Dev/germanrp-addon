package eu.germanrp.addon.api.network;

public record TimerPacket(boolean active, String name, long start) implements GRPacket {}
