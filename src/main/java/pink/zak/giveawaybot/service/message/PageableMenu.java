package pink.zak.giveawaybot.service.message;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class PageableMenu {
    protected final AtomicInteger currentPage;
    protected int maxPage = Integer.MAX_VALUE;

    protected PageableMenu(int startPage) {
        this.currentPage = new AtomicInteger(startPage);
    }

    protected PageableMenu() {
        this.currentPage = new AtomicInteger(1);
    }

    public abstract void drawPage(int page);

    public int previousPage() {
        int currentPage = this.currentPage.intValue();
        if (currentPage <= 1)
            return currentPage;

        this.drawPage(this.currentPage.decrementAndGet());
        return currentPage - 1;
    }

    public int nextPage() {
        int currentPage = this.currentPage.intValue();
        if (currentPage >= this.maxPage)
            return currentPage;

        this.drawPage(this.currentPage.incrementAndGet());
        return currentPage + 1;
    }
}
