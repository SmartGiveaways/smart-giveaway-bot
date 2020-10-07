package pink.zak.giveawaybot.service.storage.storage;


import pink.zak.giveawaybot.service.storage.Backend;

public abstract class DynamicStorage<T> extends Storage<T> {

    public DynamicStorage(Backend backend) {
        super(backend);
    }
}
