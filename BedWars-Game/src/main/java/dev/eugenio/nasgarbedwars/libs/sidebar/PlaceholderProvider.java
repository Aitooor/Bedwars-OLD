package dev.eugenio.nasgarbedwars.libs.sidebar;

import java.util.Objects;
import java.util.concurrent.Callable;

public class PlaceholderProvider {
    private final String placeholder;
    private final Callable<String> replacement;

    public PlaceholderProvider(final String placeholder, final Callable<String> replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public String getReplacement() {
        try {
            return this.replacement.call();
        } catch (Exception ex) {
            return "-";
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof PlaceholderProvider)) return false;
        PlaceholderProvider placeholderProvider = (PlaceholderProvider) object;
        return placeholderProvider.placeholder.equalsIgnoreCase(this.placeholder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.placeholder, this.replacement);
    }
}
