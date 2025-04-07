package app;

import java.io.File;
import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import util.HibernateUtil;

public class App {

	public static void main(String[] args) {
		  System.out.println("Initializing Hibernate...");
	        HibernateUtil.getSessionFactory(); // This should create tables if configured correctly
	        System.out.println("Hibernate initialized");
	        
	        Tomcat tomcat = new Tomcat();
	        tomcat.setPort(8088);


	        String projectRoot;
			try {
				projectRoot = new File(".").getCanonicalPath();
				String docBase = new File(projectRoot, "src/main/webapp").getAbsolutePath();
				File docBaseFile = new File(docBase);
				System.out.println("Project Root: " + projectRoot);
				System.out.println("DocBase: " + docBase);
				System.out.println("DocBase exists: " + docBaseFile.exists());
				System.out.println("DocBase is directory: " + docBaseFile.isDirectory());
				Context context = tomcat.addContext("", docBase);
				context.addWelcomeFile("/");
				Tomcat.addServlet(context, "default", new DefaultServlet());
				context.addServletMappingDecoded("/*", "default");
				
				// Ensure static files are served properly
				context.setResources(new org.apache.catalina.webresources.StandardRoot(context));
				
				// Add servlets with mappings
				tomcat.addServlet("", "RegisterServlet", "controller.RegisterServlet");
				context.addServletMappingDecoded("/register", "RegisterServlet");
				
				tomcat.addServlet("", "LoginServlet", "controller.LoginServlet");
				tomcat.addServlet("", "AccountServlet", "controller.AccountServlet");
				context.addServletMappingDecoded("/login", "LoginServlet");
				context.addServletMappingDecoded("/account", "AccountServlet");
				
				
				// Ensure connector is initialized
				tomcat.getConnector(); // This forces connector setup
				
				System.out.println("Starting Tomcat...");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	       // System.out.println("Checking test.html: " + new File(docBaseFile, "test.html").exists());
	        

	        // Add DefaultServlet for static files correctly
	        try {
				tomcat.start();
			} catch (LifecycleException e) {
				
				e.printStackTrace();
			}
	        System.out.println("Tomcat started on http://localhost:8088");
	        tomcat.getServer().await();
	    }
	


	}


