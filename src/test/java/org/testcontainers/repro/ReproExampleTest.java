package org.testcontainers.repro;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.stream.Stream;

public class ReproExampleTest {

    private static final Logger LOG = LoggerFactory.getLogger(ReproExampleTest.class);

    private static final Duration CONTAINER_STARTUP_TIMEOUT = Duration.ofMinutes(4);

    public static final int OB_SERVER_SQL_PORT = 2881;
    public static final int OB_SERVER_RPC_PORT = 2882;

    public static final int LOG_PROXY_PORT = 2983;

    public static final String OB_SYS_USERNAME = "root";
    public static final String OB_SYS_PASSWORD = "pswd";

    public static final String NETWORK_MODE = "host";

    /**
     * Placeholder for a piece of code that demonstrates the bug. You can use this as a starting point, or replace
     * entirely.
     * <p>
     * Ideally this would be a failing test. If it's excessively difficult to form as a test (e.g. relates to log
     * output, teardown or other side effects) then it would be sufficient to explain the behaviour in the issue
     * description.
     */
    @Test
    public void demonstration() {

        try (
                GenericContainer<?> OB_SERVER =
                        new GenericContainer<>("oceanbase/oceanbase-ce:3.1.4")
                                .withNetworkMode(NETWORK_MODE)
                                .withExposedPorts(OB_SERVER_SQL_PORT, OB_SERVER_RPC_PORT)
                                .withEnv("OB_ROOT_PASSWORD", OB_SYS_PASSWORD)
                                .waitingFor(Wait.forLogMessage(".*boot success!.*", 1))
                                .withStartupTimeout(CONTAINER_STARTUP_TIMEOUT)
                                .withLogConsumer(new Slf4jLogConsumer(LOG));
                GenericContainer<?> LOG_PROXY =
                        new GenericContainer<>("whhe/oblogproxy:1.0.3")
                                .withNetworkMode(NETWORK_MODE)
                                .withExposedPorts(LOG_PROXY_PORT)
                                .withEnv("OB_SYS_USERNAME", OB_SYS_USERNAME)
                                .withEnv("OB_SYS_PASSWORD", OB_SYS_PASSWORD)
                                .waitingFor(Wait.forLogMessage(".*boot success!.*", 1))
                                .withStartupTimeout(CONTAINER_STARTUP_TIMEOUT)
                                .withLogConsumer(new Slf4jLogConsumer(LOG));
        ) {
            // but when I use old version will be successful started (old testcontainer version : 1.15.3)
            Startables.deepStart(Stream.of(OB_SERVER, LOG_PROXY)).join();
        }
    }
}
