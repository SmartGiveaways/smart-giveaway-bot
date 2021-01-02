package pink.zak.giveawaybot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pink.zak.giveawaybot.GiveawayBot;

import java.io.File;


@SpringBootApplication
public class ApiApplication {
	private GiveawayBot bot;

	public void run(String[] args) {
		this.bot = this.testRun();
		SpringApplication.run(ApiApplication.class, args);
	}

	private GiveawayBot testRun() {
		GiveawayBot bot = new GiveawayBot(path -> new File("C:\\Users\\shear\\Documents\\Projects\\SmartGiveaways\\smart-giveaway-bot\\build\\libs").toPath());
		bot.load();
		return bot;
	}
}
