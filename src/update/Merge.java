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
		
		
		
		File temp = new File(dir + File.pathSeparatorChar + "autoUpdate.json");
		JsonReader reader = null;
		
		if (temp.exists()) {
			reader = new JsonReader(new FileReader(dir + File.pathSeparatorChar + "autoUpdate.json"));
			this.values = this.jsonObj.fromJson(reader, HashMap.class);
		} else {
			temp.createNewFile();

			this.valuesLoc.put("exclude", new ArrayList<String>());
			this.valuesLoc.put("override", new ArrayList<String>());
		}
		
		temp = new File(runDir + File.pathSeparatorChar + "autoUpdate.json");
		
		if (temp.exists()) {
			reader = new JsonReader(new FileReader(runDir + File.pathSeparatorChar + "autoUpdate.json"));
			this.valuesLoc = this.jsonObj.fromJson(reader, HashMap.class);	
		} else {
			temp.createNewFile();

			this.valuesLoc.put("exclude", new ArrayList<String>());
			this.valuesLoc.put("override", new ArrayList<String>());
		}
		
				
		this.baseDir = new File(dir);

		recurse(this.baseDir);
		recurseLoc(this.runDir);
		
		FileWriter out = new FileWriter(dir + File.pathSeparatorChar + "autoUpdate.json");
		this.jsonObj.toJson(this.values, out);
		
		out = new FileWriter(runDir + File.pathSeparatorChar + "autoUpdate.json");
		this.jsonObj.toJson(this.valuesLoc, out);
		
	}

	private void recurse(File node) throws IOException {
		System.out.println(node.getAbsoluteFile());
		String relative = node.toURI().relativize(this.baseDir.toURI()).toString();
		File relRun = new File(this.runDir.getAbsoluteFile().toString() + relative);
		
		
		
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
		//System.out.println(node.getAbsoluteFile());
		String relative = node.toURI().relativize(this.runDir.toURI()).toString();
		File relRun = new File(this.baseDir.getAbsoluteFile().toString() + relative);
		
		if (!relRun.exists()) {
			relRun.mkdirs();
			Update.copyFile(node, relRun);
		}
		
		if (node.isDirectory()) {
			String[] subNode = node.list();
			for (String filename : subNode) {
				recurse(new File(node, filename));
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
