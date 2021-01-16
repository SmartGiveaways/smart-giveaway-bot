package pink.zak.test.giveawaybot.discord.storage;

import pink.zak.giveawaybot.discord.storage.UserStorage;
import pink.zak.test.giveawaybot.discord.TestBase;

public class UserStorageTests {

    private final UserStorage userStorage = new UserStorage(TestBase.THREAD_MANAGER, TestBase.MONGO_CONNECTION_FACTORY, 751886048623067186L);
}
