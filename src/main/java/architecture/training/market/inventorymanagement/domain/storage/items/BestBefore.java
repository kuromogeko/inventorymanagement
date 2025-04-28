package architecture.training.market.inventorymanagement.domain.storage.items;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record BestBefore(LocalDate bestBeforeDate) {
    
    public BestBefore(LocalDate bestBeforeDate) {
        if (bestBeforeDate == null) {
            throw new IllegalArgumentException("Best before LocalDate cannot be null");
        }
        this.bestBeforeDate= bestBeforeDate;
    }

    public String getBestBeforeDateAsISOString() {
        return bestBeforeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public BestBefore older(BestBefore contender) {
        // date.isBefore(date2) returns true if date is older
        return this.bestBeforeDate.isBefore(contender.bestBeforeDate) ? this : contender;
    }

    public boolean isOlder(BestBefore contender){
        return this.bestBeforeDate.isBefore(contender.bestBeforeDate);
    }
}
