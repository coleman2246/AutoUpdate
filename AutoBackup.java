/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Coles Laptop
 */
public class AutoBackup {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
  
}
//It extends thread so that the program can be run by the program cocurrently.
//This was done so the program can be interactied with. The while (!stop) could not be stoppped with out this because the UI could no longer be itneracted with.
class Backupclass extends Thread {

  /*Intializing pretty much all variables so that is can be accessed by the call 
    Zip method when the CallZip method is called*/
    public static int interval = 0;
    public static String location = "";
    public static boolean stop = false;
    public String path = "";
    public int re = 0;
    public int e = 0;
    public String time="";


    @Override
    public void run() {
// replacing all of the "" that may be in the path the user gives the program with nothing.
        path = location.replaceAll("\"", "");
        try {
            
            //finding out where the last / is or the last \ is in the path and storing it in the varialbe re
            //so that I can find the name of the file and substrings can be made
            if (path.contains("/") || path.contains("\\")) {

                if (path.lastIndexOf("\\") != -1) {
                    re = path.lastIndexOf("\\") + 1;
                } else if (path.lastIndexOf("/") != -1) {
                    re = path.lastIndexOf("/") + 1;
                } else {
                    
                }
//kinda finding wwhere the .fileexstension is so that I can name the folder.              
                if (path.lastIndexOf(".") != -1) {
                    e = path.lastIndexOf(".") + 1;
                } else {
                    e = path.length();
                }
                //making the path a file
                File file = new File(path);
                //looping unless the user wants to stop the backup portion of the program
                while (!stop) {
   
                       //getting the current time and date down to the sec     
                    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                    //getting the time that the file was last modified and storing it in the String time
                    time = date.format(file.lastModified());

                    //Making paths for the backup folder and the text file. The purpose of the textfile is to store the last date modified.
                    Path path1 = Paths.get(path.substring(0, re) + "/Backups " + path.substring(e) + "/");
                    Path path2 = Paths.get(path1 + "/Copy Information.txt");
                 

                   //making the file of the
                    File back = new File(path.substring(0, re) + "/Backups " + path.substring(e) + "/" + time.replaceAll(":", ";") + "." + path.substring(e));
                   
                    //if the backup folder does not exists and the backup textfile does not exists it will crate both of them
                    if (!Files.exists(path1) & !Files.exists(path2)) {
                        Files.createDirectories(Paths.get(path.substring(0, re) + "Backups " + path.substring(e)));

                        Files.createFile(path2);
                        //if the fodler exists and the file does not exits it will crate the text file
                    } else if (Files.exists(path1) & !Files.exists(path2)) {
                        Files.createFile(path2);
                    }
                    
                    //seting the profress bar
            
                    //getting ready to rad from the text file
                    File txtFile = new File(path2.toString());
 
                    FileReader fileReader = new FileReader(txtFile);

                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    StringBuffer stringBuffer = new StringBuffer();

                    String line;
                    //reading from the text file and putting it to the String Line
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);

                    }
                    //closing the file
                    fileReader.close();
                    /*if the time modified in the text file is not the same as 
                    the lsat time modified it will copy the source file to the backup folder and 
                    rename the file to the current time and also update the text file*/
                    if (!stringBuffer.toString().equals(time)) {
                        FileChannel sourceChannel = null;
                        FileChannel destChannel = null;

                        sourceChannel = new FileInputStream(file).getChannel();
                        destChannel = new FileOutputStream(back).getChannel();
                        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

                        FileOutputStream textt = new FileOutputStream(path.substring(0, re) + "Backups " + path.substring(e) + "/Copy Information.txt", false); // true to append
                        // false to overwrite.
                        byte[] myBytes = time.getBytes();
                        textt.write(myBytes);

                        textt.close();

                        sourceChannel.close();
                        destChannel.close();

                    }
                    //aa.Progress.setValue(100);
                    //sleeping for the ammount of time the user specified in the UI
                    TimeUnit.SECONDS.sleep(interval);
                    //aa.Progress.setValue(0);
                }
            }

        } catch (Exception e) {

        }

    }
    
//calling the zip method
    public void CalZip() throws Exception {
        //passing all of the paths for the zip method.
        Path path4 = Paths.get(path.substring(0, re) + "Backups " + path.substring(e) + "/");

        Path path5 = Paths.get(path.substring(0, re) + "Backups " + path.substring(e)+" "+ time.replaceAll(":",";") + ".zip");

       
        aa.Zip1(path4,path5);

    }
    
    
    
   
}
