package pink.zak.giveawaybot.listener.button;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ButtonRegistry extends ListenerAdapter {
    private final Map<String, Consumer<ButtonClickEvent>> buttonMap = Maps.newConcurrentMap();

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        Consumer<ButtonClickEvent> consumer = this.buttonMap.get(event.getComponentId());
        if (consumer != null)
            consumer.accept(event);
    }

    public ButtonRegistry registerButton(Button button, Consumer<ButtonClickEvent> consumer) {
        this.buttonMap.put(button.getId(), consumer);

        return this;
    }

    public void deregisterButtons(Button... buttons) {
        for (Button button : buttons)
            this.buttonMap.remove(button.getId());
    }
}
