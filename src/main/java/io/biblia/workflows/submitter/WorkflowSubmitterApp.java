package io.biblia.workflows.submitter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

import java.io.IOException;

import io.biblia.workflows.Configuration;
import io.biblia.workflows.definition.InvalidWorkflowException;
import io.biblia.workflows.manager.SimpleWorkflowManager;
import io.biblia.workflows.manager.WorkflowManager;
import io.biblia.workflows.definition.parser.WorkflowParser;
import io.biblia.workflows.definition.parser.WorkflowParseException;
import io.biblia.workflows.definition.InvalidWorkflowException;
import io.biblia.workflows.definition.Workflow;
import io.biblia.workflows.ConfigurationKeys;
import io.biblia.workflows.manager.action.ActionPersistance;
import io.biblia.workflows.manager.action.MongoActionPersistance;
import io.biblia.workflows.manager.dataset.DatasetPersistance;
import io.biblia.workflows.manager.dataset.MongoDatasetPersistance;

/**
 * Hello world!
 *
 */
public class WorkflowSubmitterApp implements ConfigurationKeys
{
	/**
	 * Submits a workflow to the database from a json file
	 * that is passed as parameter.
	 * @param args
	 */
    public static void main( String[] args )
    {
        if (args.length != 1) {
        	System.out.println("Incorrect number of arguments given.");
        	System.out.println("Usage: java WorkflowSubmitterApp <path_to_workflow_json_file>");
        	System.exit(1);
        }
        
        //1. Read the workflow file path
        String filePath = args[0];
        String workflowText = null;
        try {
        	workflowText = readFile(filePath);
        }
        catch(IOException ex) {
        	ex.printStackTrace();
        	System.exit(1);
        }
        
        //2. Parse the text file into a workflow instance
        WorkflowParser wParser = io.biblia.workflows.definition.parser.v1.WorkflowParser.getInstance();
        Workflow workflow = null;
        try{
        	workflow = wParser.parseWorkflow(workflowText);
        }
        catch(WorkflowParseException | InvalidWorkflowException ex) {
        	ex.printStackTrace();
        	System.exit(1);
        }
        
        //3. Submit the workflow instance to the workflow Manager.
        //create a workflow manager and submit the workflow to the workflow manager.
        //TODO: This is why this application needs to be a server instead in the future
        //Because it is very expensive to reinitialize all this resources each
        //time that I want to submit a workflow.
        MongoClient mongo = initializeMongoClient();
        ActionPersistance aPersistance = new MongoActionPersistance(mongo);
        DatasetPersistance dPersistance = new MongoDatasetPersistance(mongo);
        WorkflowManager manager = new SimpleWorkflowManager(dPersistance, 
        		aPersistance);
        manager.submitWorkflow(workflow);
        mongo.close();
    }
    
    private static String readFile(String filePath) throws IOException {
    	
    	byte[] encoded = Files.readAllBytes(Paths.get(filePath));
    	return new String(encoded, Charset.defaultCharset());
    }
    
    /**
	 * Creates a MongoClient that supports multithreading.
	 * @return
	 */
	private static MongoClient initializeMongoClient() {
		String mongo_host = Configuration.getValue(MONGODB_HOST, "192.168.99.100");
		int mongo_port = Integer.parseInt(Configuration.getValue(MONGODB_PORT, "27017"));
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.threadsAllowedToBlockForConnectionMultiplier(50000);
		builder.socketKeepAlive(true);
		builder.connectionsPerHost(10000);
		builder.minConnectionsPerHost(2500);
		MongoClientOptions options = builder.build();
		
		MongoClient mongo = new MongoClient(new ServerAddress(mongo_host, mongo_port), options);
		return mongo;
	}
}
