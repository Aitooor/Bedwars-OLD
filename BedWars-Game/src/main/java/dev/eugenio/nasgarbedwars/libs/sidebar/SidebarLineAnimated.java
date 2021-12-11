package dev.eugenio.nasgarbedwars.libs.sidebar;

public class SidebarLineAnimated extends SidebarLine {
    private final String[] lines;
    private int pos;

    public SidebarLineAnimated(final String[] lines) {
        this.pos = -1;
        this.lines = lines;
    }

    @Override
    public String getLine() {
        return this.lines[(++this.pos == this.lines.length) ? (this.pos = 0) : this.pos];
    }

    public String[] getLines() {
        return this.lines;
    }
}
