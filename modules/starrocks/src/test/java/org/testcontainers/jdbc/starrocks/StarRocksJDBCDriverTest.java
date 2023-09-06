package org.testcontainers.jdbc.starrocks;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testcontainers.jdbc.AbstractJDBCDriverTest;

import java.util.Arrays;
import java.util.EnumSet;

@RunWith(Parameterized.class)
public class StarRocksJDBCDriverTest extends AbstractJDBCDriverTest {

    @Parameterized.Parameters(name = "{index} - {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            // mysql://hostname/databasename or mysql://hostname/catalog.databasename
            new Object[][] { { "jdbc:tc:mysql://hostname/databasename", EnumSet.noneOf(Options.class) } }
        );
    }
}
