package com.ibm.xmq.cluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQException;

/**
 * Copyright 2018 IBM Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---------------------------------------------------------------------------- 
 * __  __                               _
 * \ \/ /_ __ ___   __ _  __ _ _ __ ___| |___
 *  \  /| '_ ` _ \ / _` |/ _` | '__/ __| / __|
 *  /  \| | | | | | (_| | (_| | | | (__| \__ \
 * /_/\_\_| |_| |_|\__, |\__, |_|  \___|_|___/
 *                    |_||___/           
 *
 * Xmqgrcls - Create a graph of IBM MQ cluster objects 
 * 
 * Xmqgrcls creates a graph of IBM MQ cluster objects based on data extracted
 * and saved in CSV files. The generated graph is saved into both GRAPHML and
 * JSON formats which can then be imported into various other tools to perform
 * queries or visualize the data.
 * 
 * Return codes:
 *      0 - Successful completion
 *     20 - A file could not be found or opened
 *     30 - An I/O exception occurred processing a file
 *     98 - Missing or invalid parameter was provided
 *     99 - No parameter provided, usage is displayed
 * 
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 *       
 */
public class Xmqgrcls {
	
	private static final Logger log = LoggerFactory.getLogger(Xmqgrcls.class);
	
	public static final String progName = "Xmqgrcls";
	public static final String progVersion = "1.0";
	public static final String progAuthor = "Oliver Fisse (IBM)";
	public static final String progAuthorEmail = "fisse@us.ibm.com";
	public static final String progCopyright = "(c) Copyright IBM Corp. 2018, all rights reserved";
	
	private String qmgrFileName;
	private String qmgrClusterFileName;
	private String queueClusterFileName;
	private String graphFileName;
	
	
	/**
	 * Constructor
	 *
	 */
	public Xmqgrcls() {
		
	}
		
	/**
	 * Parse command line arguments
	 * 
	 * @param args Command line arguments
	 * @return rc Return code
	 */
	private int parseArgs(String[] args) {
		
		log.trace("[{}] Entry {}.parseArgs, args={}", Thread.currentThread().getId(), this.getClass().getName(), args);
		
		int rc = 0;
		int argc = args.length;
		char c = ' ';
		
		System.out.println(Xmqgrcls.progName + " v" + Xmqgrcls.progVersion + " - Developed by " + Xmqgrcls.progAuthor);
		System.out.println(Xmqgrcls.progCopyright);
		System.out.println();
		
		// Parse arguments
		for (int i = 0; i < argc; i++) {
			
			if (args[i].startsWith("-")) {
				try {
					c = args[i].charAt(1);
				} catch (StringIndexOutOfBoundsException sioobe) {
					System.err.println("Missing option character for option " + (i + 1) + "!");
					rc = 98;
				}
				
				switch (c) {
					case 'c':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.qmgrClusterFileName = args[i + 1];
						i++;
						break;
					case 'g':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.graphFileName = args[i + 1];
						i++;
						break;
					case 'm':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.qmgrFileName = args[i + 1];
						i++;
						break;
					case 'q':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.queueClusterFileName = args[i + 1];
						i++;
						break;
					default: 
						System.err.println(args[i] + " is not a valid option!");
						rc = 98;
				} // end switch
			} else {
				System.err.println(args[i] + " is not a valid! Options should start with '-'.");
				rc = 98;
			} // end if
        } // end for
		
		if (rc == 0) {
			if (this.qmgrClusterFileName == null) {
				System.err.println("Missing option -c, queue manager cluster file name must be specified!");
				rc = 98;
			}
			
			if (this.qmgrFileName == null) {
				System.err.println("Missing option -m, queue manager file name must be specified!");
				rc = 98;
			}
			
			if (this.graphFileName == null) {
				System.err.println("Missing option -g, output graph file name must be specified!");
				rc = 98;
			}
		} // end if
		
		log.trace("[{}]  Exit {}.parseArgs, rc={}", Thread.currentThread().getId(), this.getClass().getName(), rc);
		
		return rc;
	}
	
