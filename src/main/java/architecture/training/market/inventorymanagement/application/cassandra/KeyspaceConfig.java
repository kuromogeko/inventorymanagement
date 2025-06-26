package architecture.training.market.inventorymanagement.application.cassandra;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.cql.keyspace.SpecificationBuilder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class KeyspaceConfig extends AbstractCassandraConfiguration implements BeanClassLoaderAware {

    @Override
    protected String getContactPoints() {
        return "localhost";
    }

    @Override
    protected int getPort() {
        return 9042;
    }

    @Override
    protected String getKeyspaceName() {
        return "inventorymanagement";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {

        CreateKeyspaceSpecification specification = SpecificationBuilder.createKeyspace("inventorymanagement")
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true);

        return Arrays.asList(specification);
    }



    // ...
}
