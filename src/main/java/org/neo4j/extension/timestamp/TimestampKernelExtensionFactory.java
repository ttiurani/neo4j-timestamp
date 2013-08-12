package org.neo4j.extension.timestamp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.lifecycle.Lifecycle;

public class TimestampKernelExtensionFactory extends KernelExtensionFactory<TimestampKernelExtensionFactory.Dependencies>
{
    public interface Dependencies{
        GraphDatabaseService getDatabase();
    }

    public TimestampKernelExtensionFactory(){
        super( "timestamp" );
    }

    @Override
    public Lifecycle newKernelExtension( Dependencies dependencies ) throws Throwable {
        return new TimestampKernelExtension(dependencies.getDatabase());
    }
}
