//$Id$
package org.hibernate.search.store;

import java.util.Properties;

import org.apache.lucene.store.Directory;
import org.hibernate.search.engine.SearchFactoryImplementor;


/**
 * Set up and provide a Lucene <code>Directory</code>
 * <code>equals()</code> and <code>hashCode()</code> must guaranty equality
 * between two providers pointing to the same underlying Lucene Store.
 * Besides that, hashCode ordering is used to avoid deadlock when locking a directory provider.
 * 
 * This class must be thread safe regarding <code>getDirectory()</code> calls
 *
 * @author Emmanuel Bernard
 * @author Sylvain Vieujot
 */
public interface DirectoryProvider<TDirectory extends Directory> {
	/**
	 * get the information to initialize the directory and build its hashCode/equals method
	 */
	void initialize(String directoryProviderName, Properties properties, SearchFactoryImplementor searchFactoryImplementor);

	/**
	 * Executed after initialize, this method set up the heavy process of starting up the DirectoryProvider
	 * IO processing as well as backgroup processing are expected to be set up here
	 *
	 * TODO stop() method, for now use finalize() 
	 */
	void start();

	/**
	 * Returns an initialized Lucene Directory. This method call <b>must</b> be threadsafe
	 */
	TDirectory getDirectory();
}

