package org.javahacking.jhbl.io;

import org.javahacking.jhbl.data.ClassPool;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Constructs a simplified representation of a JAR file.
 *
 * @author trDna
 * @since 1.7
 */
public class JarConstruct {

    /**
     * The URLClassLoader that loads the JAR from a given path.
     */
    private URLClassLoader url;

    /**
     * This is where the classes are stored. For use only in the loader package!
     */
    private HashMap<String, ClassNode> loadedClassNodes = new HashMap<>();

    /**
     * The JAR's path.
     */
    private String jarPath;

    /**
     * The JAR's URL
     */
    private String jarUrl;

    /**
     * The JAR's URL path.
     */
    private URL jarUrlPath;

    /**
     * Acts as a container to store classes.
     */
    private ClassPool cPool;


    /**
     * Creates a representation of a loaded jar.
     *
     * @param jarPath The path to the JAR.
     */
    public JarConstruct(final String jarPath){
        try {

            //Set up logger and print out the path.
            System.out.println("JarConstruct " + getClass().hashCode());
            System.out.println("Path to JAR: " + jarPath);

            //Creates a new URLClassLoader and loads the JAR.
            jarUrlPath = new URL("file:" + jarPath);

            //Referencing the JAR's path.
            this.jarPath = jarPath;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a representation of a loaded JAR.
     *
     * @param jarUrl The URL to the JAR.
     * @param flags For future use
     *
     */
    public JarConstruct(final String jarUrl, int flags){
        try {

            //Set up logger and print out the path.
            System.out.println("JarConstruct " + getClass().hashCode());
            System.out.println("Path to JAR: " + (jarUrl + "!").replace("http://", "file:"));

            //Creates a new URLClassLoader and loads the JAR.
            url = new URLClassLoader(new URL[]{new URL((jarUrl + "!/").replace("http://", "jar:http://"))});

            //Referencing the JAR's URL.
            this.jarUrl = jarUrl;

            jarUrlPath = new URL((jarUrl + "!/").replace("http://", "jar:http://"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all classes from the given JAR file and stores them into a HashMap for future use.
     *
     * @return True if it was successful, False if it was not.
     */
    public boolean loadClasses(){
        short count = 0;

        try {
            //Startup
            p("---------------------------------------------------------------------");
            p("--------------------      Jar Loader     ----------------------------");
            p("File: " + jarPath);

            p("JC Hash: " + getClass().hashCode());


            JarFile jf;

            //Referencing the JAR file.
            if(jarUrl != null) {

                System.out.println(jarUrlPath.toString());

                //Connect to the JAR directly online
                JarURLConnection u = (JarURLConnection) jarUrlPath.openConnection();

                //Get the JarFile representation from the JarURLConnection
                jf = u.getJarFile();

            } else {

                //If the if statement leads here, then the JAR is being run locally
                jf = new JarFile(jarPath);

            }

            //Make sure that a non-null value was passed in from either constructor
            assert jarPath != null || jarUrl != null : "Jar loader failure!";


            //Print out the size of the JarFile
            p("JarFile Size = " + jf.size());
            p("-----------------------------------------------------------------");

            //Referencing the entries.
            Enumeration<? extends JarEntry> en = jf.entries();

            //Looping through the elements (the entries).
            while(en.hasMoreElements()){

                //The entry to work with.
                JarEntry entry = en.nextElement();

                //Grabbing solely the class files
                if (entry.getName().endsWith(".class")) {

                    //Count out the entries
                    ++count;

                    //Print out the entry
                    p("Entry " + count + ") " + entry.getName());
                    p(" -> Decompressed Size = " + entry.getSize() + " Bytes" + "\n");

                    //ClassReader retrieves the bytes from a given entry.
                    ClassReader cr = new ClassReader(jf.getInputStream(entry));

                    //Creating a new ClassNode to act as a representative of a class.
                    ClassNode cn = new ClassNode();

                    //Delegating all method calls and data from ClassReader to ClassNode.
                    //Think of it as data from 'cr' are being entrusted or put into 'cn' (such as the class bytes).
                    cr.accept(cn, 0);

                    System.out.println("");

                    //Put it into the local package's HashMap as a ClassNode.
                    loadedClassNodes.put(cn.name, cn);
                }
            }




            System.out.println(count + " classes were loaded and stored!");
            p("-----------------------------------------------------------------");
            p("-----------------------------------------------------------------");

            System.out.println("Load successful.");



        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Retrieves the loaded classes as a {@link ClassPool}
     *
     * @return A {@link ClassPool} containing the loaded classes.
     */
    public ClassPool getClassPool(){
        return cPool == null ? (cPool = new ClassPool(loadedClassNodes)) : cPool;
    }


    private void p(Object msg){
        System.out.println(msg);
    }


}

