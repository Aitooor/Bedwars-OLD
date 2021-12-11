package dev.eugenio.nasgarbedwars.api.exceptions;

import dev.eugenio.nasgarbedwars.api.server.NMSUtil;

public class InvalidMaterialException extends Exception {
    public InvalidMaterialException(final String s) {
        super(s + " no es un " + NMSUtil.getName() + " material válido! Usando defaults...");
    }
}
