package io.biblia.workflows.submitter;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import io.biblia.workflows.manager.action.ActionState;

import org.bson.Document;

public class Test {

	public static void main(String [] args) {
		Document doc = new Document();
		ActionState a = ActionState.FAILED;
		
		List<Integer> entries = new ArrayList<Integer>();
		entries.add(1);
		entries.add(2);
		doc.append("list", entries);
		System.out.println(doc.toJson());
		List<Integer> entriesBack = doc.get("list", List.class);
		System.out.println(entriesBack);
	}
	
}
