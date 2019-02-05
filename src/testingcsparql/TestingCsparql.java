/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingcsparql;


import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openrdf.rio.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingCsparql {

    private static Logger logger = LoggerFactory.getLogger(TestingCsparql.class);
    
    public static InfModel infModel;
    private static final String BASE="http://localhost:8080/smartSpace#";
    private static final String BASE1="http://localhost:8080/smartSpace/";
    static String analysisText="",CSparqlQueryAnalysisText="",processingAnalysisText="",latencyAnalysisText="";
    static String saveRuleAnalysisText,inferenceRule;
    static String winterAnalysis="",springAnalysis="",summerAnalysis="",autumAnalysis="",heatAnalysis="",coolantAnalysis="",heatcoolantAnalysis="",consistencyAnalysis="",humidityAnalysis="";
    static OntModel historicaldModel=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
    
    static InputStream inputStream=null;
    static String language_RDF="RDF/XML",language_TURTLE="TURTLE",language_NTriple="N-TRIPLES",language_N3="N3";
//    static String smartSpaceRDF= "C:\\Users\\user\\Documents\\SmartSUM\\dataset\\smartSpace.rdf";
    static String smartSpaceRDF= "C:\\Users\\user\\Documents\\SmartSUM\\ontologies\\smartSpace.rdf";
    
    static OntClass historicalTempValue;
    static OntClass historicalHumidityValue;
    static OntClass historicalPressureValue;
        
    static Individual historicalTempReadings;
    static Individual historicalHumidityReadings;
    static Individual historicalPressureReading;
    
    static  Instant beforeQuery=null;
    static int cycleCount=0,roomSize,season;
    static float doorDistanceFromTempS,fluoroDistanceFromTempS,radDistanceFromTempS,coolantDistanceFromTemps,windowDistanceFromTempS;
    
    static Random rand;
    static int totalQuadruple=0,previousQuadrupleInferred=0;
    static long latencyTime=0;
    static boolean inferredBefore=false;
    static Instant initialInstant;
    
    static float precision,recall;
    static int inconsistencyCount=0,consistencyCount=0,nullCount=0,plausibiltyCounts=0;
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        try {
            inputStream=new FileInputStream(smartSpaceRDF);
            historicaldModel.read(inputStream, language_RDF);
//            System.out.println(historicaldModel);
                     //streamingModel.write(System.out, "RDF/XML");
            historicalTempValue=historicaldModel.getOntClass(BASE1+"tempValue");
            historicalHumidityValue=historicaldModel.getOntClass(BASE1+"humidityValue");
            historicalPressureValue=historicaldModel.getOntClass(BASE1+"pressureValue");
//            System.out.println("Door Status:"+setDoorLeakage().toString());
//            System.out.println("window Status:"+setWindowLeakage().toString());
//            System.out.println("Season: "+ getSeason());
//            return;
                //PropertyConfigurator.configure(new URL("C:\\Users\\Duchess\\Documents\\SmartSUM\\CSPARQL\\config_files/csparql_readyToGoPack_log4j.properties"));
	} catch (Exception e) {
		logger.error(e.getMessage(), e);
	}
        
        String query = null;
       
	RdfStream tg = null;
        RdfStream tg2 = null;
        RdfStream tg3 = null;
        RdfStream tg4 = null;
        RdfStream tg5 = null;
        RdfStream tg6 = null;
        
        JSONObject JSONAnalysis=new JSONObject();
       
        // Initialize C-SPARQL Engine
	CsparqlEngine engine = new CsparqlEngineImpl();
        
        
        engine.initialize(true);
        
        query = "REGISTER QUERY sensorValueOf AS "
        + "PREFIX smartSpace:<http://localhost:8080/smartSpace#> "
	+ "SELECT ?tempReadings ?tempValue ?pressureReadings ?pressureValue ?humidityReadings ?humidityValue "
	+ "FROM STREAM <http://localhost:8080/smartSpace/streamTemperature> [RANGE 15s STEP 2s] "
        + "FROM STREAM <http://localhost:8080/smartSpace/streamPressure> [RANGE 15s STEP 2s] "
        + "FROM STREAM <http://localhost:8080/smartSpace/streamHumididty> [RANGE 15s STEP 2s] "
        
	+ "WHERE {"
        + "?tempReadings smartSpace:hasValue ?tempValue."
        + "?pressureReadings smartSpace:hasPressureReading ?pressureValue."
        + "?humidityReadings smartSpace:hasHumidityReading ?humidityValue."
        + "}";
      

	tg = new temperatureStreamGenerator("http://localhost:8080/smartSpace/streamTemperature");
        tg2 = new pressureStreamGenerator("http://localhost:8080/smartSpace/streamPressure");
        tg3= new humidityStreamGenerator("http://localhost:8080/smartSpace/streamHumididty");
        tg4 = new temperatureStreamGenerator2("http://localhost:8080/smartSpace/streamTemperature");
        tg5 = new pressureStreamGenerator2("http://localhost:8080/smartSpace/streamPressure");
        tg6= new humidityStreamGenerator2("http://localhost:8080/smartSpace/streamHumididty");
			//			tg = new BasicIntegerRDFStreamTestGenerator("http://myexample.org/stream");
                        
        // Register an RDF Stream
        engine.registerStream(tg);
        engine.registerStream(tg2);
        engine.registerStream(tg3);
        engine.registerStream(tg4);
        engine.registerStream(tg5);
        engine.registerStream(tg6);
        
        final Thread t = new Thread((Runnable) tg);
	t.start();
        final Thread t2 = new Thread((Runnable) tg2);
	t2.start();
        final Thread t3 = new Thread((Runnable) tg3);
	t3.start();
        final Thread t4 = new Thread((Runnable) tg4);
	t4.start();
        final Thread t5 = new Thread((Runnable) tg5);
	t5.start();
        final Thread t6 = new Thread((Runnable) tg6);
	t6.start();
        
        CsparqlQueryResultProxy c1 = null;
        
        try {
            c1 = engine.registerQuery(query, false);
            logger.debug("Query: {}", query);
            logger.debug("Query Start Time : {}", System.currentTimeMillis());
        } catch (final ParseException ex) {
            logger.error(ex.getMessage(), ex);
	}
        
        beforeQuery=Instant.now();
        if (c1 != null) {
            c1.addObserver( new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    cycleCount+=1;
                    Instant afterQuery=Instant.now();
                    final Duration csparqlQueryTimeElapsed=Duration.between(beforeQuery, afterQuery);
                    System.out.println("Total Window Cycle: "+cycleCount);
                    System.out.println("CSPARQL Query Time: "+(csparqlQueryTimeElapsed.toMillis()/1000)+"."+String.format("%04d",(csparqlQueryTimeElapsed.toMillis()%1000)));
                    beforeQuery=afterQuery;
                    
                     final RDFTable rdfTable = (RDFTable) arg;
                     //System.out.println(rdfTable.toString());
                     System.out.println("");
                     final String[] vars = rdfTable.getNames().toArray(new String[]{});
                     Date date = new Date(System.currentTimeMillis()); //Input your time in milliseconds
                     String dateString = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(date);
                     //System.out.println("----"+rdfTable.size()+" result at SystemTime=["+dateString+"]-----");
                     System.out.println("Raw quadruples with duplicated readings: "+rdfTable.size());
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
                            int nonredundantData=tempData.size()+pressureData.size()+humidityData.size();
                            System.out.println("----"+nonredundantData+" result at SystemTime=["+dateString+"]-----");
                            
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
                           
                              //System.out.println(historicalTempValue);
                              historicalTempReadings=historicalTempValue.createIndividual(key);
                              historicalTempReadings.addProperty(p("hasValue"),l1(String.valueOf(tempData.get(key)),XSDDatatype.XSDfloat));
                              historicalTempReadings.addProperty(p("tempHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                            }
                           
                            System.out.println("");
                            dataEnum = pressureData.keys();
                            while (dataEnum.hasMoreElements()) {
                              String key = (String) dataEnum.nextElement();
                              streamingPressureReading=streamingPressureValue.createIndividual(key);
                              streamingPressureReading.addProperty(p("hasPressureReading"),l1(String.valueOf(pressureData.get(key)),XSDDatatype.XSDfloat));
                              streamingPressureReading.addProperty(p("pressureHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                              System.out.println(key + " : " + pressureData.get(key));
                              
                              historicalPressureReading=historicalPressureValue.createIndividual(key);
                              historicalPressureReading.addProperty(p("hasPressureReading"),l1(String.valueOf(pressureData.get(key)),XSDDatatype.XSDfloat));
                              historicalPressureReading.addProperty(p("pressureHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                            }
                            
                            System.out.println("");
                            dataEnum = humidityData.keys();
                            while (dataEnum.hasMoreElements()) {
                              String key = (String) dataEnum.nextElement();
                              streamingHumidityReadings=streamingHumidityValue.createIndividual(key);
                              streamingHumidityReadings.addProperty(p("hasHumidityReading"),l1(String.valueOf(humidityData.get(key)),XSDDatatype.XSDinteger));
                              streamingHumidityReadings.addProperty(p("humidityHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                              System.out.println(key + " : " + humidityData.get(key));
                              
                              historicalHumidityReadings=historicalHumidityValue.createIndividual(key);
                              historicalHumidityReadings.addProperty(p("hasHumidityReading"),l1(String.valueOf(humidityData.get(key)),XSDDatatype.XSDinteger));
                              historicalHumidityReadings.addProperty(p("humidityHasTimestamp"),l1(String.valueOf(instant),XSDDatatype.XSDdateTime));
                            }
                            
                            streamingModel.setNsPrefix("smartSpace", BASE);
                            String saveStreamRDFFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamData.rdf";
                            String saveStreamTURTLEFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamDataTurtle.ttl";
                            String saveStreamNTRIPLEFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamDataNTriple.nt";
                            String saveStreamN3File="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamDataN3.n3";
                            
                            historicaldModel.setNsPrefix("smartSpace",BASE);
                            String saveHistoricalRDFFile="C:\\Users\\user\\Documents\\SmartSUM\\dataset\\smartSpace.rdf";
                            
                            //writeout onyology as in RDF format
                            OutputStream output = new FileOutputStream(saveStreamRDFFile);
                            RDFDataMgr.write(output, streamingModel, RDFFormat.RDFXML_ABBREV);
                            
                            //writeout onyology as in TUTRLE format
                            output=new FileOutputStream(saveStreamTURTLEFile);
                            RDFDataMgr.write(output, streamingModel,RDFFormat.TURTLE);
                            
                            //writeout onyology as in RDF format
                            //output=new FileOutputStream(saveStreamNTRIPLEFile);
                            //RDFDataMgr.write(output, streamingModel,RDFFormat.NTRIPLES);
                            
                            output=new FileOutputStream(saveStreamNTRIPLEFile);
                            streamingModel.write(output,"N-TRIPLES");
                            
                            output=new FileOutputStream(saveStreamN3File);
                            streamingModel.write(output,"N3");
                            
                            output =new FileOutputStream(saveHistoricalRDFFile);
                            RDFDataMgr.write(output,historicaldModel,RDFFormat.RDFXML_ABBREV);
                            //streamingModel.write(System.out, "RDF/XML"); 
//                            System.out.println("Stream RDF written successfully to:\n"+saveStreamRDFFile);
                            
//                            String rdfRule="C:\\Users\\user\\Documents\\SmartSUM\\rules\\validation.txt";
                            String rdfFile= saveStreamRDFFile;//"C:\\Users\\user\\Documents\\SmartSUM\\dataset\\streamDataNTRIPLE.ttl";
                            String rdfRule=getRuleFile("temperature");
                            String inferencingLanguage=language_RDF;
                            
                            //infer temperature
                            String latencyAnalysisText="";
                            Instant beforeInferencing=Instant.now();
                            
                            if(!inferredBefore){
                                latencyAnalysisText="0:0.0";
                                inferredBefore=true;
                            }else{
                                final Duration latencyElapsed=Duration.between(initialInstant, beforeInferencing);
                                latencyAnalysisText=previousQuadrupleInferred+":"+(latencyElapsed.toMillis()/1000)+"."+String.format("%03d",(latencyElapsed.toMillis()%1000));
                            }
                            runEngine(rdfRule, rdfFile,inferencingLanguage);
                            Instant afterInferencing=Instant.now();
                           
                            final Duration timeElapsed=Duration.between(beforeInferencing, afterInferencing);
                            final int individualsInferred=tempData.size()+pressureData.size()+humidityData.size();
                            String analysisText=individualsInferred+":"+(timeElapsed.toMillis()/1000)+"."+String.format("%03d",(timeElapsed.toMillis()%1000));
                            saveInferencingAnalysis(analysisText, inferenceRule, saveRuleAnalysisText);
                            
                            initialInstant=afterInferencing;
                            previousQuadrupleInferred=individualsInferred;
                            
                            //infer humidity
//                            rdfRule=getRuleFile("humidity");
//                            Instant beforeInferencingHumidity=Instant.now();
//                            runEngine(rdfRule, rdfFile,inferencingLanguage);5
//                            Instant afterInferencingHumidity=Instant.now();
//                            final Duration timeElapsedForHumidity=Duration.between(beforeInferencingHumidity, afterInferencingHumidity);
//                            analysisText=individualsInferred+":"+(timeElapsedForHumidity.toMillis()/1000)+"."+String.format("%03d",(timeElapsedForHumidity.toMillis()%1000));
//                            saveInferencingAnalysis(analysisText, inferenceRule, saveRuleAnalysisText);
                            
                            totalQuadruple+=individualsInferred;
                            System.out.println("Quadruple: "+individualsInferred);
                            System.out.println("Time complexity: "+(timeElapsed.toMillis()/1000)+"."+String.format("%04d",(timeElapsed.toMillis()%1000)));
                            System.out.println("Total Quadruple inferred: "+totalQuadruple);
                            System.out.println("");
                            
                            JSONObject JSONInnerObj=new JSONObject();
                            JSONInnerObj.put("Quadruple",individualsInferred);
                            JSONInnerObj.put("Time(s)",(timeElapsed.toMillis()/1000)+"."+String.format("%04d",(timeElapsed.toMillis()%1000)));
                            JSONAnalysis.put(dateString, JSONInnerObj);
                            
                            analysisText=individualsInferred+":"+(timeElapsed.toMillis()/1000)+"."+String.format("%04d",(timeElapsed.toMillis()%1000));
                            String CSparqlAnalysisText=individualsInferred+":"+(csparqlQueryTimeElapsed.toMillis()/1000)+"."+String.format("%04d",(csparqlQueryTimeElapsed.toMillis()%1000));
                            
                            //final Duration timeElapsedForLatency=csparqlQueryTimeElapsed+timeElapsed;
                            Duration processingElapsedTime=csparqlQueryTimeElapsed.plusMillis(timeElapsed.toMillis());
                            String processingAnalysisText=individualsInferred+":"+(processingElapsedTime.toMillis()/1000)+"."+String.format("%04d",(processingElapsedTime.toMillis()%1000));
                            
                            String saveJSONAnalysis="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\analysis.json";
                            String saveAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\analysis.txt";
                            String saveCSparqlAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\CSparqlQueryAnalysis.txt";
                            String saveProcessingAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\processingAnalysis.txt";
                            String saveLatencyAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\latencyAnalysis.txt";
                            FileWriter fileWriter = new FileWriter(saveJSONAnalysis);
                            fileWriter.write(JSONAnalysis.toString());
                            fileWriter.flush();
                            saveAnalysisText(analysisText,saveAnalysisText);
                            SaveCSparqlQueryAnalysisText(CSparqlAnalysisText, saveCSparqlAnalysisText);
                            saveProcessingAnalysisText(processingAnalysisText,saveProcessingAnalysisText);
                            saveLatencyAnalysisText(latencyAnalysisText,saveLatencyAnalysisText);
//                            System.gc();
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
            Thread.sleep(3*60*60*1000);
	} catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
	}
        
        // clean up (i.e., unregister query and stream)
	engine.unregisterQuery(c1.getId());
//	((LBSMARDFStreamTestGenerator) tg).pleaseStop();
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
    
    private static void runEngine(String ruleFile,String rdfFile,String ModelFormat) throws Exception{
        Model model = ModelFactory.createDefaultModel();
        InputStream inschema =new FileInputStream(rdfFile);
        model.read(inschema, null, ModelFormat);
        List rules=Rule.rulesFromURL(ruleFile);
        //System.out.println("\nMy rules:\n"+rules);
        Reasoner reasoner=new GenericRuleReasoner(rules);
        //reasoner=reasoner.bindSchema(model);
        InfModel infmodel=ModelFactory.createInfModel(reasoner, model);
        Resource children = infmodel.getResource("http://localhost:8080/smartSpace#hasValue");
        //infmodel.write(System.out, ModelFormat);
        TestingCsparql.SetInfModel(infmodel);
        
        StmtIterator it=infmodel.listStatements();
        
        String nullCheckQuery= "PREFIX smartSpace:<http://localhost:8080/smartSpace#> "+
                "SELECT * WHERE { "+
                //"?tempSensor smartSpace:hasValue \"8888\"."+
                "?tempSensor smartSpace:tempHasTimestamp ?time."+
                "?tempSensor smartSpace:hasValue ?tempValue."+
                 " FILTER (?tempValue = 8888.88) ."+
                "}";
          
        Query query=QueryFactory.create(nullCheckQuery);
        QueryExecution qexec=QueryExecutionFactory.create(query, infmodel);
        ResultSet rs=qexec.execSelect();
      
        ArrayList<String> nullCheckList=new ArrayList<>(1);
     
        while(rs.hasNext()){
            QuerySolution solution=rs.nextSolution();
            Literal tempSensor= solution.getLiteral("tempReading");
            Resource r=solution.getResource("tempSensor");
           
//           Literal count= solution.getLiteral("total");
//            System.out.println(count.getInt());
            
//            Node node=infmodel.getResource(r.toString()).asNode();
//            RDFNode rdfNode=infmodel.getRDFNode(node);
//            //namespace=r.toString();
            nullCheckList.add(r.toString());
            //System.out.println(solution);
        }
        
        for(int i=0;i<nullCheckList.size();i++){
            //System.out.println(tempReadings.get(i));
            infmodel.getResource(nullCheckList.get(i)).addProperty(p("errorData"), "Missing Value",  XSDDatatype.XSDstring);
            //System.out.println(rsrc.toString());
        }
         
        String plausibilityCheckQuery="PREFIX smartSpace:<http://localhost:8080/smartSpace#> "+
                "SELECT ?reason (COUNT(*) AS ?total)"+
                "WHERE {"+
                "?tempSensor smartSpace:isValidForPlausibilityCheck ?reason."+
                // " FILTER (?tempValue = 8888.88) ."+
                "} GROUP BY ?reason";
        
        String consistencyCheckQuery="PREFIX smartSpace:<http://localhost:8080/smartSpace#> "+
                "SELECT ?reason (COUNT(*) AS ?total)"+
                "WHERE {"+
                "?tempSensor smartSpace:isValid ?reason."+
                // " FILTER (?tempValue = 8888.88) ."+
                "} GROUP BY ?reason";
        
        String inconsistencyCheckQuery="PREFIX smartSpace:<http://localhost:8080/smartSpace#> "+
                "SELECT ?tempValue (COUNT(*) AS ?total)"+
                "WHERE {"+
                "?tempSensor smartSpace:hasValue ?tempValue."+
                 " FILTER (?tempValue < -25.2) ."+
                "} GROUP BY ?tempValue";
        
        plausibiltyCounts+=getPrecisionCount(infmodel, plausibilityCheckQuery);
        consistencyCount+=getPrecisionCount(infmodel, consistencyCheckQuery);
        inconsistencyCount+=getPrecisionCount(infmodel, inconsistencyCheckQuery);
        nullCount+=nullCheckList.size();
        
        precision=(float)(inconsistencyCount+nullCount)/(consistencyCount+plausibiltyCounts);
        recall=(float)(inconsistencyCount+nullCount)/(consistencyCount+nullCount);
        
//        System.out.println("PlausibiltyCheck count: "+getPrecisionCount(infmodel, plausibilityCheckQuery));
//        System.out.println("ConsistencyCheck count: "+getPrecisionCount(infmodel, consistencyCheckQuery));
//        System.out.println("ErrorCheck count: "+getPrecisionCount(infmodel, inconsistencyCheckQuery));
//        System.out.println("nullCheck count: "+nullCheckList.size());
        
        System.out.println("PlausibiltyCheck count: "+plausibiltyCounts);
        System.out.println("ConsistencyCheck count: "+consistencyCount);
        System.out.println("InconsistencyCheck count: "+inconsistencyCount);
        System.out.println("nullCheck count: "+nullCount);
        System.out.println("Precision: "+precision);
        System.out.println("Recallt: "+recall);
        infmodel.write(System.out, ModelFormat);

    }

    private static int getPrecisionCount(Model inferredModel,String sparqlQuery){
        int count=0;
        Query query=QueryFactory.create(sparqlQuery);
        QueryExecution qexec=QueryExecutionFactory.create(query, inferredModel);
        ResultSet rs=qexec.execSelect();
        while(rs.hasNext()){
            QuerySolution solution=rs.nextSolution();
            Literal total= solution.getLiteral("total");
            count=total.getInt();
        }
        return count;
    }
    
    private static void saveAnalysisText(String text,String savePath) throws Exception{
        analysisText+=text+"\r\n";
        FileWriter fileWriter = new FileWriter(savePath);
        fileWriter.write(analysisText);
        fileWriter.flush();
    }
    
    private static void saveProcessingAnalysisText(String text,String savePath) throws Exception{
        processingAnalysisText+=text+"\r\n";
        FileWriter fileWriter = new FileWriter(savePath);
        fileWriter.write(processingAnalysisText);
        fileWriter.flush();
    }
    
     private static void saveLatencyAnalysisText(String text,String savePath) throws Exception{
        latencyAnalysisText+=text+"\r\n";
        FileWriter fileWriter = new FileWriter(savePath);
        fileWriter.write(latencyAnalysisText);
        fileWriter.flush();
    }
    
    private static void SaveCSparqlQueryAnalysisText(String text,String savePath) throws Exception{
        CSparqlQueryAnalysisText+=text+"\r\n";
        FileWriter fileWriter = new FileWriter(savePath);
        fileWriter.write(CSparqlQueryAnalysisText);
        fileWriter.flush();
    }
    
    private static void saveInferencingAnalysis(String text, String rule, String savePath) throws Exception{
        String saveText="";
        switch (rule){
            case "winter":
                winterAnalysis+=text+"\r\n";
                saveText=winterAnalysis;
                break;
            case "spring":
                springAnalysis+=text+"\r\n";
                saveText=springAnalysis;
                break;
            case "summer":
                summerAnalysis+=text+"\r\n";
                saveText=summerAnalysis;
                break;
            case "autum":
                autumAnalysis+=text+"\r\n";
                saveText=autumAnalysis;
                break;
            case "heat":
                heatAnalysis+=text+"\r\n";
                saveText=heatAnalysis;
                break;
            case "coolant":
                coolantAnalysis+=text+"\r\n";
                saveText=coolantAnalysis;
                break;
            case "heatcoolant":
                heatcoolantAnalysis+=text+"\r\n";
                saveText=heatcoolantAnalysis;
                break;
            case "consistency":
                consistencyAnalysis+=text+"\r\n";
                saveText=consistencyAnalysis;
                break;
            case "humidity":
                humidityAnalysis+=text+"\r\n";
                saveText=humidityAnalysis;
                break;
        }
        FileWriter fileWriter = new FileWriter(savePath);
        fileWriter.write(saveText);
        fileWriter.flush();
    }
    
    private static float getActuatorDistances(String Individual, String dataProperty){      
        Individual in=historicaldModel.getIndividual(BASE1+Individual);
        Property prp=historicaldModel.getProperty(BASE1+dataProperty);
        return Float.valueOf(in.getProperty(prp).getObject().toString());
    }
    
    private static boolean doorLeaks(){
        rand=new Random();
        return rand.nextBoolean();
    }
    
    private static boolean windowLeaks(){
        rand=new Random();
        return rand.nextBoolean();
    }
    
    private static boolean heatRegulatorOn(){
        rand=new Random();
        return rand.nextBoolean();
    }
    
    private static boolean coolantRegulatorOn(){
        rand=new Random();
        return rand.nextBoolean();
    }
    
    private static String getSeason(){
        Calendar now = Calendar.getInstance();
        int month= now.get(Calendar.MONTH)+1;
        String season = "";
        switch(month){
            case 1:
                season = "winter";
                break;
            case 2:
                season = "winter";
                break; 
            case 3:
                season = "spring";
                break; 
            case 4:
                season = "spring";
                break;
            case 5:
                season = "spring";
                break;
            case 6:
                season = "summer";
                break;
            case 7:
                season = "summer";
                break;
            case 8:
                season = "summer";
                break;
            case 9:
                season = "autum";
                break;
            case 10:
                season = "autum";
                break;
            case 11:
                season = "autum";
                break;
            case 12:
                season = "winter";
                break;
        }
        return season;
    }
    
    private static String getRuleFile(String inferenceType){
        String ruleURL="";
        boolean doorIsLaeking=doorLeaks();
        boolean windowIsLaeking=windowLeaks();
        boolean heatRegulatorIsOn=heatRegulatorOn();
        boolean coolantRegulatorIsOn=coolantRegulatorOn();
        String seasonOfTheYear=getSeason();
        
        System.out.println("Door leakage: "+doorIsLaeking);
        System.out.println("Window leakage: "+windowIsLaeking);
        System.out.println("Heat Regulator: "+(heatRegulatorIsOn ? "ON" : "OFF"));
        System.out.println("Coolant Regulator: "+(coolantRegulatorIsOn ? "ON" : "OFF"));
        System.out.println("Season of the year: "+getSeason());
        
        if("humidity".equals(inferenceType)){
            ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\humidityCheck.txt";
            saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\humidityAnalysis.txt";
            inferenceRule="humidity";
        }else if("temperature".equals(inferenceType)){
            
           if(System.currentTimeMillis()%5==0){
               ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\consistency.txt";
               saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\consistencyAnalysis.txt";
               inferenceRule="consistency";
           }else{
                    if(doorIsLaeking || windowIsLaeking){
                     switch(seasonOfTheYear){
                         case "winter":
                             ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\winter.txt";
                             saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\winterAnalysis.txt";
                             inferenceRule="winter";
                             break;
                         case "spring":
                             ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\spring.txt";
                             saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\springAnalysis.txt";
                             inferenceRule="spring";
                             break;
                         case "summer":
                             ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\summer.txt";
                             saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\summerAnalysis.txt";
                             inferenceRule="summer";
                             break;
                         case "autum":
                             ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\autum.txt";
                             saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\autumAnalysis.txt";
                             inferenceRule="autum";
                             break;
                     }
                 }else if(heatRegulatorIsOn || coolantRegulatorIsOn){
                     if(heatRegulatorIsOn && !coolantRegulatorIsOn){
                         ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\heat.txt";
                         saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\heatAnalysis.txt";
                         inferenceRule="heat";
                     }else if(!heatRegulatorIsOn && coolantRegulatorIsOn){
                         ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\coolant.txt";
                         saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\coolantAnalysis.txt";
                         inferenceRule="coolant";
                     }else if(heatRegulatorIsOn && coolantRegulatorIsOn){
                         ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\heatandcoolant.txt";
                         saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\heatandcoolantAnalysis.txt";
                         inferenceRule="heatcoolant";
                     }
                 }else{
                     ruleURL="C:\\Users\\user\\Documents\\SmartSUM\\rules\\consistency.txt";
                     saveRuleAnalysisText="C:\\Users\\user\\Documents\\SmartSUM\\analysis\\consistencyAnalysis.txt";
                     inferenceRule="consistency";
                 } 
           }  
        }
        
        System.out.println("Rule File: "+ruleURL);
        return ruleURL;
    }
    
    private void unusedCodes(){
        //get data count n time for each rule
        //conflict resolution check btw rules
        //accuracy evaluation for fault tolreant system
        //pull all redaings and filter out 
        
//        System.out.println("Select Season:");
//                System.out.println("Autum -----------> 1");
//                System.out.println("Spring ----------> 2");
//                System.out.println("Summer ----------> 3");
//                System.out.println("Winter ----------> 4");
//                Scanner sc =new Scanner(System.in);
//                season=sc.nextInt();
//                inputStream=new FileInputStream(smartSpaceRDF);
//                historicaldModel.read(inputStream, langauge);
                
//                historicalTempValue=historicaldModel.getOntClass(BASE1+"tempValue");
//                historicalHumidityValue=historicaldModel.getOntClass(BASE1+"humidityValue");
//                historicalPressureValue=historicaldModel.getOntClass(BASE1+"pressureValue");
////                OntClass getClass=historicaldModel.getOntClass(BASE1);
//                System.out.println(historicalTempValue.toString());

//                doorDistanceFromTempS=getActuatorDistances("valueofDoorDist", "doorDistFromTempS");
//                fluoroDistanceFromTempS=getActuatorDistances("fluorometerDistance", "fDistanceFromTempS");
//                radDistanceFromTempS=getActuatorDistances("radiaorDistValue", "hasRadDistValue");
//                coolantDistanceFromTemps=getActuatorDistances("coolantDistValue", "hasCoolantValueOf");
//                windowDistanceFromTempS=getActuatorDistances("winDistanceValue", "hasWinDistValueOf");
//                
//                System.out.println("Door: "+doorDistanceFromTempS);
//                System.out.println("Fluoro: "+fluoroDistanceFromTempS);
//                System.out.println("Radio: "+radDistanceFromTempS);
//                System.out.println("Coolant: "+coolantDistanceFromTemps);
//                System.out.println("Window: "+windowDistanceFromTempS);
//                return;


//        while(it.hasNext()){
//            Statement stmt=it.nextStatement();
//            Resource subject = stmt.getSubject();
//            Property predicate = stmt.getPredicate();
//            RDFNode object = stmt.getObject(); 
//            //System.out.println( subject.toString() + " " + predicate.toString() + " " + object.toString() );
//            //System.out.println(subject.toString());
//            System.out.println(stmt.toString());
//            System.out.println("");
//        }
    }
}
