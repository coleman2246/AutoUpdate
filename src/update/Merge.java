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
	private HashMap<String, ArrayList<String>> values;
	private HashMap<String, ArrayList<String>> valuesLoc;
	public File baseDir;
	public File runDir;
	
	// Pass dir of copy to and dir of copy from
	public Merge(String dir, String runDir) throws IOException {

		this.runDir = new File(runDir);
		
		ArrayList<String> empty = new ArrayList<String>();
		
		File temp = new File(dir + File.separatorChar + "autoUpdate.json");
		JsonReader reader = null;
		
		if (temp.exists()) {
			reader = new JsonReader(new FileReader(dir + File.separatorChar + "autoUpdate.json"));
			this.values = this.jsonObj.fromJson(reader, HashMap.class);
		} else {
			temp.createNewFile();
			
			this.values = new HashMap <String, ArrayList<String>>();
			
			this.values.put("exclude", empty);
			this.values.put("override", empty);
		}
		
		temp = new File(runDir + File.separatorChar + "autoUpdate.json");
		
		if (temp.exists()) {
			reader = new JsonReader(new FileReader(runDir + File.separatorChar + "autoUpdate.json"));
			this.valuesLoc = this.jsonObj.fromJson(reader, HashMap.class);	
		} else {
			temp.createNewFile();
			temp.createNewFile();
			
			this.valuesLoc = new HashMap <String, ArrayList<String>>();
			
			this.valuesLoc.put("exclude", empty);
			this.valuesLoc.put("override", empty);
		}
		
				
		this.baseDir = new File(dir);

		//this.recurse(this.baseDir);
		this.recurseLoc(this.runDir);
		
		FileWriter out = new FileWriter(dir + File.separatorChar + "autoUpdate.json");
		this.jsonObj.toJson(this.values, out);
		
		out = new FileWriter(runDir + File.separatorChar + "autoUpdate.json");
		this.jsonObj.toJson(this.valuesLoc, out);
		
	}

	private void recurse(File node) throws IOException {
		System.out.println(node.getAbsoluteFile());
		String relative = node.toURI().relativize(this.baseDir.toURI()).toString();
		relative = node.getAbsolutePath().substring(this.baseDir.getAbsolutePath().length(), node.getAbsolutePath().length()).toString();
		
		File relRun = new File(this.runDir.getAbsoluteFile().toString() + File.separatorChar + relative);
		
		
		
		if (node.isFile()) {
			if (!relRun.exists() && relRun.isFile()) {
				node.delete();
			}

			if (this.isModAfter(relRun, node) && 
					(!this.values.get("exclude").contains(relative) || 
							this.valuesLoc.get("override").contains(relative) || 
							this.values.get("override").contains(relative))) {
				Update.copyFile(relRun, node);
			} else if (!this.isModAfter(relRun, node) && !this.values.get("exclude").contains(relative)){
				this.values.get("exclude").add(relative);
			}
			
		}
		
		if (node.isDirectory() && !node.equals(this.runDir)) {
			String[] subNode = node.list();
			for (String filename : subNode) {
				recurse(new File(node, filename));
			}
			
			if (!relRun.exists() && node.list().length == 0) {
				node.delete();
			}
		}
	}
	
	private void recurseLoc(File node) throws IOException {
		
		String relative = node.toURI().relativize(this.runDir.toURI()).getPath();
		relative = node.getAbsolutePath().substring(this.runDir.getAbsolutePath().length(), node.getAbsolutePath().length()).toString();
		
		File relRun = new File(this.baseDir.getAbsoluteFile().toString() + File.separatorChar + relative);
		System.out.println(node.toString());
		System.out.println(relRun.toString());
		System.out.println(relative);
		
		if (!relRun.exists() && node.isFile() && !node.toString().contains(".git")) {
			//relRun.mkdirs();
			Update.copyFile(node, relRun);
		}
		
		if (node.isDirectory()) {
			String[] subNode = node.list();
			for (String filename : subNode) {
				recurseLoc(new File(node, filename));
			}
			
			
		}
	}
	
	public boolean isModAfter(File greater, File less) {
		if (greater.isFile() && less.isFile()) {
			
			if (greater.lastModified() > less.lastModified()) {
				return true;
			}
			
		} else {
			System.out.println("One or both is directory, failure!");
			return false;
		}
		
		
		return true;
	}
}
