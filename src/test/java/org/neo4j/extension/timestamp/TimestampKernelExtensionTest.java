package org.neo4j.extension.timestamp;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.extension.KernelExtensionFactory;

public class TimestampKernelExtensionTest extends TimestampTestBase {

  @Test
  public void shouldCreateAndUpdateTimestamp() {    
    List<KernelExtensionFactory<?>> extensions = new ArrayList<KernelExtensionFactory<?>>(1); 
    extensions.add(new TimestampKernelExtensionFactory(true, false));
    GraphDatabaseService graphdb = new GraphDatabaseFactory()
        .addKernelExtensions(extensions)
        .newEmbeddedDatabaseBuilder(TEST_DATA_STORE_DESTINATION)
        .newGraphDatabase();

    long createdNodeId = super.checkTimestampCreation(graphdb);
    super.checkTimestampUpdateOnPropertyAdd(graphdb, createdNodeId);
    super.checkTimestampUpdateOnRelationshipAdd(graphdb, createdNodeId);
  }
}
