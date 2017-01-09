package io.smartcat.cassandra.diagnostics.module.compactions;

import io.smartcat.cassandra.diagnostics.config.ConfigurationException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.Yaml;

/**
 * Compaction module configuration.
 */
public class CompactionsConfiguration {
    private Values values = new Values();

    private CompactionsConfiguration() {
    }

    /**
     * Create typed configuration for metrics module out of generic module configuration.
     *
     * @param options Module configuration options.
     * @return types request rate module configuration from a generic one
     * @throws ConfigurationException in case the provided options are not valid
     */
    public static CompactionsConfiguration create(Map<String, Object> options) throws ConfigurationException {
        CompactionsConfiguration conf = new CompactionsConfiguration();
        Yaml yaml = new Yaml();
        String str = yaml.dumpAsMap(options);
        conf.values = yaml.loadAs(str, CompactionsConfiguration.Values.class);
        return conf;
    }

    /**
     * Request rate reporting period.
     *
     * @return request rate reporting period
     */
    public int period() {
        return values.period;
    }

    /**
     * Request rate reporting time unit.
     *
     * @return request rate reporting time unit
     */
    public TimeUnit timeunit() {
        return values.timeunit;
    }

    /**
     * Reporting rate in milliseconds.
     *
     * @return reporting rate in milliseconds
     */
    public long reportingRateInMillis() {
        return timeunit().toMillis(period());
    }

    /**
     * JMX host address.
     *
     * @return jmx host address
     */
    public String jmxHost() {
        return values.jmxHost;
    }

    /**
     * JMX host port.
     *
     * @return jmx host port
     */
    public int jmxPort() {
        return values.jmxPort;
    }

    /**
     * JMX ssl enabled.
     *
     * @return jmx ssl enabled
     */
    public boolean jmxSslEnabled() {
        return values.jmxSslEnabled;
    }

    /**
     * JMX ssl username.
     *
     * @return jmx ssl username
     */
    public String jmxSslUsername() {
        return values.jmxSslUsername;
    }

    /**
     * JMX ssl password.
     *
     * @return jmx ssl password
     */
    public String jmxSslPassword() {
        return values.jmxSslPassword;
    }

    /**
     * A helper class for constructing immutable outer class.
     */
    public static class Values {
        private static final int DEFAULT_PERIOD = 1;
        private static final String DEFAULT_TIMEUNIT = "SECONDS";
        private static final String DEFAULT_JMX_HOST = "127.0.0.1";
        private static final int DEFAULT_JMX_PORT = 7199;
        private static final boolean DEFAULT_JMX_SSL_ENABLED = false;
        private static final String DEFAULT_JMX_SSL_USERNAME = null;
        private static final String DEFAULT_JMX_SSL_PASSWORD = null;

        /**
         * Metrics reporting period.
         */
        public int period = DEFAULT_PERIOD;

        /**
         * Metrics reporting time unit.
         */
        public TimeUnit timeunit = TimeUnit.valueOf(DEFAULT_TIMEUNIT);

        /**
         * JMX host address.
         */
        public String jmxHost = DEFAULT_JMX_HOST;

        /**
         * JMX host port.
         */
        public int jmxPort = DEFAULT_JMX_PORT;

        /**
         * JMX ssl enabled.
         */
        public boolean jmxSslEnabled = DEFAULT_JMX_SSL_ENABLED;

        /**
         * JMX ssl username.
         */
        public String jmxSslUsername = DEFAULT_JMX_SSL_USERNAME;

        /**
         * JMX ssl password.
         */
        public String jmxSslPassword = DEFAULT_JMX_SSL_PASSWORD;
    }
}
