package org.neo4j.extension.timestamp;

import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;

public class TimestampKernelExtension extends LifecycleAdapter{
  
  private final GraphDatabaseService gdb;
  private boolean setupAutoIndexing = false;
  private boolean addCreated = false;
  private List<TimestampCustomPropertyHandler> customPropertyHandlers = null;


  public TimestampKernelExtension(GraphDatabaseService gdb,
		  						  boolean setupAutoIndexing,
		  						  boolean addCreated,
		  						  List<TimestampCustomPropertyHandler> customPropertyHandlers){
    this.gdb = gdb;
    this.setupAutoIndexing = setupAutoIndexing;
    this.addCreated = addCreated;
    this.customPropertyHandlers = customPropertyHandlers;
  }

  @Override
  public void start() throws Throwable{
    IndexManager indexManager = this.gdb.index();
    if(setupAutoIndexing){
      setupTimestampIndexing(indexManager.getNodeAutoIndexer());
      setupTimestampIndexing(indexManager.getRelationshipAutoIndexer());
    }
    this.gdb.registerTransactionEventHandler(new TimestampTransactionEventHandler<String>(this.addCreated, this.customPropertyHandlers));
  }

  void setupTimestampIndexing(AutoIndexer<? extends PropertyContainer> autoIndexer) {
    autoIndexer.startAutoIndexingProperty(TimestampTransactionEventHandler.MODIFIED_TIMESTAMP_PROPERTY_NAME);
    if (this.addCreated){
      autoIndexer.startAutoIndexingProperty(TimestampTransactionEventHandler.CREATED_TIMESTAMP_PROPERTY_NAME);    	
    }
    autoIndexer.setEnabled(true);
  }
}
