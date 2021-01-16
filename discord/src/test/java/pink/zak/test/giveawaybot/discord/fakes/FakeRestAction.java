package pink.zak.test.giveawaybot.discord.fakes;

import net.dv8tion.jda.internal.requests.RestActionImpl;
import pink.zak.test.giveawaybot.discord.TestBase;

import java.util.function.Consumer;

public class FakeRestAction<T> extends RestActionImpl<T> {
    private final T returnValue;

    public FakeRestAction(T returnValue) {
        super(TestBase.JDA_MOCK, null);
        this.returnValue = returnValue;
    }

    @Override
    public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
        if (success != null) {
            success.accept(this.returnValue);
        }
    }

    @Override
    public T complete(boolean shouldQueue) {
        return this.returnValue;
    }
}
