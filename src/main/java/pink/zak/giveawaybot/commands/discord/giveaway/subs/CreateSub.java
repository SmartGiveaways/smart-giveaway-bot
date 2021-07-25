package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.giveaway.GiveawayCmdUtils;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.SlashCommandUtils;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.service.time.Time;

public class CreateSub extends SubCommand {
    private final GiveawayCmdUtils cmdUtils;

    public CreateSub(GiveawayBot bot, GiveawayCmdUtils cmdUtils) {
        super(bot, "create", true, false);
        this.cmdUtils = cmdUtils;
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        long lengthMillis = Time.parse(event.getOption("length").getAsString());
        long winners = event.getOption("winners").getAsLong();
        String giveawayItem = event.getOption("name").getAsString();
        String presetName = SlashCommandUtils.hasOption(event, "presetname") ? event.getOption("presetname").getAsString() : "default";
        TextChannel giveawayChannel = event.getTextChannel();
        if (SlashCommandUtils.hasOption(event, "channel")) {
            GuildChannel guildChannel = event.getOption("channel").getAsGuildChannel();

            if (guildChannel instanceof TextChannel textChannel) {
                giveawayChannel = textChannel;
            }
        }

        this.cmdUtils.create(server, lengthMillis, winners, presetName, giveawayItem, giveawayChannel, event);
    }
}
