package pink.zak.giveawaybot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pink.zak.giveawaybot.discord.GiveawayBot;

import java.io.File;


@SpringBootApplication
public class ApiApplication {

	public void load(GiveawayBot bot) {

	}

	public void testRun() {
		GiveawayBot bot = new GiveawayBot(path -> new File("C:\\Users\\shear\\Documents\\Projects\\SmartGiveaways\\smart-giveaway-bot\\build\\libs").toPath());
		bot.load();
		this.load(bot);
		SpringApplication.run(ApiApplication.class);
	}
}
