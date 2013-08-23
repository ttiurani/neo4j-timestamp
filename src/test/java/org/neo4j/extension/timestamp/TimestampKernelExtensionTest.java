package org.neo4j.extension.timestamp;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.extension.KernelExtensionFactory;

public class TimestampKernelExtensionTest extends TimestampTestBase {

  @Test
  public void shouldCreateAndUpdateTimestamp() {    
    List<KernelExtensionFactory<?>> extensions = new ArrayList<KernelExtensionFactory<?>>(1); 
    extensions.add(new TimestampKernelExtensionFactory(true));
    GraphDatabaseService graphdb = new GraphDatabaseFactory()
        .addKernelExtensions(extensions)
        .newEmbeddedDatabaseBuilder(TEST_DATA_STORE_DESTINATION)
        .newGraphDatabase();

    Node createdNode = super.checkTimestampCreation(graphdb);
    super.checkTimestampUpdateOnPropertyAdd(graphdb, createdNode);
    super.checkTimestampUpdateOnRelationshipAdd(graphdb, createdNode);
  }
}
