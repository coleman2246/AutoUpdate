package update;

import update.Update;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Merge {
	
	private Gson jsonObj = new Gson();
	private HashMap<String, ArrayList<String>> valuesEnd;
	private HashMap<String, ArrayList<String>> valuesStart;
	public File endpoint;
	public File startpoint;
	
	// Pass dir of copy to and dir of copy from
	public Merge(String endpoint, String startpoint) throws IOException {
		
		// The directory of the git repo
		this.startpoint = new File(startpoint);
		
		// Initialize endpoint file	
		this.endpoint = new File(endpoint);
		
		
		// An empty array list for initializing
		ArrayList<String> empty = new ArrayList<String>();
		
		// Temp directory for initializing
		// For endpoint
		File temp = new File(endpoint + File.separatorChar + "autoUpdate.json");
		
		// JsonReader initialize
		JsonReader reader = null;
		
		// If the file exists, read it in, otherwise
		// create it
		if (temp.exists()) {
			// Make the reader point to local endpoint
			reader = new JsonReader(new FileReader(endpoint + File.separatorChar + "autoUpdate.json"));
			// Set values of endpoint into Hashmap
			this.valuesEnd = this.jsonObj.fromJson(reader, HashMap.class);
		} else {
			
			// Create File
			temp.createNewFile();
			
			// Initialize Hashmap
			this.valuesEnd = new HashMap <String, ArrayList<String>>();
			
			// Fill with empty data
			this.valuesEnd.put("exclude", empty);
			this.valuesEnd.put("override", empty);
			this.valuesEnd.put("ignore", empty);
		}
		
		
		// Temp for startpoint
		temp = new File(startpoint + File.separatorChar + "autoUpdate.json");
		
		// If the file exists, read it in, otherwise
		// create it
		if (temp.exists()) {
			// Make the reader point to local endpoint
			reader = new JsonReader(new FileReader(startpoint + File.separatorChar + "autoUpdate.json"));
			// Set values of endpoint into Hashmap
			this.valuesStart = this.jsonObj.fromJson(reader, HashMap.class);	
		} else {
			// Create file
			temp.createNewFile();
			
			// Initialize Hashmap
			this.valuesStart = new HashMap <String, ArrayList<String>>();
			
			// Fill with empty data
			this.valuesStart.put("exclude", empty);
			this.valuesStart.put("override", empty);
			this.valuesStart.put("ignore", empty);
		}
		
		
		
		// Recursively copy updated files
		//this.recurseEnd(this.endpoint);
		
		// Recusively add new files
		this.recurseStart(this.startpoint);
		
		FileWriter out = new FileWriter(endpoint + File.separatorChar + "autoUpdate.json");
		this.jsonObj.toJson(this.valuesEnd, out);
		
		out = new FileWriter(startpoint + File.separatorChar + "autoUpdate.json");
		this.jsonObj.toJson(this.valuesStart, out);
		
	}

	private void recurseEnd(File node) throws IOException {
		System.out.println(node.getAbsoluteFile());
		
		// Get relative path as string
		//String relative = node.toURI().relativize(this.endpoint.toURI()).toString();
		String relative = node
							.getAbsolutePath()
							.substring(this.endpoint.getAbsolutePath().length(), node.getAbsolutePath().length()).toString();
		
		// Get the file from start at same relative path
		File relStart = new File(this.startpoint.getAbsoluteFile().toString() + File.separatorChar + relative);
		
		
		
		// If the object we are checking is a file
		// Then checks can be done on it
		if (node.isFile()) {
			
			// If the file does not exist in the 
			// start directory and is not marked as
			// ignore, delete it and return
			if (!relStart.exists() &&
					!this.valuesEnd.get("ignore").contains(relative)) {
				// Delete file
				node.delete();
				
				// If the relative path is in the exclude 
				// then remove it 
				if (this.valuesEnd.get("exclude").contains(relative)) {
					this.valuesEnd.get("exclude").remove(relative);
				}
				
				// If the relative path is in the override
				// then remove it
				if (this.valuesEnd.get("override").contains(relative)) {
					this.valuesEnd.get("override").remove(relative);
				}
				
				// return, no more checks need to be made
				return;
			}

			// If the modified time of Start file is after
			// the endpoint
			if (this.isModAfter(relStart, node) && 
					// not in Endpoint exclude section
					(!this.valuesEnd.get("exclude").contains(relative) || 
							// In start point override section
							this.valuesStart.get("override").contains(relative) ||
							// Marked by endpoint as override on next copy
							this.valuesEnd.get("override").contains(relative)) &&
					// Not in start point exclusion section
					!this.valuesStart.get("exclude").contains(relative) &&
					// not the json for this program
					!node.getPath().contains("autoUpdate.json")) {
				
				// copy from startpoint to endpoint
				Update.copyFile(relStart, node);
			
			// Else if endpoint is modified after startpoint,
			} else if (this.isModAfter(node, relStart) &&
						// is not already in exclude
						!this.valuesEnd.get("exclude").contains(relative) &&
						// and is a file
						node.isFile()){
				// Add to endpoint exclusions,
				// it has been modified by the user
				this.valuesEnd.get("exclude").add(relative);
			}
			
		}
		
		// If it is a directory, but not the startpoint
		// recurse through it
		if (node.isDirectory() && !node.equals(this.startpoint)) {
			// Get the list of files/folders in the current
			// directory
			String[] subNode = node.list();
			
			// for each file/directory
			// call this function
			for (String filename : subNode) {
				recurseEnd(new File(node, filename));
			}
			
			// If the current directory does not 
			// exist in the startpoint and is empty
			if ((!relStart.exists() && node.list().length == 0) ||
					// or is just empty
					node.list().length == 0) {
				
				// delete folder
				node.delete();
			}
		}
	}
	
	private void recurseStart(File node) throws IOException {
		// Get relative path as string
		//String relative = node.toURI().relativize(this.startpoint.toURI()).getPath();
		String relative = node
							.getAbsolutePath()
							.substring(this.startpoint.getAbsolutePath().length(), node.getAbsolutePath().length()).toString();
		
		// File with relative path added to endpoint
		File relEnd = new File(this.endpoint.getAbsoluteFile().toString() + File.separatorChar + relative);
		//System.out.println(node.toString());
		//System.out.println(relEnd.toString());
		//System.out.println(relative);
		
		// if the object being checked is a directory
		if (node.isDirectory() &&
				// And is set to be ignored by startpoint
				this.valuesStart.get("ignore").contains(relative)) {
			// skip current directory
			return;
		}
		
		// If the endpoint does not exist
		if (!relEnd.exists() && 
				// The current object is a file
				node.isFile() && 
				// The file is not in the git folder
				!node.toString().contains(".git") &&
				// The file is not set to ignore by startpoint
				!this.valuesStart.get("ignore").contains(relative)) {
			//relEnd.mkdirs();
			// copy from this node, to relative endpoint
			Update.copyFile(node, relEnd);
		}
		
		// If it is a directory and not the endpoint path
		if (node.isDirectory() && !node.equals(this.endpoint)) {
			// create list of all files in directory
			String[] subNode = node.list();
			
			// Go over that list and call with this function
			for (String filename : subNode) {
				recurseStart(new File(node, filename));
			}
			
			
		}
	}
	
	// Check if first file is newer than second file
	public boolean isModAfter(File greater, File less) {
		// If they are both files
		if (greater.isFile() && less.isFile()) {
			
			// If the first is newer than the second, return true
			if (greater.lastModified() > less.lastModified()) {
				return true;
			}
			
			// if not greater return false;
			return true;
		
		// otherwise
		} else {
			// if it is a directory of DNE, return false
			System.out.println("One or both is directory, failure!");
			return false;
		}
		
	}
}
