package com.scc.enci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.scc.enci.config.ServiceConfig;
import com.scc.enci.model.Judge;
import com.scc.enci.model.JudgeBreed;
import com.scc.enci.repository.BreedRepository;
import com.scc.enci.repository.JudgeRepository;
import com.scc.enci.template.BreedObject;
import com.scc.enci.template.JudgeObject;
import com.scc.enci.template.ResponseObjectList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JudgeService {

    private static final Logger logger = LoggerFactory.getLogger(JudgeService.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private JudgeRepository judgeRepository;

    @Autowired
    private BreedRepository breedRepository;
    
    @Autowired
    ServiceConfig config;

    @HystrixCommand(fallbackMethod = "buildFallbackListJudge",
            threadPoolKey = "dogByIdentifiantThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="30"),
                     @HystrixProperty(name="maxQueueSize", value="10")},
            commandProperties={
                     @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                     @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                     @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                     @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                     @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
    )
    public ResponseObjectList<JudgeObject> getFrenchJudges(String show){

        Span newSpan = tracer.createSpan("getFrenchJudges");
        logger.debug("In the judgeService.getFrenchJudges() call, trace id: {}", tracer.getCurrentSpan().traceIdString());
        try {
        	
        	List<Judge> list = new ArrayList<Judge>(); 
        	if ("ESIN".equals(show))
        		list = judgeRepository.findByCountryAndIsInternationalOrderByLastNameAscFirstNameAsc("FR","O");
        	else
        		list = judgeRepository.findByCountryOrderByLastNameAscFirstNameAsc("FR");
        		
        	List<JudgeObject> results = new ArrayList<JudgeObject>();
	    	
	    	for (Judge _judge : list) {
	    		
	    		JudgeObject result = new JudgeObject();

		    	// Construction de la réponse
	    		result.withId(_judge.getId() )
	    			.withName( buildName(_judge.getLastName(),_judge.getFirstName()) )
	    			.withAddress( _judge.getAddress())
	    			.withCity( _judge.getCity() )
	    			.withZipCode( _judge.getZipCode() )
	    			.withEmail( _judge.getEmail() )
	    			.withCountry( _judge.getCountry() )
	    		;
	    		
	    		results.add(result);
	    	}
	    	return new ResponseObjectList<JudgeObject>(results.size(),results);
        }
	    finally{
	    	newSpan.tag("peer.service", "postgres");
	        newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
	        tracer.close(newSpan);
	    }
    }

    private String buildName(String lastName, String firstName) {
    	
    	String completeName = "";
    	
    	if (!"".equals(lastName) && lastName != null) {
        	if (!"".equals(firstName) && firstName != null)
        		completeName = lastName+" "+firstName;
        	else
        		completeName = lastName;
    	}
    		
    	return completeName;
    }
    
    private ResponseObjectList<JudgeObject> buildFallbackListJudge(){
    	
    	List<JudgeObject> list = new ArrayList<JudgeObject>(); 
    	list.add(new JudgeObject()
                .withId(0))
    	;
        return new ResponseObjectList<JudgeObject>(list.size(),list);
    }
    
    private ResponseObjectList<JudgeObject> buildFallbackListJudge(String show){
    	
    	List<JudgeObject> list = new ArrayList<JudgeObject>(); 
    	list.add(new JudgeObject()
                .withId(0))
    	;
        return new ResponseObjectList<JudgeObject>(list.size(),list);
    }
    
    public int getFrenchJudgesCount() {
        Span newSpan = tracer.createSpan("getFrenchJudgesCount");
        logger.debug("In the judgeService.getFrenchJudgesCount() call, trace id: {}", tracer.getCurrentSpan().traceIdString());
        try {
        	return judgeRepository.findByCountryOrderByLastNameAscFirstNameAsc("FR").size();
        }
	    finally{
	    	newSpan.tag("peer.service", "postgres");
	        newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
	        tracer.close(newSpan);
	    }
    }
    
    @HystrixCommand(fallbackMethod = "buildFallbackListJudge",
            threadPoolKey = "dogByIdentifiantThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="30"),
                     @HystrixProperty(name="maxQueueSize", value="10")},
            commandProperties={
                     @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                     @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                     @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                     @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                     @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
    )
    public ResponseObjectList<JudgeObject> getInternationalJudges() {
        Span newSpan = tracer.createSpan("getInternationalJudges");
        logger.debug("In the judgeService.getInternationalJudges() call, trace id: {}", tracer.getCurrentSpan().traceIdString());
        try {
        	List<Judge> list = new ArrayList<Judge>(); 
        	list = judgeRepository.findByIsInternationalOrderByLastNameAscFirstNameAsc("O");
	    	
        	List<JudgeObject> results = new ArrayList<JudgeObject>();
	    	
	    	for (Judge _judge : list) {
	    		
		    	JudgeObject result = new JudgeObject();

		    	// Construction de la réponse
	    		result.withId(_judge.getId() )
	    			.withName( buildName(_judge.getLastName(),_judge.getFirstName()) )
	    			.withAddress( _judge.getAddress())
	    			.withZipCode( _judge.getZipCode() )
	    			.withCity( _judge.getCity() )
	    			.withEmail( _judge.getEmail() )
	    			.withCountry( _judge.getCountry() )
	    		;
	    		
	    		results.add(result);
	    	}
	    	return new ResponseObjectList<JudgeObject>(results.size(),results);
        }
	    finally{
	    	newSpan.tag("peer.service", "postgres");
	        newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
	        tracer.close(newSpan);
	    }
    }

    @HystrixCommand(fallbackMethod = "buildFallbackJudge",
            threadPoolKey = "dogByIdentifiantThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="30"),
                     @HystrixProperty(name="maxQueueSize", value="10")},
            commandProperties={
                     @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                     @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                     @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                     @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                     @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
    )
    public JudgeObject getJudgeById(int id) {
        Span newSpan = tracer.createSpan("getJudgeById");
        logger.debug("In the judgeService.getJudgeById() call, trace id: {}", tracer.getCurrentSpan().traceIdString());

    	JudgeObject result = new JudgeObject();

        try {
        	
        	Judge _judge = judgeRepository.findById(id);

	    	// Construction de la réponse
    		result.withId(_judge.getId() )
    			.withName( buildName(_judge.getLastName(),_judge.getFirstName()) )
    			.withAddress( _judge.getAddress())
    			.withZipCode( _judge.getZipCode() )
    			.withCity( _judge.getCity() )
    			.withEmail( _judge.getEmail() )
    			.withCountry( _judge.getCountry() )
    		;
	    		
        }
	    finally{
	    	newSpan.tag("peer.service", "postgres");
	        newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
	        tracer.close(newSpan);
	    }

        return result;
    }

    private JudgeObject buildFallbackJudge(int id){
    	
    	return new JudgeObject().withId(0);

    }
    
    @HystrixCommand(fallbackMethod = "buildFallbackBreedsByJudge",
            threadPoolKey = "dogByIdentifiantThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="30"),
                     @HystrixProperty(name="maxQueueSize", value="10")},
            commandProperties={
                     @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                     @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                     @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                     @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                     @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
    )
    public ResponseObjectList<BreedObject> getBreedsByIdJudge(int id) {

        Span newSpan = tracer.createSpan("getBreedsByIdJudge");
        logger.debug("In the judgeService.getBreedsByIdJudge() call, trace id: {}", tracer.getCurrentSpan().traceIdString());
        
    	List<BreedObject> results = new ArrayList<BreedObject>();

    	try {
        
    		List<JudgeBreed> list = new ArrayList<JudgeBreed>(); 
        	list = breedRepository.findById(id);

        	for (JudgeBreed _breed : list) {

		    	// Construction de la réponse
	    		results.add( new BreedObject()
	    				.withIdRace( _breed.getIdRace() )
	    		);
	    	}
	    	return new ResponseObjectList<BreedObject>(results.size(),results);
        }
	    finally{
	    	newSpan.tag("peer.service", "postgres");
	        newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
	        tracer.close(newSpan);
	    }

    }


    private ResponseObjectList<BreedObject> buildFallbackBreedsByJudge(int id){
    	
    	List<BreedObject> list = new ArrayList<BreedObject>(); 
    	list.add(new BreedObject()
                .withIdRace(0))
    	;
        return new ResponseObjectList<BreedObject>(list.size(),list);
    }

}
