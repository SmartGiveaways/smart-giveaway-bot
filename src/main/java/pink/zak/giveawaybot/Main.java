package pink.zak.giveawaybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.api.ApiApplication;
import pink.zak.giveawaybot.argument.ArgumentType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
        Set<ArgumentType> parsedArgs = parseArgs(args);
        GiveawayBot bot = new GiveawayBot(path -> path);
        bot.load();
        if (!parsedArgs.contains(ArgumentType.NO_API)) {
            ApiApplication api = new ApiApplication();
            api.load(bot);
            SpringApplication.run(ApiApplication.class, args);
        }
    }

    private static Set<ArgumentType> parseArgs(String[] args) {
        return Arrays.stream(args)
                .map(String::toLowerCase)
                .map(ArgumentType::findArg)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
