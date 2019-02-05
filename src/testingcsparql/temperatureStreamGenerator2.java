/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingcsparql;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class temperatureStreamGenerator2 extends RdfStream implements Runnable  {
    protected final Logger logger = LoggerFactory.getLogger(temperatureStreamGenerator.class);	
    private int c = 1;
    private boolean keepRunning = false;
     
    private RdfQuadruple q=null;
     
    float generatedTemperature=0.0F;
    long generatedTime=0L;
    int timeCount=0;
    
    public temperatureStreamGenerator2(final String iri){
        super(iri);
    }
    
    @Override
    public void run() {
	keepRunning = true;
        Random rnd = new Random();
       
        while (keepRunning) {
            //Instant instant=Instant.now();
            timeCount+=1;
            if(timeCount <5){
               // generatedTemperature= 19.0F + new Random().nextFloat() * (21.0F - 19.0F);
               generatedTemperature= -25.2F +new Random().nextFloat() * (19.7F - (-25.5F));
                        
                q = new RdfQuadruple("http://localhost:8080/smartSpace#temp2Readings" + this.c,
			"http://localhost:8080/smartSpace#hasValue", 
                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                        String.format("%.2f", generatedTemperature) ,
                        System.currentTimeMillis());
                this.put(q);
               
            }else{
                timeCount=0;
                generatedTime=System.currentTimeMillis();
                if(System.currentTimeMillis()%3==0){
                    generatedTemperature= 0.0F + rnd.nextFloat() * (22.0F - 0.0F);

                    q = new RdfQuadruple("http://localhost:8080/smartSpace#temp2Readings" + this.c,
                            "http://localhost:8080/smartSpace#hasValue", 
                            //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                            //String.format("%.2f", generatedTemperature) ,
                            "27.44",
                            generatedTime);
                    this.put(q);

                }else if(System.currentTimeMillis()%5==0){
                    generatedTemperature= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                    q = new RdfQuadruple("http://localhost:8080/smartSpace#temp2Readings" + this.c,
                            "http://localhost:8080/smartSpace#hasValue", 
                            //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                            String.format("%.2f", generatedTemperature) ,
                            System.currentTimeMillis());
                    this.put(q);

                    generatedTemperature= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);

                    q = new RdfQuadruple("http://localhost:8080/smartSpace#temp2Readings" + this.c,
                            "http://localhost:8080/smartSpace#hasValue", 
                            //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                            String.format("%.2f", generatedTemperature) ,
                            System.currentTimeMillis());
                    this.put(q);
                }else if(System.currentTimeMillis()%7==0){
                    generatedTemperature= -25.2F +new Random().nextFloat() * (38.5F - (-25.5F));

                    q = new RdfQuadruple("http://localhost:8080/smartSpace#temp2Readings" + this.c,
                            "http://localhost:8080/smartSpace#hasValue", 
                            //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                            String.format("%.2f", generatedTemperature) ,
                            generatedTime);
                    this.put(q);

                }    
            }
            
            try {
                Thread.sleep(1*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.c++;
        }
    }
    
}
