package org.testcontainers.starrocks;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Set;

/**
 * Testcontainers implementation for StarRocks.
 * <p>
 * Exposed ports:
 * <ul>
 *     <li>Database: 9030</li>
 *     <li>HTTP: 8040</li>
 * </ul>
 */
public class StarRocksContainer extends JdbcDatabaseContainer<StarRocksContainer> {

    static final String NAME = "allin1-ubuntu";

    static final String DOCKER_IMAGE_NAME = "starrocks/allin1-ubuntu";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(DOCKER_IMAGE_NAME);

    private static final Integer SQL_PORT = 9030;

    private static final int REST_API_PORT = 8040;

    private String databaseName = "sys";

    private String username = "root";

    private String password = "";

    public StarRocksContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public StarRocksContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        addExposedPorts(SQL_PORT, REST_API_PORT);

        waitingFor(
            new HttpWaitStrategy()
                .forPath("/api/health")
                .forPort(REST_API_PORT)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofMinutes(1))
        );
    }

    /**
     * @return the ports on which to check if the container is ready
     * @deprecated use {@link #getLivenessCheckPortNumbers()} instead
     */
    @NotNull
    @Override
    @Deprecated
    protected Set<Integer> getLivenessCheckPorts() {
        return super.getLivenessCheckPorts();
    }

    @Override
    public String getDriverClassName() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return "com.mysql.cj.jdbc.Driver";
        } catch (ClassNotFoundException e) {
            return "com.mysql.jdbc.Driver";
        }
    }

    @Override
    public String getJdbcUrl() {
        String additionalUrlParams = constructUrlParameters("?", "&");
        return "jdbc:mysql://" + getHost() + ":" + getMappedPort(SQL_PORT) + "/" + databaseName + additionalUrlParams;
    }

    @Override
    protected String constructUrlForConnection(String queryString) {
        String url = super.constructUrlForConnection(queryString);

        if (!url.contains("useSSL=")) {
            String separator = url.contains("?") ? "&" : "?";
            url = url + separator + "useSSL=false";
        }

        if (!url.contains("allowPublicKeyRetrieval=")) {
            url = url + "&allowPublicKeyRetrieval=true";
        }

        return url;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public StarRocksContainer withDatabaseName(final String databaseName) {
        throw new UnsupportedOperationException("The StarRocks docker image does not currently support this");
    }

    @Override
    public StarRocksContainer withUsername(final String username) {
        throw new UnsupportedOperationException("The StarRocks docker image does not currently support this");
    }

    @Override
    public StarRocksContainer withPassword(final String password) {
        throw new UnsupportedOperationException("The StarRocks docker image does not currently support this");
    }
}
