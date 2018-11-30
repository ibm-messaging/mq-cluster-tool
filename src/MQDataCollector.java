package com.ibm.xmq.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;

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
 * MQDataCollector Class
 *
 * Collect MQ data for a queue manager
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQDataCollector {
	
	private static final Logger log = LoggerFactory.getLogger(MQDataCollector.class);
	
	private MQQueueManagerData qmData;
	private List<MQClusterQueueManagerData> qmClusterQueueManagerData;
	private List<MQClusterQueueData> qmClusterQueueData;
	
	
	/**
	 * Constructor
	 *
	 */
	public MQDataCollector() {
		
		log.trace("[{}] Entry {}.constructor", Thread.currentThread().getId(), this.getClass().getName());
		
		this.qmData = new MQQueueManagerData();
		this.qmClusterQueueManagerData = new ArrayList<MQClusterQueueManagerData>();
		this.qmClusterQueueData = new ArrayList<MQClusterQueueData>();
		
		log.trace("[{}] Exit {}.constructor", Thread.currentThread().getId(), this.getClass().getName());
	}
	
	public boolean collect(MQQueueManagerInventory inventory, MQQueueManagerInventoryData inventoryEntry, 
			boolean collectClusterQueues, String userId, String password, String sslCipherSuite) {
		
		log.trace("[{}] Entry {}.collect, entry={}, clusterQueues={}, userId={}, password={}, cipherSuite={}", Thread.currentThread().getId(), this.getClass().getName(), inventoryEntry, collectClusterQueues, userId, password, sslCipherSuite);
		
		MQAgent mqAgent = null;
		PCFMessage[] pcfResp;
		MQClusterQueueManagerData mqcd;
		MQClusterQueueData mqcqd;
		
		boolean rc = true;
		
		try {
			// Connect to queue manager
			mqAgent = new MQAgent();
			mqAgent.setQmName(inventoryEntry.getQmName());
			mqAgent.setChannelName(inventoryEntry.getChannelName());
			mqAgent.setUserId(userId);
			mqAgent.setPassword(password);
			mqAgent.setSslCipherSuite(sslCipherSuite);
			mqAgent.setConnNameList(inventoryEntry.getConnNameList());
		
			mqAgent.connect();
		
			// Collect queue manager information
			PCFMessage inqQueueManager = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_MGR);
			inqQueueManager.addParameter(CMQCFC.MQIACF_Q_MGR_ATTRS, new int[] { CMQCFC.MQIACF_ALL });	
							
			pcfResp = mqAgent.submitPCF(inqQueueManager);
		
			this.qmData.setQmName(StringUtils.rtrim(pcfResp[0].getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)));
			this.qmData.setQmId(StringUtils.rtrim(pcfResp[0].getStringParameterValue(CMQC.MQCA_Q_MGR_IDENTIFIER)));
			this.qmData.setDescription(StringUtils.rtrim(pcfResp[0].getStringParameterValue(CMQC.MQCA_Q_MGR_DESC)));
			this.qmData.setPlatform(MQConstants.lookup(pcfResp[0].getIntParameterValue(CMQC.MQIA_PLATFORM), "MQPL_.*"));
			this.qmData.setCommandLevel(pcfResp[0].getIntParameterValue(CMQC.MQIA_COMMAND_LEVEL));
			this.qmData.setTag(inventoryEntry.getTag());
		
			// For queue managers at version greater than 7.0.1 the VERSION attribute is available
			if (this.qmData.getCommandLevel() > 701) {
				this.qmData.setVersion(pcfResp[0].getStringParameterValue(CMQC.MQCA_VERSION));
			} else this.qmData.setVersion("" + this.qmData.getCommandLevel());
		
			// Collect queue manager cluster information
			PCFMessage inqClusterQueueManager = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CLUSTER_Q_MGR);
			inqClusterQueueManager.addParameter(CMQC.MQCA_CLUSTER_Q_MGR_NAME, "*");
			inqClusterQueueManager.addParameter(CMQCFC.MQIACF_CLUSTER_Q_MGR_ATTRS, new int[] { CMQCFC.MQIACF_ALL });	
		
			pcfResp = mqAgent.submitPCF(inqClusterQueueManager);
		
			for (PCFMessage resp : pcfResp) {
				mqcd = new MQClusterQueueManagerData();
				mqcd.setQmName(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_CLUSTER_Q_MGR_NAME)));
				mqcd.setQmId(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_Q_MGR_IDENTIFIER)));
				mqcd.setQmType(MQConstants.lookup(resp.getIntParameterValue(CMQCFC.MQIACF_Q_MGR_TYPE), "MQQMT_.*"));
				mqcd.setCluster(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_CLUSTER_NAME)));
				mqcd.setChannelName(StringUtils.rtrim(resp.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME)));
				mqcd.setConnectionName(StringUtils.rtrim(resp.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME)));
				mqcd.setDefinitionType(MQConstants.lookup(resp.getIntParameterValue(CMQCFC.MQIACF_Q_MGR_DEFINITION_TYPE), "MQQMDT_.*"));
				mqcd.setSslCAuth(MQConstants.lookup(resp.getIntParameterValue(CMQCFC.MQIACH_SSL_CLIENT_AUTH), "MQSCA_.*"));
				mqcd.setSslCiph(StringUtils.rtrim(resp.getStringParameterValue(CMQCFC.MQCACH_SSL_CIPHER_SPEC)));
				mqcd.setSslPeer(StringUtils.rtrim(resp.getStringParameterValue(CMQCFC.MQCACH_SSL_PEER_NAME)));
			
				// This is weird... if CLUSRCVR channel then channel status is not returned (failed with 3014) event though the field is part of the response!!
				// Not a big deal since CLUSRCVR are ignored for the graph
				if (resp.getIntParameterValue(CMQCFC.MQIACF_Q_MGR_DEFINITION_TYPE) != CMQCFC.MQQMDT_CLUSTER_RECEIVER) mqcd.setStatus(MQConstants.lookup(resp.getIntParameterValue(CMQCFC.MQIACH_CHANNEL_STATUS), "MQCHS_.*"));
				else mqcd.setStatus("");
				
				if (this.qmData.getCommandLevel() > 710) mqcd.setXmitQ(StringUtils.rtrim(resp.getStringParameterValue(CMQCFC.MQCACH_XMIT_Q_NAME)));
				else mqcd.setXmitQ("SYSTEM.CLUSTER.TRANSMIT.QUEUE"); 
				
				if (this.qmData.getCommandLevel() > 750) {
					String version = resp.getStringParameterValue(CMQC.MQCA_VERSION);
					if (version.compareTo("") == 0) mqcd.setVersion("unknown");
					else mqcd.setVersion(version);
				}
				else mqcd.setVersion("unknown");
				mqcd.setSourceQmName(inventoryEntry.getQmName());
				mqcd.setTag(inventory.getTag(mqcd.getQmName()));
			
				this.qmClusterQueueManagerData.add(mqcd);
			}
			
			// Collect cluster queue information
			if (collectClusterQueues) {
				PCFMessage inqClusterQueue = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
				inqClusterQueue.addParameter(CMQC.MQCA_Q_NAME, "*");
				inqClusterQueue.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_CLUSTER);
				inqClusterQueue.addParameter(CMQCFC.MQIACF_CLUSTER_INFO, 1);
				inqClusterQueue.addParameter(CMQCFC.MQIACF_Q_ATTRS, new int[] { CMQCFC.MQIACF_ALL });
				
				pcfResp = mqAgent.submitPCF(inqClusterQueue);
				
				for (PCFMessage resp : pcfResp) {
					mqcqd = new MQClusterQueueData();
					mqcqd.setQmName(inventoryEntry.getQmName());
					mqcqd.setQueue(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_Q_NAME)));
					mqcqd.setCluster(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_CLUSTER_NAME)));
					mqcqd.setClusQmgr(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_CLUSTER_Q_MGR_NAME)));
					mqcqd.setClusQT(MQConstants.lookup(resp.getIntParameterValue(CMQC.MQIA_CLUSTER_Q_TYPE), "MQCQT_.*"));
					mqcqd.setClWlPrty(resp.getIntParameterValue(CMQC.MQIA_CLWL_Q_PRIORITY));
					mqcqd.setClWlRank(resp.getIntParameterValue(CMQC.MQIA_CLWL_Q_RANK));
					mqcqd.setDefBind(MQConstants.lookup(resp.getIntParameterValue(CMQC.MQIA_DEF_BIND), "MQBND_BIND_.*"));
					mqcqd.setDescr(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_Q_DESC)));
					mqcqd.setPut(MQConstants.lookup(resp.getIntParameterValue(CMQC.MQIA_INHIBIT_PUT), "MQQA_PUT_.*"));
					mqcqd.setQmId(StringUtils.rtrim(resp.getStringParameterValue(CMQC.MQCA_Q_MGR_IDENTIFIER)));
					
					this.qmClusterQueueData.add(mqcqd);
				}
			}
			
		} catch (MQException mqe) {
			System.err.println("MQException - " + mqe.getLocalizedMessage() + " while processing queue manager: " + inventoryEntry.getQmName());
			rc = false;
		} catch (IOException ioe) {
			System.out.println("IOException - " + ioe.getLocalizedMessage() + " while processing queue manager: " + inventoryEntry.getQmName());
			rc = false;
		} finally {
			try {
				// Disconnect from the queue manager
				if (mqAgent != null ) mqAgent.disconnect();
			} catch (MQException mqe) {
				// ignore
			}
		}
		
		log.trace("[{}] Exit {}.collect, rc={}", Thread.currentThread().getId(), this.getClass().getName(), rc);
		
		return rc;
	}

	public MQQueueManagerData getQmData() {
		return qmData;
	}

	public List<MQClusterQueueManagerData> getClusterQueueManagerData() {
		return qmClusterQueueManagerData;
	}
	
	public List<MQClusterQueueData> getClusterQueueData() {
		return qmClusterQueueData;
	}
	
}
