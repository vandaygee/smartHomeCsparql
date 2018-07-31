/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingcsparql;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.time.Instant;
import java.util.List;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duchess
 */
public class LBSMARDFStreamTestGenerator extends RdfStream implements Runnable {
    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(LBSMARDFStreamTestGenerator.class);	
    private int c = 1;
    private int ct = 1;
    private boolean keepRunning = false;
        
    private RdfQuadruple q=null;
    private RdfQuadruple q2=null;
       
    float generatedSensorValue =0.0F;
    long generatedTime=0L;
    float generatedPressure=0.0F;
    int generatedHumidity= 0;
        
        //Model m = ModelFactory.createDefaultModel();
    OntModel accumulatedModel=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
        
    private static final String BASE="http://localhost:8080/smartSpace#";
        
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
        OntClass tempValue=accumulatedModel.createClass(BASE+"tempValue");
        OntClass humidityValue=accumulatedModel.createClass(BASE+"humidityValue");
        OntClass pressureValue=accumulatedModel.createClass(BASE+"pressureValue");
        
        Individual tempReadings;
        Individual humidityReadings;
        Individual pressureReading;
        
        while (keepRunning) {
            OntModel streamingModel=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
            
            OntClass streamingtempValue=streamingModel.createClass(BASE+"tempValue");
            OntClass streaminghumidityValue=streamingModel.createClass(BASE+"humidityValue");
            OntClass streamingpressureValue=streamingModel.createClass(BASE+"pressureValue");
            
            Individual streamingtempReadings;
            Individual streaminghumidityReadings;
            Individual streamingpressureReading;
            
            Instant instant=Instant.now();
            generatedTime=System.currentTimeMillis();
            generatedPressure=750.0F + new Random().nextFloat()* (762.0F - 750.0F);
            generatedHumidity= 30 + (int)(Math.random()*(60-30)+1);
            
//            q = new RdfQuadruple("http://localhost:8080/smartSpace#pressureReading" + this.c,
//			"http://localhost:8080/smartSpace#hasPressureReading", 
//                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
//                        String.format("%.2f", generatedPressure) ,
//                        generatedTime);
//                this.put(q);
//                 
            pressureReading=pressureValue.createIndividual(BASE+"pressureReading"+this.c);
            pressureReading.addProperty(p("hasPressureReading"),l1(roundOffTo2DecPlaces(generatedPressure),XSDDatatype.XSDfloat));
            pressureReading.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
            
            humidityReadings=humidityValue.createIndividual(BASE+"humidityReading"+this.c);
            humidityReadings.addProperty(p("hasHumidityReading"),l1(String.valueOf(generatedHumidity),XSDDatatype.XSDinteger));
            humidityReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
            
            streamingpressureReading=pressureValue.createIndividual(BASE+"pressureReading"+this.c);
            streamingpressureReading.addProperty(p("hasPressureReading"),l1(roundOffTo2DecPlaces(generatedPressure),XSDDatatype.XSDfloat));
            streamingpressureReading.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
            
            humidityReadings=humidityValue.createIndividual(BASE+"humidityReading"+this.c);
            humidityReadings.addProperty(p("hasHumidityReading"),l1(String.valueOf(generatedHumidity),XSDDatatype.XSDinteger));
            humidityReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));

            if(System.currentTimeMillis()%3==0){
                generatedSensorValue= 0.0F + new Random().nextFloat() * (22.0F - 0.0F);
                        
                q = new RdfQuadruple("http://localhost:8080/smartSpace#tempReadings" + this.c,
			"http://localhost:8080/smartSpace#hasValue", 
                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                        String.format("%.2f", generatedSensorValue) ,
                        generatedTime);
                this.put(q);
                       
                tempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                tempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                tempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                
                streamingtempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                streamingtempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                streamingtempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
            }else if(System.currentTimeMillis()%5==0){
                generatedSensorValue= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                q = new RdfQuadruple("http://localhost:8080/smartSpace#tempReadings" + this.c,
			"http://localhost:8080/smartSpace#hasValue", 
                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                        String.format("%.2f", generatedSensorValue) ,
                        System.currentTimeMillis());
                this.put(q);
                tempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                tempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                tempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                
                streamingtempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                streamingtempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                streamingtempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                        
                generatedSensorValue= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                        
                q = new RdfQuadruple("http://localhost:8080/smartSpace#tempReadings" + this.c,
			"http://localhost:8080/smartSpace#hasValue", 
                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                        String.format("%.2f", generatedSensorValue) ,
                        System.currentTimeMillis());
                this.put(q);
                tempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                tempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                tempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                
                streamingtempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                streamingtempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                streamingtempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
            }else{
                generatedSensorValue= 23.0F + new Random().nextFloat() * (28.0F - 23.0F);
                        
                q = new RdfQuadruple("http://localhost:8080/smartSpace#tempReadings" + this.c,
			"http://localhost:8080/smartSpace#hasValue", 
                        //"http://www.semanticweb.org/40011133/ontologies/2017/10/untitled-ontology-21#temperatureValue" + this.c,
                        String.format("%.2f", generatedSensorValue) ,
                        System.currentTimeMillis());
                this.put(q);
                tempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                tempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                tempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                
                streamingtempReadings=tempValue.createIndividual(BASE+"tempReadings"+this.c);
                streamingtempReadings.addProperty(p("hasValue"),l1(roundOffTo2DecPlaces(generatedSensorValue),XSDDatatype.XSDfloat));
                streamingtempReadings.addProperty(p("hasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
            }
            ct++;

            if(c%10==0) logger.info(ct+ " triples streamed so far");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.c++;
            
            try{
//                accumulatedModel.setNsPrefix("smartSpace", BASE); 
//                streamingModel.setNsPrefix("smartSpace", BASE);
//                //model.write(System.out, "RDF/XML"); 
//                String saveRDFFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\accumulatedData.rdf";
//                String saveStreamRDFFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamData.rdf";
//                
//                OutputStream output = new FileOutputStream(saveRDFFile);
//                RDFDataMgr.write(output, accumulatedModel, RDFFormat.RDFXML_ABBREV);
//                
//                output=new FileOutputStream(saveStreamRDFFile);
//                RDFDataMgr.write(output, accumulatedModel, RDFFormat.RDFXML_ABBREV);
//                System.out.println("Data written successfully into RDF. Open at: "+saveRDFFile);
//                
//                String rdfRule="C:\\Users\\user\\Documents\\SmartSUM\\rules\\error.txt";
//                String rdfFile= "C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamData.rdf";
//                
//                runEngine(rdfRule, rdfFile);
                
            }catch(Exception e){
                System.out.println(e.toString());
            }
            
        }
    }
        
    private static Property p ( String localname ){
        return ResourceFactory.createProperty ( BASE, localname );
    }

    private static Literal l ( Object value ) {
        return ResourceFactory.createTypedLiteral ( value );
    }

    private static Literal l1 ( String lexicalform, RDFDatatype datatype ) {
        return ResourceFactory.createTypedLiteral(lexicalform, datatype);
    }
    
    private static String roundOffTo2DecPlaces(float val){
        return String.format("%.2f", val);
    }
    
    private static void runEngine(String ruleFile,String rdfFile) throws Exception{
        Model model = ModelFactory.createDefaultModel();
        InputStream inschema =new FileInputStream(rdfFile);
        model.read(inschema, null, "RDF/XML");
        List rules=Rule.rulesFromURL(ruleFile);
        System.out.println("\nMy rules:\n"+rules);
        Reasoner reasoner=new GenericRuleReasoner(rules);
        //reasoner=reasoner.bindSchema(model);
        InfModel infmodel=ModelFactory.createInfModel(reasoner, model);
        Resource children = infmodel.getResource("http://localhost:8080/smartSpace#hasValue");
        //infmodel.write(System.out, "RDF/XML-ABBREV");
        TestingCsparql.SetInfModel(infmodel);
//        StmtIterator it=infmodel.listStatements();
//        while(it.hasNext()){
//            Statement stmt=it.nextStatement();
//            Resource subject = stmt.getSubject();
//            Property predicate = stmt.getPredicate();
//            RDFNode object = stmt.getObject(); 
//            System.out.println( subject.toString() + " " + predicate.toString() + " " + object.toString() );
        //}
    }


}
