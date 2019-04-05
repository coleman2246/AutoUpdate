/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package update;

import update.Merge;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;



import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


/**
 *
 * @author cole2
 */

public class Update {
	
	public static void createDirs(File inFile) {
		
		if (inFile.getParentFile().exists()) {
			
			inFile.mkdir();
			return;
		}
		
		createDirs(inFile.getParentFile());
		
		
	}
	
	public static void copyFile(File modsFolderGit, File modsFolderLocal) throws IOException {
        //copying and pasting folder 
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        //File modsFolderGit = new File(fileSource);
        //File modsFolderLocal = new File(fileDest);
        /*
        if (modsFolderLocal.isDirectory()) {
        	modsFolderLocal.mkdirs();
        	return;
        } else if (modsFolderLocal.isFile()) {
        	File temp = new File(modsFolderLocal.getAbsolutePath()
        					.toString()
        					.subSequence(0, modsFolderLocal.getAbsoluteFile().toString().lastIndexOf(File.separatorChar)).toString());
        	temp.mkdirs();
        }
        */
        
        if (modsFolderGit.isDirectory()) {
        	return;
        }
        
        
        
        //createDirs(modsFolderGit.getParentFile());
        Path path = Paths.get(modsFolderGit.getParent());
        Files.createDirectories(path);
        
        //modsFolderGit.getParentFile().mkdirs();
        System.out.println(modsFolderGit.getParent());

        
        //System.out.println(modsFolderGit.toString());
        	
        
        sourceChannel = new FileInputStream(modsFolderGit).getChannel();
        
        destChannel = new FileOutputStream(modsFolderLocal).getChannel();

     
        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        sourceChannel.close();
        destChannel.close();
	}
	
	
	
	public static Git returnGit(String dir, String os,String user) throws GitAPIException, IOException {
		Git git = null;
		/*
		FileRepositoryBuilder builder = new FileRepositoryBuilder();

		System.out.print(dir);

		Repository repo = builder.setGitDir(new File(dir)).readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
				*/
		//git = new Git(repo);
		String repoDir = dir;
		dir = dir + File.separatorChar + "UOIT-Craft-Modpack";

		File gitRepo = new File(dir);

		Repository localRepo = new FileRepository(dir + "/.git");

		try {
			git = Git.cloneRepository()
					.setURI("https://github.com/coleman2246/UOIT-Craft-Modpack.git")
					.setDirectory(gitRepo)
					.call();
			
			Merge temp = new Merge(repoDir, dir);
			
			return git;
		} catch (Exception e) {
			e.printStackTrace();
            try {
                git = new Git(localRepo);
                String parseResult = git.pull().call().toString();
                
               
                if("up-to-date.".equals(parseResult.substring(parseResult.length()-12,parseResult.length()-1))) {
                	System.out.println("No update Avaliable");
                	return git;
                }else {
                	Merge temp = new Merge(repoDir, dir);
                	System.out.println("Update Downloading");
                }
       
            } catch (Exception f) {
                System.out.println("\n UPDATE FAILED: report on https://github.com/coleman2246/UOIT-Craft-Modpack.git");
    
            }

		}

		return git;
	}


	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws GitAPIException, IOException {
		// TODO code application logic here

		// getting os
		String os = System.getProperty("os.name").toLowerCase();
		String userName = System.getProperty("user.name");
		System.out.println(userName);
		boolean testing = true;
		
		
		
		String repoDir = "";
		if (os.contains("windows")) {
			// git = returnGit(repoDir);
			// C:\\Users\\cole2\\Twitch\\Minecraft\\Instances\\UOIT CRACT\\
			repoDir = "C:\\Users\\"+userName+"\\Twitch\\Minecraft\\Instances\\UOIT CRACT";

		} else if (os.contains("linux")&& testing) {
		
			repoDir = "/home/mitchell/Documents/temp/updateMine/";
		} else if (os.contains("mac")) {
            // TODO find dir for mac
		} else {
			System.out.println("Unknown filesystem: " + os);

			System.exit(1);
		}

		Git git = returnGit(repoDir, os,userName);

		
	}

}
