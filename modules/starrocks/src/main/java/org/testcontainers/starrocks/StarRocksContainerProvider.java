package org.testcontainers.starrocks;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.JdbcDatabaseContainerProvider;
import org.testcontainers.utility.DockerImageName;

/**
 * Factory for StarRocks containers.
 */
public class StarRocksContainerProvider extends JdbcDatabaseContainerProvider {

    private static final String DEFAULT_TAG = "3.1.0";

    @Override
    public boolean supports(String databaseType) {
        return databaseType.equals(StarRocksContainer.NAME);
    }

    @Override
    public JdbcDatabaseContainer newInstance() {
        return newInstance(DEFAULT_TAG);
    }

    @Override
    public JdbcDatabaseContainer newInstance(String tag) {
        if (tag != null) {
            return new StarRocksContainer(DockerImageName.parse(StarRocksContainer.DOCKER_IMAGE_NAME).withTag(tag));
        } else {
            return newInstance();
        }
    }
}
