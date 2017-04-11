package io.biblia.workflows.submitter;

import java.util.Collections;
import java.util.List;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.biblia.workflows.manager.action.ActionState;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.bson.Document;

public class Test {

	public static void main(String [] args) 
	throws MalformedURLException {
		 
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
		String path1 = "hdfs://ip-10-165-124-30.ec2.internal:8020";
		 String path2 = "user/jadiel/workflows/";
		 System.out.println(combine(path1, path2));
	}
	
	public static String combine(String url1, String url2) throws MalformedURLException {
		
		
		URL mergedURL = new URL(new URL(url1), url2);
		
		return mergedURL.toString();
	}
	
}
