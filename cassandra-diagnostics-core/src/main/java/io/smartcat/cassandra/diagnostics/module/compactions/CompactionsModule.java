package io.smartcat.cassandra.diagnostics.module.compactions;

import io.smartcat.cassandra.diagnostics.Measurement;
import io.smartcat.cassandra.diagnostics.config.ConfigurationException;
import io.smartcat.cassandra.diagnostics.module.Module;
import io.smartcat.cassandra.diagnostics.module.ModuleConfiguration;
import io.smartcat.cassandra.diagnostics.reporter.Reporter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compaction module collecting Cassandra compaction info exposed over JMX. Requires local JMX connection.
 */
public class CompactionsModule extends Module {
    private static final Logger logger = LoggerFactory.getLogger(CompactionsModule.class);
    private static final String TIMER_THREAD = "compactions-module";

    private final CompactionsCollector collector;
    private final Timer timer;

    /**
     * Constructor.
     *
     * @param config    module configuration
     * @param reporters list of reporters
     * @throws ConfigurationException in case the provided module configuration is not valid
     */
    public CompactionsModule(ModuleConfiguration config, List<Reporter> reporters) throws ConfigurationException {
        super(config, reporters);

        CompactionsConfiguration cfg = CompactionsConfiguration.create(config.options);
        collector = new CompactionsCollector(cfg);

        logger.info("Compactions module initialized with {} {} reporting period.", cfg.period(), cfg.timeunit().name());

        timer = new Timer(TIMER_THREAD);

        if (collector.connect()) {
            timer.schedule(new CompactionsTask(), 0, cfg.period());
        }
    }

    @Override public void stop() {
        logger.trace("Stopping compactions module...");
        timer.cancel();
        collector.disconnect();
        logger.trace("Compactions module stopped!");
    }

    /**
     * Compaction reporter task that will be triggered at configured frequency.
     */
    private class CompactionsTask extends TimerTask {
        @Override public void run() {
            for (Measurement measurement : collector.collectMeasurements()) {
                for (Reporter reporter : reporters) {
                    reporter.report(measurement);
                }
            }
        }
    }
}
