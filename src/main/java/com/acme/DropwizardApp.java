package com.acme;

import com.acme.core.Person;
import com.acme.db.PersonDAO;
import com.acme.db.UserDAO;
import com.acme.health.TemplateHealthCheck;
import com.acme.resources.HelloWorldResource;
import com.acme.resources.PersonResource;
import com.acme.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;

public class DropwizardApp extends Application<DropwizardAppConfiguration> {

    private final HibernateBundle<DropwizardAppConfiguration> hibernate = new HibernateBundle<DropwizardAppConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardAppConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final MigrationsBundle<DropwizardAppConfiguration> migrations = new MigrationsBundle<DropwizardAppConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardAppConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final FlywayBundle<DropwizardAppConfiguration> flyway = new FlywayBundle<DropwizardAppConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardAppConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new DropwizardApp().run(args);
    }

    @Override
    public String getName() {
        return "Dropwizard Example App";
    }

    @Override
    public void initialize(final Bootstrap<DropwizardAppConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(migrations);
        // Configure Flyway
        // https://github.com/dropwizard/dropwizard-flyway
        // bootstrap.addBundle(flyway);
    }

    @Override
    public void run(final DropwizardAppConfiguration configuration,
                    final Environment environment) {
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final PersonDAO personDAO = new PersonDAO(hibernate.getSessionFactory());
        environment.jersey().register(new PersonResource(personDAO));

        // Configure JDBI
        // https://github.com/stevenalexander/dropwizard-jdbi
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "oracle");
        final UserDAO dao = jdbi.onDemand(UserDAO.class);
        environment.jersey().register(new UserResource(dao));
    }

}
