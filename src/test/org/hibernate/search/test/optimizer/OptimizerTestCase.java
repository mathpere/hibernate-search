//$Id$
package org.hibernate.search.test.optimizer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.Environment;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.impl.FullTextSessionImpl;
import org.hibernate.search.store.FSDirectoryProvider;
import org.hibernate.search.test.SearchTestCase;

/**
 * @author Emmanuel Bernard
 */
public class OptimizerTestCase extends SearchTestCase {
	protected void setUp() throws Exception {
		File sub = getBaseIndexDir();
		delete( sub );
		sub.mkdir();
		File[] files = sub.listFiles();
		for (File file : files) {
			if ( file.isDirectory() ) {
				delete( file );
			}
		}
		//super.setUp(); //we need a fresh session factory each time for index set up
		buildSessionFactory( getMappings(), getAnnotatedPackages(), getXmlFiles() );
	}

	private File getBaseIndexDir() {
		File current = new File( "." );
		File sub = new File( current, "indextemp" );
		return sub;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		File sub = getBaseIndexDir();
		delete( sub );
	}

	private void delete(File sub) {
		if ( sub.isDirectory() ) {
			for (File file : sub.listFiles()) {
				delete( file );
			}
			sub.delete();
		}
		else {
			sub.delete();
		}
	}

	public void testConcurrency() throws Exception {
		int nThreads = 15;
		ExecutorService es = Executors.newFixedThreadPool( nThreads );
		Work work = new Work( getSessions() );
		ReverseWork reverseWork = new ReverseWork( getSessions() );
		long start = System.currentTimeMillis();
		int iteration = 100;
		for (int i = 0; i < iteration; i++) {
			es.execute( work );
			es.execute( reverseWork );
		}
		while ( work.count < iteration - 1 ) {
			Thread.sleep( 20 );
		}
		System.out.println( iteration + " iterations (8 tx per iteration) in " + nThreads + " threads: " + ( System
				.currentTimeMillis() - start ) );
	}

	protected class Work implements Runnable {
		private SessionFactory sf;
		public volatile int count = 0;

		public Work(SessionFactory sf) {
			this.sf = sf;
		}

		public void run() {
			try {
				Session s = sf.openSession();
				Transaction tx = s.beginTransaction();
				Worker w = new Worker( "Emmanuel", 65 );
				s.persist( w );
				Construction c = new Construction( "Bellagio", "Las Vagas Nevada" );
				s.persist( c );
				tx.commit();
				s.close();

				s = sf.openSession();
				tx = s.beginTransaction();
				w = (Worker) s.get( Worker.class, w.getId() );
				w.setName( "Gavin" );
				c = (Construction) s.get( Construction.class, c.getId() );
				c.setName( "W Hotel" );
				tx.commit();
				s.close();

				try {
					Thread.sleep( 50 );
				}
				catch (InterruptedException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}

				s = sf.openSession();
				tx = s.beginTransaction();
				FullTextSession fts = new FullTextSessionImpl( s );
				QueryParser parser = new QueryParser( "id", new StopAnalyzer() );
				Query query;
				try {
					query = parser.parse( "name:Gavin" );
				}
				catch (ParseException e) {
					throw new RuntimeException( e );
				}
				boolean results = fts.createFullTextQuery( query ).list().size() > 0;
				//don't test because in case of async, it query happens before actual saving
				//if ( !results ) throw new RuntimeException( "No results!" );
				tx.commit();
				s.close();

				s = sf.openSession();
				tx = s.beginTransaction();
				w = (Worker) s.get( Worker.class, w.getId() );
				s.delete( w );
				c = (Construction) s.get( Construction.class, c.getId() );
				s.delete( c );
				tx.commit();
				s.close();
				count++;
			} catch (Throwable t) {
				t.printStackTrace( );
			}
		}
	}

	protected class ReverseWork implements Runnable {
		private SessionFactory sf;

		public ReverseWork(SessionFactory sf) {
			this.sf = sf;
		}

		public void run() {
			try {
				Session s = sf.openSession();
				Transaction tx = s.beginTransaction();
				Worker w = new Worker( "Mladen", 70 );
				s.persist( w );
				Construction c = new Construction( "Hover Dam", "Croatia" );
				s.persist( c );
				tx.commit();
				s.close();

				s = sf.openSession();
				tx = s.beginTransaction();
				w = (Worker) s.get( Worker.class, w.getId() );
				w.setName( "Remi" );
				c = (Construction) s.get( Construction.class, c.getId() );
				c.setName( "Palais des festivals" );
				tx.commit();
				s.close();

				s = sf.openSession();
				tx = s.beginTransaction();
				w = (Worker) s.get( Worker.class, w.getId() );
				s.delete( w );
				c = (Construction) s.get( Construction.class, c.getId() );
				s.delete( c );
				tx.commit();
				s.close();
			} catch (Throwable t) {
				t.printStackTrace( );
			}
		}
	}

	protected void configure(org.hibernate.cfg.Configuration cfg) {
		super.configure( cfg );
		File sub = getBaseIndexDir();
		cfg.setProperty( "hibernate.search.default.indexBase", sub.getAbsolutePath() );
		cfg.setProperty( "hibernate.search.default.directory_provider", FSDirectoryProvider.class.getName() );
		cfg.setProperty( Environment.ANALYZER_CLASS, StopAnalyzer.class.getName() );
	}

	protected Class[] getMappings() {
		return new Class[] {
				Worker.class,
				Construction.class
		};
	}
}