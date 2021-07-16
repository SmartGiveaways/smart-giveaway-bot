package pink.zak.giveawaybot.service.message;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class PageableMenu {
    protected final AtomicInteger currentPage;
    protected long cooldown;
    private long lastChange = System.currentTimeMillis();
    protected int maxPage = Integer.MAX_VALUE;

    protected PageableMenu(int startPage) {
        this.currentPage = new AtomicInteger(startPage);
    }

    protected PageableMenu() {
        this.currentPage = new AtomicInteger(1);
        this.cooldown = 0;
    }

    public abstract void drawPage(int page);

    public void previousPage() {
        if (this.currentPage.intValue() <= 1 || (this.lastChange + this.cooldown) > System.currentTimeMillis()) {
            return;
        }
        this.lastChange = System.currentTimeMillis();
        this.drawPage(this.currentPage.decrementAndGet());
    }

    public void nextPage() {
        if (this.currentPage.get() >= this.maxPage || (this.lastChange + this.cooldown) > System.currentTimeMillis()) {
            return;
        }
        this.lastChange = System.currentTimeMillis();
        this.drawPage(this.currentPage.incrementAndGet());
    }
}
