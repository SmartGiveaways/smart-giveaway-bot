package pink.zak.giveawaybot.discord.service.colour;


import java.awt.*;

public class PaletteBuilder {
    private Color primary;
    private Color secondary;
    private Color success;
    private Color failure;

    public PaletteBuilder setPrimary(Color primary) {
        this.primary = primary;
        return this;
    }

    public PaletteBuilder setPrimary(int red, int green, int blue) {
        this.primary = new Color(red, green, blue);
        return this;
    }

    public PaletteBuilder setPrimary(String hex) {
        this.primary = this.fromHex(hex);
        return this;
    }

    public PaletteBuilder setSecondary(Color secondary) {
        this.secondary = secondary;
        return this;
    }

    public PaletteBuilder setSecondary(int red, int green, int blue) {
        this.secondary = new Color(red, green, blue);
        return this;
    }

    public PaletteBuilder setSecondary(String hex) {
        this.secondary = this.fromHex(hex);
        return this;
    }

    public PaletteBuilder setSuccess(Color success) {
        this.success = success;
        return this;
    }

    public PaletteBuilder setSuccess(int red, int green, int blue) {
        this.success = new Color(red, green, blue);
        return this;
    }

    public PaletteBuilder setSuccess(String hex) {
        this.success = this.fromHex(hex);
        return this;
    }

    public PaletteBuilder setFailure(Color failure) {
        this.failure = failure;
        return this;
    }

    public PaletteBuilder setFailure(int red, int green, int blue) {
        this.failure = new Color(red, green, blue);
        return this;
    }

    public PaletteBuilder setFailure(String hex) {
        this.failure = this.fromHex(hex);
        return this;
    }

    public Palette build() throws IllegalArgumentException {
        if (this.primary == null || this.secondary == null) {
            throw new IllegalArgumentException("Primary and secondary colours must be set");
        }
        return new Palette(this.primary, this.secondary, this.success, this.failure);
    }

    private Color fromHex(String hex) {
        return Color.decode((hex.startsWith("#") ? "" : "#").concat(hex));
    }
}
