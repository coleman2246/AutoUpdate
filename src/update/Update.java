/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package update;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 *
 * @author cole2
 */

public class Update {

    public static Git returnGit(String dir) throws GitAPIException, IOException {
        Git git;
        
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        
        Repository repo = builder.setGitDir(new File(dir))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build(); 
        git = new Git(repo);
        
        try {
            git.cloneRepository()
                .setURI("https://github.com/coleman2246/UOIT-Craft-Modpack.git")
                .call(); 
        } catch (Exception e) {
            ;
        }
        
        return git;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws GitAPIException, IOException {
        // TODO code application logic here
        Git git;
        
        git = returnGit("C:\\Users\\cole2\\Twitch\\Minecraft\\Instances\\Test");

    }

}
