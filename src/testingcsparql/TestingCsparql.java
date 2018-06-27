/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingcsparql;

import com.sun.xml.internal.ws.api.pipe.Engine;
import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingCsparql {

    private static Logger logger = LoggerFactory.getLogger(TestingCsparql.class);
    
    public static void main(String[] args) {
        // TODO code application logic here
        try {
                PropertyConfigurator.configure(new URL("C:\\Users\\Duchess\\Documents\\SmartSUM\\CSPARQL\\config_files/csparql_readyToGoPack_log4j.properties"));
	} catch (MalformedURLException e) {
		logger.error(e.getMessage(), e);
	}
        
        String query = null;
	String queryDownStream = null;
	RdfStream tg = null;
	RdfStream anotherTg = null;
        
        // Initialize C-SPARQL Engine
	CsparqlEngine engine = new CsparqlEngineImpl();
        
        engine.initialize(true);
        
        //logger.debug("WHO_LIKES_WHAT example");
        
        query = "REGISTER QUERY sensorValueOf AS "
        + "PREFIX :<http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#> "
	+ "SELECT ?temperatureSensor ?temperatureValue "
	+ "FROM STREAM <http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21/stream> [RANGE 10s STEP 1s] "
	+ "WHERE { ?temperatureSensor :isValueOf ?temperatureValue}";

	tg = new LBSMARDFStreamTestGenerator("http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21/stream");
			//			tg = new BasicIntegerRDFStreamTestGenerator("http://myexample.org/stream");
                        
        // Register an RDF Stream
        engine.registerStream(tg);
        
        final Thread t = new Thread((Runnable) tg);
	t.start();
        
        CsparqlQueryResultProxy c1 = null;
        
        try {
            c1 = engine.registerQuery(query, false);
            logger.debug("Query: {}", query);
            logger.debug("Query Start Time : {}", System.currentTimeMillis());
        } catch (final ParseException ex) {
            logger.error(ex.getMessage(), ex);
	}
        
        if (c1 != null) {
            c1.addObserver( new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                     final RDFTable rdfTable = (RDFTable) arg;
                     //System.out.println(rdfTable.toString());
                     final String[] vars = rdfTable.getNames().toArray(new String[]{});
                     Date date = new Date(System.currentTimeMillis()); //Input your time in milliseconds
                     String dateString = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(date);
                     System.out.println("----"+rdfTable.size()+" result at SystemTime=["+dateString+"]-----");
                     rdfTable.stream().forEach((t) -> {
                         System.out.println(t.toString());
            });
                     System.out.println("");
                     
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            }
                    
            );
            
            //c1.addObserver(new ConsoleFormatter());
	}
        
        try {
            Thread.sleep(200000);
	} catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
	}
        
        // clean up (i.e., unregister query and stream)
	engine.unregisterQuery(c1.getId());
	((LBSMARDFStreamTestGenerator) tg).pleaseStop();
	engine.unregisterStream(tg.getIRI());
        
        System.exit(0);
    }
    
    public static void testRemoteStreamer(){
        try{
            //Configure log4j logger for the csparql engine
             PropertyConfigurator.configure(new URL("C:\\Users\\Duchess\\Documents\\SmartSUM\\CSPARQL\\config_files/csparql_readyToGoPack_log4j.properties"));
            
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }
 
}
