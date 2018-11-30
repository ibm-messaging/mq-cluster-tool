package com.ibm.xmq.cluster;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
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
 * 
 * MQGraphBuilder Class
 *
 * Build a graph of an MQ Cluster
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQGraphBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(MQGraphBuilder.class);
	
	TinkerGraph tg;
	GraphTraversalSource g;
	Vertex v1, v2, v3;
	
	
	/**
	 * Constructor
	 *
	 */
	public MQGraphBuilder() {
		
		log.trace("[{}] Entry {}.constructor", Thread.currentThread().getId(), this.getClass().getName());
		
		this.tg = TinkerGraph.open();
		this.g = tg.traversal();
		
		log.trace("[{}] Exit {}.constructor", Thread.currentThread().getId(), this.getClass().getName());
	}
	
    public void addQueueManager(MQQueueManagerData qmd) {
    	
    	log.trace("[{}] Entry {}.addQueueManager, qm data={}", Thread.currentThread().getId(), this.getClass().getName(), qmd);
    	
    	// Add a vertex (queue manager) to the graph
		try {
			v1 = g.V().has("qm", "name", qmd.getQmName()).next();
			v1.property("description", qmd.getDescription());
			v1.property("platform", qmd.getPlatform());
			v1.property("cmdlevel", qmd.getCommandLevel());
			if (qmd.getCommandLevel() < 710) v1.property("version", qmd.getCommandLevel());
			else v1.property("version", qmd.getVersion());
		} catch (NoSuchElementException nsee) {
			v1 = tg.addVertex(T.label, "qm",
					"tag", qmd.getTag(),
					"name", qmd.getQmName(),
					"qmid", qmd.getQmId(),
					"description", qmd.getDescription(),
					"cmdlevel", qmd.getCommandLevel(),
					"platform", qmd.getPlatform(),
					"version", qmd.getVersion());
		}
		
		log.trace("[{}] Exit {}.addQueueManager", Thread.currentThread().getId(), this.getClass().getName());
    }
    
    public void addClusteredQueueManager(MQClusterQueueManagerData cqmd) {
    	
    	log.trace("[{}] Entry {}.addClusteredQueueManager, cluster queue manager data={}", Thread.currentThread().getId(), this.getClass().getName(), cqmd);
    	
    	// Add an edge (connects) to the graph
		if (cqmd.getDefinitionType().compareTo("MQQMDT_AUTO_CLUSTER_SENDER") == 0) {
			
			try {
				v1 = g.V().has("qm", "name", cqmd.getSourceQmName()).next();
			} catch(NoSuchElementException nsee) {
				System.err.println("NoSuchElementException - " + nsee.getLocalizedMessage());
				nsee.printStackTrace();
			}
		
			try {
				v2 = g.V().has("qm", "name", cqmd.getQmName()).next();
			} catch (NoSuchElementException nsee) {
				v2 = tg.addVertex(T.label, "qm",
					"tag", cqmd.getTag(),
			    	"name", cqmd.getQmName(),
			    	"qmid", cqmd.getQmId(),
					"version", cqmd.getVersion());
			}
						
			v1.addEdge("connects", v2,
				"tag", cqmd.getTag(),
				"SourceQM", cqmd.getSourceQmName(),
				"name", cqmd.getQmName(),
				"qmid", cqmd.getQmId(),
				"qmtype", cqmd.getQmType(),
				"cluster", cqmd.getCluster(),
				"deftype", cqmd.getDefinitionType(),
				"channel", cqmd.getChannelName(),
				"connection", cqmd.getConnectionName(),
				"status", cqmd.getStatus(),
				"sslcauth", cqmd.getSslCAuth(),
				"sslciph", cqmd.getSslCiph(),
				"sslpeer", cqmd.getSslPeer(),
				"xmitq", cqmd.getXmitQ(),
				"version", cqmd.getVersion());
		}
		
		log.trace("[{}] Exit {}.addClusteredQueueManagerr", Thread.currentThread().getId(), this.getClass().getName());
    }
    
    public void addClusteredQueue(MQClusterQueueData cqd) {
    	
    	log.trace("[{}] Entry {}.addClusteredQueue, cluster queue data={}", Thread.currentThread().getId(), this.getClass().getName(), cqd);
    	
    	try {
			v1 = g.V().has("qm", "name", cqd.getQmName()).next();
		} catch(NoSuchElementException nsee) {
			System.err.println("NoSuchElementException - " + nsee.getLocalizedMessage());
			nsee.printStackTrace();
		}
    	
    	try {
			v3 = g.V().has("queue", "qmname",  cqd.getQmName())
					.has("queue", "queue",  cqd.getQueue())
					.next();
		} catch (NoSuchElementException nsee) {
			v3 = tg.addVertex(T.label, "queue",
				"qmname", cqd.getQmName(),
		    	"name", cqd.getQueue(),
		    	"cluster", cqd.getCluster(),
				"clusqmgr", cqd.getClusQmgr(),
				"clusqt", cqd.getClusQT(),
				"clWlPrty", cqd.getClWlPrty(),
				"clWlRank", cqd.getClWlRank(),
				"defbind", cqd.getDefBind(),
				"descr", cqd.getDescr(),
				"put", cqd.getPut(),
				"qmid", cqd.getQmId());
		}
		
    	if (cqd.getQmName().compareTo(cqd.getClusQmgr()) == 0) v1.addEdge("owns", v3);
    	else v1.addEdge("uses", v3);
    	
    	log.trace("[{}] Exit {}.addClusteredQueue", Thread.currentThread().getId(), this.getClass().getName());
    }
    
    public void closeGraph() {
    	
    	log.trace("[{}] Entry {}.closeGraph", Thread.currentThread().getId(), this.getClass().getName());
    	
    	try {
    		this.g.close();
    		this.tg.close();
    	} catch (Exception e) {
    		// ignore
    	}
    	
    	log.trace("[{}] Exit {}.closeGraph", Thread.currentThread().getId(), this.getClass().getName());
    }
    
    public void writeGraphMLToFile(String fileName) 
    	throws IOException {
    	
    	log.trace("[{}] Entry {}.writeGraphMLToFile", Thread.currentThread().getId(), this.getClass().getName());
    	
		this.tg.io(IoCore.graphml()).writeGraph(fileName);
		
		log.trace("[{}] Exit {}.writeGraphMLToFile", Thread.currentThread().getId(), this.getClass().getName());
    }
    
    public void writeGraphJSONToFile(String fileName) 
        throws IOException {
    	
    	log.trace("[{}] Entry {}.writeJSONToFile", Thread.currentThread().getId(), this.getClass().getName());
        	
    	this.tg.io(IoCore.graphson()).writeGraph(fileName);
    	
    	log.trace("[{}] Exit {}.writeJSONToFile", Thread.currentThread().getId(), this.getClass().getName());
    }

}
