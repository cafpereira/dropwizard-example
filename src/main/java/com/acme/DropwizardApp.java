package com.acme;

import com.acme.core.Person;
import com.acme.db.PersonDAO;
import com.acme.health.TemplateHealthCheck;
import com.acme.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
    }

    @Override
    public void run(final DropwizardAppConfiguration configuration,
                    final Environment environment) {
        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

        final PersonDAO personDAO = new PersonDAO(hibernate.getSessionFactory());
//        environment.jersey().register(new UserResource(personDAO));
    }

}
