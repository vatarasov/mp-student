package ru.vtarasov.mp.student;

import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.inject.Singleton;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author vtarasov
 * @since 06.10.2019
 */
@Startup
@Singleton
public class StudentRepositoryMigrator {
    @Resource(lookup = "jdbc/jpadatasource")
    private DataSource dataSource;

    @PostConstruct
    public void setUp() {
        try (Connection conn = dataSource.getConnection()) {
            Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liq = new Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), db);
            liq.update("main");
        } catch (SQLException e) {
            handleException(e);
        } catch (DatabaseException e) {
            handleException(e);
        } catch (LiquibaseException e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}
