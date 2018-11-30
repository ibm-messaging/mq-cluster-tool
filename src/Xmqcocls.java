package com.ibm.xmq.cluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.ibm.mq.MQException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * \ \/ /_ __ ___   __ _  ___ ___   ___| |___
 *  \  /| '_ ` _ \ / _` |/ __/ _ \ / __| / __|
 *  /  \| | | | | | (_| | (_| (_) | (__| \__ \
 * /_/\_\_| |_| |_|\__, |\___\___/ \___|_|___/
 *                    |_|   
 *
 * Xmqcocls - Collect IBM MQ cluster data to build a graph of cluster objects 
 * 
 * Xmqcocls collects IBM MQ cluster data and optionally build a graph of cluster
 * objects. Data collected is also stored in several CSV files that can be used 
 * to build a graph using a separate tool. If a graph is generated, it is saved
 * in both GRAPHML and JSON formats. The graph can then be imported into various
 * other tools to either perform queries or visualize the data.
 * 
 * Return codes:
 *      0 - Successful completion
 *      1 - Partially successful completion
 *     10 - No matching tag found in the queue manager inventory
 *     20 - A file could not be found or opened
 *     30 - An I/O exception occurred processing a file
 *     98 - Missing or invalid parameter was provided
 *     99 - No parameter provided, usage is displayed
 *     
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 *       
 */
public class Xmqcocls {
	
	private static final Logger log = LoggerFactory.getLogger(Xmqcocls.class);
	
	public static final String progName = "Xmqcocls";
	public static final String progVersion = "1.0";
	public static final String progAuthor = "Oliver Fisse (IBM)";
	public static final String progAuthorEmail = "fisse@us.ibm.com";
	public static final String progCopyright = "(c) Copyright IBM Corp. 2018, all rights reserved";
	
	private String csvFilePrefix;
	private String qmDataFileName;
	private String qmClusterQueueManagerFileName;
	private String qmClusterQueueDataFileName;
	
	private String inventoryFileName;
	private ArrayList<String> tagList;
	private boolean generateGraph;
	private String graphFileName;
	private boolean collectClusterQueues;
	private String cipherSuite;
	private String userId;
	private String password;
	
	
	/**
	 * Constructor
	 *
	 */
	public Xmqcocls() {
		
		this.csvFilePrefix = "";
		this.collectClusterQueues = false;
		this.tagList = new ArrayList<String>();
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
		
		System.out.println(Xmqcocls.progName + " v" + Xmqcocls.progVersion + " - Developed by " + Xmqcocls.progAuthor);
		System.out.println(Xmqcocls.progCopyright);
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
						this.cipherSuite = args[i + 1];
						i++;
						break;
					case 'f':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.inventoryFileName = args[i + 1];
						i++;
						break;
					case 'g':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.graphFileName = args[i + 1];
						this.generateGraph = true;
						i++;
						break;
					case 'p':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.password = args[i + 1];
						i++;
						break;
					case 'q':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							rc = 98;
						}
						this.collectClusterQueues = true;
						break;
					case 't':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						
						while (i + 1 < argc && !args[i + 1].startsWith("-")) {
							this.tagList.add(args[i + 1]);
							i++;
						} // end while
						break;
					case 'u':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.userId = args[i + 1];
						i++;
						break;
					case 'x':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							rc = 98;
						}
						this.csvFilePrefix = args[i + 1];
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
			if (this.inventoryFileName == null) {
				System.err.println("Missing option -f, queue manager inventory file name must be specified!");
				rc = 98;
			} else {
				String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss"));
				
				this.qmDataFileName = this.csvFilePrefix + "qmdata-" + timestamp + ".csv";
				this.qmClusterQueueManagerFileName = this.csvFilePrefix + "clusqmdata-" + timestamp + ".csv";
				this.qmClusterQueueDataFileName = this.csvFilePrefix + "clusqueuedata-" + timestamp + ".csv";
			}
		}
		
		log.trace("[{}]  Exit {}.parseArgs, rc={}", Thread.currentThread().getId(), this.getClass().getName(), rc);
		
		return rc;
	}
	
	/**
	 * Run the tool
	 * 
	 */
	private int run() {
		
		log.trace("[{}] Entry {}.run", Thread.currentThread().getId(), this.getClass().getName());
		
		MQQueueManagerInventoryData inventoryEntry;
		MQDataCollector dc;
		MQGraphBuilder gb = null;
		
		MQException.log = null;
		
		int totalQMSuccess = 0;
		int totalQMFailure = 0;
		
		double percentComplete;
		
		PrintWriter qmDataWriter = null;
		PrintWriter qmClusterDataWriter = null;
		PrintWriter qmClusterQueueDataWriter = null;
		
		int rc = 0;
			
		try {
			System.out.println("Reading queue manager inventory from file '" + this.inventoryFileName + "'...");
			if (this.tagList.size() != 0) {
				System.out.println("Matching the following tags...");
				for (String tag : tagList) System.out.println("   --> " + tag);
			}
				
			// Read the queue manager inventory
			MQQueueManagerInventory inventory = new MQQueueManagerInventory(this.inventoryFileName, this.tagList);
				
			if (inventory.getTotalInventoryEntries() == 0) {
				System.out.println("No matching tag found in the inventory, no work to be done!");
				rc = 10;
			}
			
			if (rc == 0) {
				System.out.println(inventory.getTotalInventoryEntries() + " queue managers found in the inventory, let's go we got work to do!");
				System.out.println();

				// Open CSV files
				qmDataWriter = new PrintWriter(this.qmDataFileName);
				qmClusterDataWriter = new PrintWriter(this.qmClusterQueueManagerFileName);
				if (this.collectClusterQueues) qmClusterQueueDataWriter = new PrintWriter(this.qmClusterQueueDataFileName);
				
				// Create and open an empty graph
				if (this.generateGraph) gb = new MQGraphBuilder();
				
				if (this.generateGraph) System.out.println(LocalTime.now() + " - Collecting queue managers cluster information and building graph...");
				else System.out.println(LocalTime.now() + " - Collecting queue managers cluster information...");
				
				// Collect data for all queue managers in the inventory
				while ((inventoryEntry = inventory.next()) != null) {
					dc = new MQDataCollector();
					if (dc.collect(inventory, inventoryEntry, this.collectClusterQueues, this.userId, this.password, this.cipherSuite)) totalQMSuccess++;
					else { 
						totalQMFailure++;
						rc = 1;
						continue;
					}
					
					// Process queue manager data
					qmDataWriter.println(dc.getQmData().toCSV());
					
					// Add vertices (queue managers) to the graph
					if (this.generateGraph) gb.addQueueManager(dc.getQmData());
						
					// Process cluster queue manager data
					for (MQClusterQueueManagerData cqmd : dc.getClusterQueueManagerData()) {
						// Write data to CSV file
						qmClusterDataWriter.println(cqmd.toCSV());
						
						// Add edges (connects) between queue managers to the graph
						if (this.generateGraph) gb.addClusteredQueueManager(cqmd);
					} // end for
					
					// Process cluster queue  data
					if (this.collectClusterQueues) {
						for (MQClusterQueueData cqd : dc.getClusterQueueData()) {
							// Write data to CSV file
							qmClusterQueueDataWriter.println(cqd.toCSV());
							
							// Add edges (owns, uses) from queue managers to cluster queues to the graph
							if (this.generateGraph) gb.addClusteredQueue(cqd);
						} // end for
					} // end if
			
					// Display progress
					percentComplete = ((double)(totalQMSuccess + totalQMFailure) / inventory.getTotalInventoryEntries()) * 100.0;
					if (percentComplete % 10 == 0) {
						System.out.println(LocalTime.now() + " - " + (int)percentComplete + "% complete" + " - " + inventory.getCurrentInventoryEntry() + " queue managers processed");
					} // end if		
				} // while
				
				System.out.println();
				System.out.println("Total queue managers processed successfully: " + totalQMSuccess);
				System.out.println("Total queue managers that could not be processed: " + totalQMFailure);
				System.out.println();
				
				if (totalQMSuccess != 0) {
					System.out.println("Queue manager data written to file: " + this.qmDataFileName);
					System.out.println("Queue manager cluster data written to file: " + this.qmClusterQueueManagerFileName);
					if (this.collectClusterQueues) System.out.println("Queue cluster data written to file: " + this.qmClusterQueueDataFileName);
				}
				
				System.out.println();
				
				// Format and write graph to files
				if (this.generateGraph) {
					System.out.println("Writing graph (GRAPHML) to file '" + this.graphFileName + ".graphml'...");
					gb.writeGraphMLToFile(this.graphFileName + ".graphml");
					System.out.println("Writing graph (JSON) to file '" + this.graphFileName + ".json'...");
					gb.writeGraphJSONToFile(this.graphFileName + ".json");
					System.out.println();
				} // end if
				
				System.out.println("All done folks!");
			} // end if
				
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException - " + fnfe.getLocalizedMessage());
			rc = 20;
		} catch (IOException ioe) {
			System.err.println("IOException - " + ioe.getLocalizedMessage());
			rc = 30;
		} finally {	
			// Close CSV files
			if (qmDataWriter != null) qmDataWriter.close();
			if (qmClusterDataWriter != null) qmClusterDataWriter.close();
			if (qmClusterQueueDataWriter != null) qmClusterQueueDataWriter.close();
				
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
		System.out.println("\\ \\/ /_ __ ___   __ _  ___ ___   ___| |___");
		System.out.println(" \\  /| '_ ` _ \\ / _` |/ __/ _ \\ / __| / __|");
		System.out.println(" /  \\| | | | | | (_| | (_| (_) | (__| \\__ \\");
		System.out.println("/_/\\_\\_| |_| |_|\\__, |\\___\\___/ \\___|_|___/");
		System.out.println("                   |_|");             
        System.out.println();
		
		System.out.println(Xmqcocls.progName + " v" + Xmqcocls.progVersion + " - Collect IBM MQ cluster data to build a graph of cluster objects");
		System.out.println(Xmqcocls.progCopyright);
		System.out.println();
		System.out.println(Xmqcocls.progName + " collects IBM MQ cluster data and optionally build a graph of cluster");
		System.out.println("objects. Data collected is also stored in several CSV files that can be used"); 
		System.out.println("to build a graph using a separate tool. If a graph is generated, it is saved");
		System.out.println("in both GRAPHML and JSON formats. The graph can then be imported into various");
		System.out.println("other tools to either perform queries or visualize the data.");
		System.out.println();
		
		System.out.println("Usage: " + Xmqcocls.progName + " -f inv-file [-g graph-file] [-q] [-x csv-file-prefix]");
		System.out.println("                [-t tag-list] [-c cipher-suite] [-u user-id [-p password]]");
		System.out.println();
		
		System.out.println("Options:");
		System.out.println("    -c cipher-suite    Cipher suite for SSL/TLS connection");
		System.out.println("    -f inv-file        Queue manager inventory file name");
		System.out.println("    -g graph-file      Generate graph and save it to files (no extension)");
		System.out.println("    -p password        Password associated with user-id for authentication");
		System.out.println("    -q                 Collect cluster queue information");
		System.out.println("    -t tag-list        List of tags to process");
		System.out.println("    -u user-id         User-id for authentication/authorization");
		System.out.println("    -x csv-file-prefix File name prefix for the CSV files");
		System.out.println();
		
		System.out.println("    Inventory file should follow the structure below for each queue manager:");
		System.out.println("       tag:qm_name:hostname(port),...:channel_name");
		System.out.println();
		
		System.out.println("    tag-list: list of one or more tags separated by space");
		System.out.println();
		System.out.println("    Wildcards * and ? may be used for tags. * replaces one or more");
		System.out.println("    characters, ? replaces a single character.");
		System.out.println();
		
		System.out.println("Send bug reports, comments, etc... to " + Xmqcocls.progAuthor + " at "+ Xmqcocls.progAuthorEmail);
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
			Xmqcocls util = new Xmqcocls();
			if ((rc = util.parseArgs(args)) == 0) rc = util.run();
		}
		
		System.exit(rc);
	} 

}
