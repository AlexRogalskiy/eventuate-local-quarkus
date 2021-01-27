package io.eventuate.local.java.quarkus.jdbc.crud;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.javaclient.jdbc.EventuateJdbcAccess;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.javaclient.commonimpl.crud.AggregateCrud;
import io.eventuate.javaclient.commonimpl.crud.adapters.SyncToAsyncAggregateCrudAdapter;
import io.eventuate.local.java.crud.EventuateLocalAggregateCrud;
import io.eventuate.local.java.crud.EventuateLocalJdbcAccess;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Singleton
public class EventuateLocalCrudConfiguration {
  @Singleton
  public EventuateJdbcAccess eventuateJdbcAccess(IdGenerator idGenerator,
                                                 EventuateTransactionTemplate eventuateTransactionTemplate,
                                                 EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                                 EventuateCommonJdbcOperations eventuateCommonJdbcOperations,
                                                 EventuateSchema eventuateSchema) {
    return new EventuateLocalJdbcAccess(idGenerator, eventuateTransactionTemplate, eventuateJdbcStatementExecutor, eventuateCommonJdbcOperations, eventuateSchema);
  }

  @Singleton
  public EventuateLocalAggregateCrud eventuateLocalAggregateCrud(TransactionTemplate transactionTemplate,
                                                                 EventuateJdbcAccess eventuateJdbcAccess) {
    return new EventuateLocalAggregateCrud(transactionTemplate, eventuateJdbcAccess);
  }

  @Singleton
  public AggregateCrud asyncAggregateCrud(io.eventuate.javaclient.commonimpl.crud.sync.AggregateCrud aggregateCrud) {
    return new SyncToAsyncAggregateCrudAdapter(aggregateCrud);
  }

  @Singleton
  public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
    return new TransactionTemplate(platformTransactionManager);
  }

  @Singleton
  @Default
  public PlatformTransactionManager platformTransactionManager(Instance<DataSource> dataSource) {
    return new DataSourceTransactionManager(dataSource.get());
  }
}
