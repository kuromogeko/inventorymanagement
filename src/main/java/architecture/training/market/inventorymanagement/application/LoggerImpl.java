package architecture.training.market.inventorymanagement.application;

import architecture.training.market.inventorymanagement.domain.DomainLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerImpl implements DomainLogger {
    private static final Logger logger = LoggerFactory.getLogger(DomainLogger.class);
    @Override
    public void log(String message) {
        logger.info(message);
    }
}
