/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package update;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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

	public static Git returnGit(String dir, String os) throws GitAPIException, IOException {
		Git git = null;
		/*
		FileRepositoryBuilder builder = new FileRepositoryBuilder();

		System.out.print(dir);

		Repository repo = builder.setGitDir(new File(dir)).readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
				*/
		//git = new Git(repo);
		dir = dir + "/UOIT-Craft-Modpack";

		File gitRepo = new File(dir);

		Repository localRepo = new FileRepository(dir + "/.git");

		try {
			git = Git.cloneRepository()
					.setURI("https://github.com/coleman2246/UOIT-Craft-Modpack.git")
					.setDirectory(gitRepo)
					.call();

		} catch (Exception e) {
            try {
                git = new Git(localRepo);
                git.pull().call();
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

		String repoDir = "";
		if (os.contains("windows")) {
			// git = returnGit(repoDir);
			repoDir = "";

		} else if (os.contains("linux")) {
			repoDir = "/home/mitchell/Documents/temp/updateMine/";
		} else if (os.contains("mac")) {
            // TODO find dir for mac
		} else {
			System.out.println("Unknown filesystem: " + os);

			System.exit(1);
		}

		Git git = returnGit(repoDir, os);

	}

}
