package org.testcontainers.junit.starrocks;

import org.junit.Test;
import org.testcontainers.StarRocksTestImages;
import org.testcontainers.db.AbstractContainerDatabaseTest;
import org.testcontainers.starrocks.StarRocksContainer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleStarRocksTest extends AbstractContainerDatabaseTest {

    @Test
    public void testSimple() throws SQLException {
        try (StarRocksContainer starrocks = new StarRocksContainer(StarRocksTestImages.StarRocks_IMAGE)) {
            starrocks.start();

            ResultSet resultSet = performQuery(starrocks, "SELECT 1");

            int resultSetInt = resultSet.getInt(1);
            assertThat(resultSetInt).isEqualTo(1);
            assertHasCorrectExposedAndLivenessCheckPorts(starrocks);
        }
    }

    @Test
    public void testExplicitInitScript() throws SQLException {
        try (
            StarRocksContainer starrocks = new StarRocksContainer(StarRocksTestImages.StarRocks_IMAGE)
                .withInitScript("somepath/init_starrocks.sql")
        ) { // StarRocks is expected to be compatible with MySQL
            starrocks.start();

            ResultSet resultSet = performQuery(starrocks, "SELECT foo FROM bar");

            String firstColumnValue = resultSet.getString(1);
            assertThat(firstColumnValue).isEqualTo("hello world");
        }
    }

    @Test
    public void testWithAdditionalUrlParamInJdbcUrl() {
        StarRocksContainer starrocks = new StarRocksContainer(StarRocksTestImages.StarRocks_IMAGE)
            .withUrlParam("sslmode", "disable");

        try {
            starrocks.start();
            String jdbcUrl = starrocks.getJdbcUrl();
            assertThat(jdbcUrl).contains("?");
            assertThat(jdbcUrl).contains("sslmode=disable");
        } finally {
            starrocks.stop();
        }
    }

    private void assertHasCorrectExposedAndLivenessCheckPorts(StarRocksContainer starrocks) {
        Integer sqlPort = 9030;
        Integer restApiPort = 8040;

        assertThat(starrocks.getExposedPorts()).containsExactlyInAnyOrder(sqlPort, restApiPort);
        assertThat(starrocks.getLivenessCheckPortNumbers())
            .containsExactlyInAnyOrder(starrocks.getMappedPort(sqlPort), starrocks.getMappedPort(restApiPort));
    }
}
