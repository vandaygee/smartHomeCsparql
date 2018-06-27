/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingcsparql;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duchess
 */
public class LBSMARDFStreamTestGenerator extends RdfStream implements Runnable {
    /** The logger. */
	protected final Logger logger = LoggerFactory
			.getLogger(LBSMARDFStreamTestGenerator.class);	

	private int c = 1;
	private int ct = 1;
	private boolean keepRunning = false;
        
        private RdfQuadruple q=null;
       
        float generatedSensorValue =0.0F;
        
        Model m = ModelFactory.createDefaultModel();
        
        public LBSMARDFStreamTestGenerator(final String iri) {
		super(iri);
	}
        
        public void pleaseStop() {
		keepRunning = false;
	}
        
        @Override
	public void run() {

		keepRunning = true;
                 
                Random rnd = new Random();
		while (keepRunning) {
                    if(System.currentTimeMillis()%3==0){
                        generatedSensorValue= 0.0F + new Random().nextFloat() * (22.0F - 0.0F);
                        
                        q = new RdfQuadruple("http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureSensor" + this.c,
					"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#isValueOf", 
                                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                                        String.format("%.2f", generatedSensorValue) ,
                                        System.currentTimeMillis());
                        this.put(q);
                    }else if(System.currentTimeMillis()%5==0){
                        generatedSensorValue= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                        
                        q = new RdfQuadruple("http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureSensor" + this.c,
					"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#isValueOf", 
                                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                                         String.format("%.2f", generatedSensorValue) ,
                                        System.currentTimeMillis());
                        this.put(q);
                        
                        generatedSensorValue= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                        
                        q = new RdfQuadruple("http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureSensor" + this.c,
					"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#isValueOf", 
                                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                                         String.format("%.2f", generatedSensorValue) ,
                                        System.currentTimeMillis());
                        this.put(q);
                    }else{
                        generatedSensorValue= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                        
                        q = new RdfQuadruple("http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureSensor" + this.c,
					"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#isValueOf", 
                                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                                         String.format("%.2f", generatedSensorValue) ,
                                        System.currentTimeMillis());
                        this.put(q);
                    }
               	ct++;

		if(c%10==0) logger.info(ct+ " triples streamed so far");


		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			this.c++;
		}
	}

}
