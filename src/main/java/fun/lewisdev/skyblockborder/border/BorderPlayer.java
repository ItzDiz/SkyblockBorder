package fun.lewisdev.skyblockborder.border;

public class BorderPlayer {

    private boolean enabled;
    private BorderColor color;

    public BorderPlayer(boolean enabled, BorderColor color) {
        this.enabled = enabled;
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public BorderColor getColor() {
        return color;
    }

    void setColor(BorderColor color) {
        this.color = color;
    }
}
