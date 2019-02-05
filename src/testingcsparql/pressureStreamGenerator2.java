
package testingcsparql;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class pressureStreamGenerator2 extends RdfStream implements Runnable {
    
    protected final Logger logger = LoggerFactory.getLogger(pressureStreamGenerator.class);	
    private int c = 1;
    private boolean keepRunning = false;
     
    private RdfQuadruple q=null;
     
    float generatedPressure=0.0F;
    long generatedTime=0L;
  
      
    public pressureStreamGenerator2(final String iri){
        super(iri);
    }
    
    @Override
    public void run() {
	keepRunning = true;
        Random rnd = new Random();
       
        while (keepRunning) {
            //Instant instant=Instant.now();
            generatedTime=System.currentTimeMillis();
            generatedPressure=750.0F + rnd.nextFloat()* (762.0F - 750.0F);
           
            q = new RdfQuadruple("http://localhost:8080/smartSpace#pressure2Reading" + this.c,
			"http://localhost:8080/smartSpace#hasPressureReading", 
                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                        String.format("%.2f", generatedPressure) ,
                        generatedTime);
                this.put(q);
            try {
                Thread.sleep(1*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.c++; 
        }
    }
    
}