	/**
	 * Run the tool
	 * 
	 */
	private int run() {
		
		log.trace("[{}] Entry {}.run", Thread.currentThread().getId(), this.getClass().getName());
		
		MQGraphBuilder gb = null;
		
		MQException.log = null;
		
		BufferedReader qmData = null;
		BufferedReader qmClusterQueueManagerData = null;
		BufferedReader qmClusterQueueData = null;
		
		String record; 
		
		int rc = 0;
			
		try {
			// Create and open an empty graph
			gb = new MQGraphBuilder();
				
			// Open CSV files to be read
			qmData = new BufferedReader(new FileReader(this.qmgrFileName));
			qmClusterQueueManagerData = new BufferedReader(new FileReader(this.qmgrClusterFileName));
			if (this.queueClusterFileName != null) qmClusterQueueData = new BufferedReader(new FileReader(this.queueClusterFileName));
				
			System.out.println("Building the graph using the CSV data files...");
				
			// Process queue manager data
			System.out.println("  ->> Processing file '" + this.qmgrFileName + "'...");
				
			while ((record = qmData.readLine()) != null) {
				MQQueueManagerData qmd = new MQQueueManagerData();
				qmd.fromCSV(record);
					
				// Add the queue manager to the graph
				gb.addQueueManager(qmd);
			} // end while
				
			// Process cluster queue manager data
			System.out.println("  ->> Processing file '" + this.qmgrClusterFileName + "'...");
				
			while ((record = qmClusterQueueManagerData.readLine()) != null) {
				MQClusterQueueManagerData cqmd = new MQClusterQueueManagerData();
				cqmd.fromCSV(record);
					
				// Add the queue manager connection to the graph
				gb.addClusteredQueueManager(cqmd);
			} // end while
				
			// Process cluster queue data
			if (this.queueClusterFileName != null) {
				System.out.println("  ->> Processing file '" + this.queueClusterFileName + "'...");
					
				while ((record = qmClusterQueueData.readLine()) != null) {
					MQClusterQueueData cqd = new MQClusterQueueData();
					cqd.fromCSV(record);
						
					// Add the cluster queue relation to the graph
					gb.addClusteredQueue(cqd);
				} // end while
			} // end if
				
			System.out.println();
				
			// Format and Write graph to files
			System.out.println("Writing graph (GRAPHML) to file '" + this.graphFileName + ".graphml'...");
			gb.writeGraphMLToFile(this.graphFileName + ".graphml");		
			System.out.println("Writing graph (JSON) to file '" + this.graphFileName + ".json'...");
			gb.writeGraphJSONToFile(this.graphFileName + ".json");
						
			System.out.println();
			System.out.println("All done folks!");
				
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException - " + fnfe.getLocalizedMessage());
			rc = 20;
		} catch (IOException ioe) {
			System.err.println("IOException - " + ioe.getLocalizedMessage());
			rc = 30;
		} finally {	
			// Close CSV files
			try {
				if (qmData != null) qmData.close();
				if (qmClusterQueueData != null) qmClusterQueueData.close();
				if (qmClusterQueueManagerData != null) qmClusterQueueManagerData.close();
			} catch (IOException ioe) {
				// ignore
			}
				
			// Close graph
			if (gb != null) gb.closeGraph();
		}
		
		log.trace("[{}]  Exit {}.run, rc={}", Thread.currentThread().getId(), this.getClass().getName(), rc);
		
		return rc;
	}
	
	/**
	 * Display usage
	 * 
	 */
	private static void usage() {
		
		System.out.println("__  __                               _");
		System.out.println("\\ \\/ /_ __ ___   __ _  __ _ _ __ ___| |___");
		System.out.println(" \\  /| '_ ` _ \\ / _` |/ _` | '__/ __| / __|");
		System.out.println(" /  \\| | | | | | (_| | (_| | | | (__| \\__ \\");
		System.out.println("/_/\\_\\_| |_| |_|\\__, |\\__, |_|  \\___|_|___/");
		System.out.println("                   |_||___/");                
        System.out.println();
		
		System.out.println(Xmqgrcls.progName + " v" + Xmqgrcls.progVersion + " - Create a graph of IBM MQ cluster objects");
		System.out.println(Xmqgrcls.progCopyright);
		System.out.println();
		System.out.println(Xmqgrcls.progName + " creates a graph of IBM MQ cluster objects based on data extracted");
		System.out.println("and saved in CSV files. The generated graph is saved into both GRAPHML and");
		System.out.println("JSON formats which can then be imported into various other tools to perform");
		System.out.println("queries or visualize the data.");
		System.out.println();
		
		System.out.println("Usage: " + Xmqgrcls.progName + " -g graph-file -c qmgr-cluster-file -m qmgr-file");
		System.out.println("                [-q q-cluster-file]");
		System.out.println();
		
		System.out.println("Options:");
		System.out.println("    -c qmgr-cluster-file  Cluster queue manager data file name");
		System.out.println("    -g graph-file         Generate graph and save it to files (no extension)");
		System.out.println("    -m qmgr-file          Queue manager data file name");
		System.out.println("    -q q-cluster-file     Cluster queue data file name");
		System.out.println();
		
		System.out.println("Send bug reports, comments, etc... to " + Xmqgrcls.progAuthor + " at "+ Xmqgrcls.progAuthorEmail);
		System.out.println();
		  
		System.out.println("This tool is provided in good faith and AS-IS. There is no warranty");
		System.out.println("or further service implied or committed via IBM product service channels.");
	} // end of method usage()
	
	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		int rc = 0;
		int argc = args.length;
		
		// Display usage if no argument, ?, -?, -h or --help is passed
		if (argc == 0 || 
		   (argc == 1 && (args[0].compareTo("?") == 0 ||
						  args[0].compareTo("-?") == 0 ||
						  args[0].compareTo("-h") == 0 ||
						  args[0].compareTo("--help") == 0))) {
			usage();
			rc = 99;
		} else {
			// Execute the tool
			Xmqgrcls util = new Xmqgrcls();
			if ((rc = util.parseArgs(args)) == 0) rc = util.run();
		}
		
		System.exit(rc);
	} 

}
