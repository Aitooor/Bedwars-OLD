package dev.eugenio.nasgarbedwars.api.exceptions;

import dev.eugenio.nasgarbedwars.api.server.NMSUtil;

public class InvalidEffectException extends Throwable {
    public InvalidEffectException(final String s) {
        super(s + " no es un efecto " + NMSUtil.getName() + " válido, usando defaults...");
    }
}
