package pink.zak.test.giveawaybot.discord.fakes;

import pink.zak.giveawaybot.discord.service.bot.JdaBot;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class FakeJdaBot extends JdaBot {

    public FakeJdaBot(UnaryOperator<Path> subBasePath) {
        super(subBasePath);
    }

    @Override
    public void unload() {

    }

    @Override
    public void onConnect() {

    }
}
