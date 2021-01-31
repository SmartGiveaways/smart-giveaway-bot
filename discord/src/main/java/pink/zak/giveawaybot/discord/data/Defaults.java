package pink.zak.giveawaybot.discord.data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.enums.Setting;
import pink.zak.giveawaybot.discord.data.models.Preset;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.colour.PaletteBuilder;
import pink.zak.giveawaybot.discord.service.types.ReactionContainer;

import java.awt.*;

public class Defaults {
    public static final Preset defaultPreset = new Preset("default");
    private final JDA jda;
    private Permission[] requiredPermissions;
    private Palette palette;

    public Defaults(GiveawayBot bot) {
        this.jda = bot.getShardManager().retrieveApplicationInfo().getJDA();
        this.setupDefaultPreset();
        this.setupRequiredPermissions();
        this.setupPalette();
    }

    private void setupDefaultPreset() {
        defaultPreset.setSetting(Setting.ENABLE_REACT_TO_ENTER, true);
        defaultPreset.setSetting(Setting.REACT_TO_ENTER_EMOJI, new ReactionContainer("\uD83C\uDF89", this.jda));
        defaultPreset.setSetting(Setting.ENABLE_MESSAGE_ENTRIES, true);
        defaultPreset.setSetting(Setting.ENTRIES_PER_MESSAGE, 1);
        defaultPreset.setSetting(Setting.MAX_ENTRIES, 1000);
        defaultPreset.setSetting(Setting.PING_WINNERS, true);
        defaultPreset.setSetting(Setting.WINNERS_MESSAGE, true);
        defaultPreset.setSetting(Setting.DM_WINNERS, false);
    }

    private void setupRequiredPermissions() {
        this.requiredPermissions = new Permission[]{Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.MESSAGE_ADD_REACTION,
                Permission.VIEW_CHANNEL
        };
    }

    private void setupPalette() {
        this.palette = new PaletteBuilder()
                .setPrimary("D6008D")
                .setSecondary("424066")
                .setSuccess(Color.GREEN)
                .setFailure(Color.RED)
                .build();
    }

    public Preset getDefaultPreset() {
        return defaultPreset;
    }

    public Permission[] getRequiredPermissions() {
        return this.requiredPermissions;
    }

    public Palette getPalette() {
        return this.palette;
    }
}
