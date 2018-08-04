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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingCsparql {

    private static Logger logger = LoggerFactory.getLogger(TestingCsparql.class);
    
    public static InfModel infModel;
    private static final String BASE="http://localhost:8080/smartSpace#";
    static String analysisText="";
    
    public static void main(String[] args) {
        // TODO code application logic here
        try {
                PropertyConfigurator.configure(new URL("C:\\Users\\Duchess\\Documents\\SmartSUM\\CSPARQL\\config_files/csparql_readyToGoPack_log4j.properties"));
	} catch (MalformedURLException e) {
		logger.error(e.getMessage(), e);
	}
        
        String query = null;
       
	RdfStream tg = null;
        RdfStream tg2=null;
        RdfStream tg3=null;
        
        JSONObject JSONAnalysis=new JSONObject();
       
        // Initialize C-SPARQL Engine
	CsparqlEngine engine = new CsparqlEngineImpl();
        
        engine.initialize(true);
        
        query = "REGISTER QUERY sensorValueOf AS "
        + "PREFIX smartSpace:<http://localhost:8080/smartSpace#> "
	+ "SELECT ?tempReadings ?tempValue ?pressureReadings ?pressureValue ?humidityReadings ?humidityValue "
	+ "FROM STREAM <http://localhost:8080/smartSpace/streamTemperature> [RANGE 10s STEP 1s] "
        + "FROM STREAM <http://localhost:8080/smartSpace/streamPressure> [RANGE 10s STEP 1s] "
        + "FROM STREAM <http://localhost:8080/smartSpace/streamHumididty> [RANGE 10s STEP 1s] "
	+ "WHERE {"
        + "?tempReadings smartSpace:hasValue ?tempValue."
        + "?pressureReadings smartSpace:hasPressureReading ?pressureValue."
        + "?humidityReadings smartSpace:hasHumidityReading ?humidityValue."
        + "}";
      

	tg = new temperatureStreamGenerator("http://localhost:8080/smartSpace/streamTemperature");
        tg2 = new pressureStreamGenerator("http://localhost:8080/smartSpace/streamPressure");
        tg3= new humidityStreamGenerator("http://localhost:8080/smartSpace/streamHumididty");
			//			tg = new BasicIntegerRDFStreamTestGenerator("http://myexample.org/stream");
                        
        // Register an RDF Stream
        engine.registerStream(tg);
        engine.registerStream(tg2);
        engine.registerStream(tg3);
        
        final Thread t = new Thread((Runnable) tg);
	t.start();
        final Thread t2 = new Thread((Runnable) tg2);
	t2.start();
        final Thread t3 = new Thread((Runnable) tg3);
	t3.start();
        
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
                     //System.out.println("----"+rdfTable.size()+" result at SystemTime=["+dateString+"]-----");
                     Hashtable tempData=new Hashtable();
                     Hashtable pressureData=new Hashtable();
                     Hashtable humidityData=new Hashtable();
                     
                     String JSONData=rdfTable.getJsonSerialization();
                     
                     try{
                            JSONObject JSONObj= new JSONObject(JSONData);
                            JSONObject JSONResults= JSONObj.getJSONObject("results");
                            JSONArray JSONBindings= JSONResults.getJSONArray("bindings");
                            for(int i=0; i<JSONBindings.length();i++){
                                JSONObject JSONDataset=JSONBindings.getJSONObject(i);
                             
                                String tempReadings=JSONDataset.getJSONObject("tempReadings").getString("value");
                                String tempValue=JSONDataset.getJSONObject("tempValue").getString("value");
                                String pressureReadings=JSONDataset.getJSONObject("pressureReadings").getString("value");
                                String pressureValue=JSONDataset.getJSONObject("pressureValue").getString("value");
                                String humidityReadings=JSONDataset.getJSONObject("humidityReadings").getString("value");
                                String humidityValue=JSONDataset.getJSONObject("humidityValue").getString("value");
//                                
                                if(!tempData.containsKey(tempReadings))
                                    tempData.put(tempReadings, tempValue);
                                if(!pressureData.containsKey(pressureReadings))
                                    pressureData.put(pressureReadings, pressureValue);
                                if(!humidityData.containsKey(humidityReadings))
                                    humidityData.put(humidityReadings, humidityValue);
                            }
                            System.out.println("----"+tempData.size()+" result at SystemTime=["+dateString+"]-----");
                            
                            OntModel streamingModel=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
            
                            OntClass streamingTempValue=streamingModel.createClass(BASE+"tempValue");
                            OntClass streamingHumidityValue=streamingModel.createClass(BASE+"humidityValue");
                            OntClass streamingPressureValue=streamingModel.createClass(BASE+"pressureValue");
            
                            Individual streamingTempReadings;
                            Individual streamingHumidityReadings;
                            Individual streamingPressureReading;
                            
                            Instant instant=Instant.now();
                            
                            Enumeration dataEnum = tempData.keys();
                            while (dataEnum.hasMoreElements()) {
                              String key = (String) dataEnum.nextElement();
                              streamingTempReadings=streamingTempValue.createIndividual(key);
                              streamingTempReadings.addProperty(p("hasValue"),l1(String.valueOf(tempData.get(key)),XSDDatatype.XSDfloat));
                              streamingTempReadings.addProperty(p("tempHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                              System.out.println(key + " : " + tempData.get(key));
                            }
                            System.out.println("");
                   
                            dataEnum = pressureData.keys();
                            while (dataEnum.hasMoreElements()) {
                              String key = (String) dataEnum.nextElement();
                              streamingPressureReading=streamingPressureValue.createIndividual(key);
                              streamingPressureReading.addProperty(p("hasPressureReading"),l1(String.valueOf(pressureData.get(key)),XSDDatatype.XSDfloat));
                              streamingPressureReading.addProperty(p("pressureHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                              System.out.println(key + " : " + pressureData.get(key));
                            }
                            
                            System.out.println("");
                            dataEnum = humidityData.keys();
                            while (dataEnum.hasMoreElements()) {
                              String key = (String) dataEnum.nextElement();
                              streamingHumidityReadings=streamingHumidityValue.createIndividual(key);
                              streamingHumidityReadings.addProperty(p("hasHumidityReading"),l1(String.valueOf(humidityData.get(key)),XSDDatatype.XSDinteger));
                              streamingHumidityReadings.addProperty(p("humidityHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                              System.out.println(key + " : " + humidityData.get(key));
                            }
                            
                            streamingModel.setNsPrefix("smartSpace", BASE);
                            String saveStreamRDFFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamData.rdf";
                            
                            OutputStream output = new FileOutputStream(saveStreamRDFFile);
                            RDFDataMgr.write(output, streamingModel, RDFFormat.RDFXML_ABBREV);
                            //streamingModel.write(System.out, "RDF/XML"); 
                            System.out.println("Stream RDF written succesffully to:\n"+saveStreamRDFFile);
                            
                            String rdfRule="C:\\Users\\user\\Documents\\SmartSUM\\rules\\validation.txt";
                            String rdfFile= "C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamData.rdf";
                            
                            Instant beforeInferencing=Instant.now();
                            runEngine(rdfRule, rdfFile);
                            Instant afterInferencing=Instant.now();
                            final Duration timeElapsed=Duration.between(beforeInferencing, afterInferencing);
                            final int individualsInferred=tempData.size()+pressureData.size()+humidityData.size();
                            System.out.println("Quadruple: "+individualsInferred);
                            System.out.println("Time complexity: "+(timeElapsed.toMillis()/1000)+"."+(timeElapsed.toMillis()%1000)+" seconds");
                            System.out.println("");
                            
                            JSONObject JSONInnerObj=new JSONObject();
                            JSONInnerObj.put("Quadruple",individualsInferred);
                            JSONInnerObj.put("Time(s)",(timeElapsed.toMillis()/1000)+"."+(timeElapsed.toMillis()%1000));
                            JSONAnalysis.put(dateString, JSONInnerObj);
                            String analysisText=individualsInferred+":"+(timeElapsed.toMillis()/1000)+"."+(timeElapsed.toMillis()%1000);
                            
                            String saveJSONAnalysis="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\analysis.json";
                            String saveAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\analysis.txt";
                            FileWriter fileWriter = new FileWriter(saveJSONAnalysis);
                            fileWriter.write(JSONAnalysis.toString());
                            fileWriter.flush();
                            saveAnalysisText(analysisText,saveAnalysisText );
                        }catch(Exception ex){
                         System.out.println(ex.toString());
                        }
                       System.out.println("");
                     //System.out.println("\nInferencing...");
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
     
     private static Property p ( String localname ){
        return ResourceFactory.createProperty ( BASE, localname );
    }

    private static Literal l ( Object value ) {
        return ResourceFactory.createTypedLiteral ( value );
    }

    private static Literal l1 ( String lexicalform, RDFDatatype datatype ) {
        return ResourceFactory.createTypedLiteral(lexicalform, datatype);
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
        infmodel.write(System.out, "RDF/XML-ABBREV");
        //TestingCsparql.SetInfModel(infmodel);
//        StmtIterator it=infmodel.listStatements();
//        while(it.hasNext()){
//            Statement stmt=it.nextStatement();
//            Resource subject = stmt.getSubject();
//            Property predicate = stmt.getPredicate();
//            RDFNode object = stmt.getObject(); 
//            System.out.println( subject.toString() + " " + predicate.toString() + " " + object.toString() );
        //}
    }
    
    private static void saveAnalysisText(String text,String savePath) throws Exception{
        analysisText+=text+"\r\n";
        FileWriter fileWriter = new FileWriter(savePath);
        fileWriter.write(analysisText);
        fileWriter.flush();
    }
}
