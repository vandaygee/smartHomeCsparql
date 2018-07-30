/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingcsparql;


import com.hp.hpl.jena.rdf.model.InfModel;
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
    
    public static InfModel infModel;
    
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
        + "PREFIX smartSpace:<http://localhost:8080/smartSpace#> "
	+ "SELECT ?tempReadings ?value "
	+ "FROM STREAM <http://localhost:8080/smartSpace/stream> [RANGE 10s STEP 1s] "
	+ "WHERE { ?tempReadings smartSpace:hasValue ?value}";

	tg = new LBSMARDFStreamTestGenerator("http://localhost:8080/smartSpace/stream");
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
                     System.out.println("\nInferencing...");
                     //displayInferencing(getInfModel());
                     
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
    
    public static void SetInfModel(InfModel infmodel){
        infModel=infmodel;
    }
    
    public static InfModel getInfModel(){
        return infModel;
    }
    
     public static void displayInferencing(InfModel infmodel){
         infmodel.write(System.out, "RDF/XML-ABBREV");
     }
}
