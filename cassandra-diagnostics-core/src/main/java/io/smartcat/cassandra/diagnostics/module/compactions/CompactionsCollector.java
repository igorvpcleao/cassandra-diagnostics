package io.smartcat.cassandra.diagnostics.module.compactions;

import io.smartcat.cassandra.diagnostics.Measurement;

import java.io.IOException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compaction info collector. Handles mbean, JMX connection and collecting metrics.
 */
class CompactionsCollector {
    private static final Logger logger = LoggerFactory.getLogger(CompactionsCollector.class);
    private static final String JXM_URL_FORMAT = "service:jmx:rmi:///jndi/rmi://[%s]:%d/jmxrmi";
    private static final String DEFAULT_SOCKET_FACTORY = "com.sun.jndi.rmi.factory.socket";
    private static final String MBEAN_OBJECT_NAME = "org.apache.cassandra.db:type=CompactionManager";

    private final CompactionsConfiguration config;

    private JMXConnector jmxc;
    private MBeanServerConnection mbsc;

    /**
     * Constructor.
     *
     * @param config module configuration
     */
    CompactionsCollector(final CompactionsConfiguration config) {
        this.config = config;
    }

    /**
     * Opens JMX connection registers mbean.
     *
     * @return {@code true} if connection succeeded, {@code false} otherwise.
     */
    boolean connect() {
        try {
            JMXServiceURL jmxURL = new JMXServiceURL(String.format(JXM_URL_FORMAT, config.jmxHost(), config.jmxPort()));

            jmxc = JMXConnectorFactory.connect(jmxURL, jmxEnv());
            mbsc = jmxc.getMBeanServerConnection();

            // retrieve bean (requires Cassandra library code)
            // proxy = JMX.newMBeanProxy(mbsc, new ObjectName(MBEAN_OBJECT_NAME), CompactionManagerMBean.class);
            return true;
        } catch (Exception e) {
            logger.error("Failed to use JMX...");
        }

        return false;
    }

    private Map<String, Object> jmxEnv() {
        Map<String, Object> env = new HashMap<>();
        env.put(DEFAULT_SOCKET_FACTORY, getRMIClientSocketFactory());

        if (config.jmxSslEnabled()) {
            String[] credentials = {config.jmxSslUsername(), config.jmxSslPassword()};
            env.put(JMXConnector.CREDENTIALS, credentials);
        }

        return env;
    }

    private RMIClientSocketFactory getRMIClientSocketFactory() {
        if (config.jmxSslEnabled()) {
            return new SslRMIClientSocketFactory();
        } else {
            return RMISocketFactory.getDefaultSocketFactory();
        }
    }

    /**
     * Closes the JMX connection.
     */
    void disconnect() {
        if (jmxc != null) {
            try {
                jmxc.close();
            } catch (IOException e) {
                logger.error("Cannot close jmx connection", e);
            }
            jmxc = null;
        }
    }

    /**
     * Collects the complete compaction info in form of measurements.
     *
     * @return list of measurements
     */
    List<Measurement> collectMeasurements() {
        throw new UnsupportedOperationException("Implement collectMeasurements!");
    }
}
