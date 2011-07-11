package de.escidoc.core.test.migration;

import de.escidoc.core.test.common.resources.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

public class DbAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbAccess.class);

    public static final String ESCIDOC_DATABASE_URL = "escidoc.database.url";

    public static final String ESCIDOC_DATABASE_PASSWORD = "escidoc.database.password";

    public static final String ESCIDOC_DATABASE_USERNAME = "escidoc.database.username";

    public static final String ESCIDOC_DATABASE_DRIVER_CLASS_NAME = "escidoc.database.driverClassName";

    private static final String USER_ACCOUNT_ID_QUERY = "select id from aa.user_account";

    private static final String ROLE_ID_QUERY = "select id from aa.escidoc_role";

    private static Properties PROPERTIES = null;

    /**
     */
    public DbAccess() {
        if (PROPERTIES == null) {
            PROPERTIES = loadProperties();
        }
    }

    public Collection<String> retrieveUserAccountIds() throws Exception {

        return executeQuery(USER_ACCOUNT_ID_QUERY);
    }

    public Collection<String> retrieveRoleIds() throws Exception {

        return executeQuery(ROLE_ID_QUERY);
    }

    private Collection<String> executeQuery(final String query) throws SQLException, ClassNotFoundException {
        Collection<String> result = new Vector<String>();
        Connection con = getConnection();
        try {
            PreparedStatement state = con.prepareStatement(query);
            ResultSet resultSet = state.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(getDriverClassName());
        return DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
    }

    private String getJdbcUrl() {
        if (PROPERTIES == null) {
            PROPERTIES = loadProperties();
        }
        return PROPERTIES.getProperty(DbAccess.ESCIDOC_DATABASE_URL);
    }

    private String getDriverClassName() {
        if (PROPERTIES == null) {
            PROPERTIES = loadProperties();
        }
        return PROPERTIES.getProperty(DbAccess.ESCIDOC_DATABASE_DRIVER_CLASS_NAME);
    }

    private String getUsername() {
        if (PROPERTIES == null) {
            PROPERTIES = loadProperties();
        }
        return PROPERTIES.getProperty(DbAccess.ESCIDOC_DATABASE_USERNAME);
    }

    private String getPassword() {
        if (PROPERTIES == null) {
            PROPERTIES = loadProperties();
        }
        return PROPERTIES.getProperty(DbAccess.ESCIDOC_DATABASE_PASSWORD);
    }

    /**
     * Load properties file to influence test behavior.
     *
     * @return properties
     */
    private Properties loadProperties() {
        InputStream fis = null;
        Properties properties = new Properties();
        String propertiesFile = "escidoc.properties";

        try {
            try {
                fis = ResourceProvider.getFileInputStreamFromFile("./etc", propertiesFile);
            }
            catch (final IOException e) {
                e.printStackTrace();
                if (fis == null) {
                    fis = ResourceProvider.getFileInputStreamFromFile("../../etc", propertiesFile);
                }
            }
            if (fis != null) {
                properties.load(fis);
                fis.close();
            }
        }
        catch (final IOException e) {
            LOGGER.warn("", e);
            throw new RuntimeException(e);
        }

        return (properties);
    }
}
