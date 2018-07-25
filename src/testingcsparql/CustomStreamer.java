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
import it.polimi.deib.csparql_rest_api.RSP_services_csparql_API;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CustomStreamer implements Runnable {
    
    private RSP_services_csparql_API csparqlAPI;
    private String streamName;
    private long sleepTime;
	private String baseUri;
	private boolean keepRunning = true;
        
        private Logger logger = LoggerFactory.getLogger(CustomStreamer.class.getName());
        
        public CustomStreamer(RSP_services_csparql_API csparqlAPI, String iri, String baseUri, long sleepTime) {
		super();
		this.csparqlAPI = csparqlAPI;
		this.streamName = iri;
		this.sleepTime = sleepTime;
		this.baseUri = baseUri;
	}
        
        public void run() {

		Model m;
		Random random = new Random();
		int senderIndex;
		int roomIndex;
		int postIndex;

		while(keepRunning){
			try {
				senderIndex = random.nextInt(5);
				roomIndex = random.nextInt(5);
				postIndex = random.nextInt(Integer.MAX_VALUE);

				m = ModelFactory.createDefaultModel();
				m.add(new ResourceImpl(baseUri + "person" + senderIndex), new PropertyImpl(baseUri + "posts"), new ResourceImpl(baseUri + "post" + postIndex));
				m.add(new ResourceImpl(baseUri+"post" + postIndex), new PropertyImpl(baseUri + "who"), new ResourceImpl(baseUri + "person" + senderIndex));
				m.add(new ResourceImpl(baseUri+"post" + postIndex), new PropertyImpl(baseUri + "where"), new ResourceImpl(baseUri + "room" + roomIndex));

				csparqlAPI.feedStream(streamName, m);
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				logger.error("Error while launching the sleep operation", e);
			}
		}
	}

	public void stopStream(){
		keepRunning = false;
	}
}
