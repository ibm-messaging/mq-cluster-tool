package com.ibm.xmq.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

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
 * MQAgent Class
 *
 * Perform various MQ operations against a queue manager
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQAgent {
	
	private static final Logger log = LoggerFactory.getLogger(MQAgent.class);
	
	private final String CONN_NAMELIST_SEPARATOR = ",";
	
	private String qmName;
	private String hostname;
	private int port;
	private String connNameList;
	private String channelName;
	private String userId;
	private String password;
	private String sslCipherSuite;
	
	private MQQueueManager qmgr;
	private PCFMessageAgent pcfAgent;
	
	
	/**
	 * Constructor
	 *
	 */
	public MQAgent() {
		
		this.qmName = "";
	}
	
	/**
	 * Connect to a queue manager
	 * 
	 */
	public void connect() 
		throws MQException {
		
		log.trace("[{}] Entry {}.connect", Thread.currentThread().getId(), this.getClass().getName());
				
		List<String> listOfConn = new ArrayList<String>();
		Hashtable<String, Object> connProps = new Hashtable<String, Object>();
		
		if (this.connNameList == null) listOfConn.add(this.hostname + "(" + this.port + ")");
		else {
			String[] connListArr = this.connNameList.split(CONN_NAMELIST_SEPARATOR);
			for (String conn : connListArr) {
				listOfConn.add(conn);
			}
		}
		
		connProps.put(CMQC.CHANNEL_PROPERTY, this.channelName);
		
		if (this.userId != null) connProps.put(CMQC.USER_ID_PROPERTY, this.userId);
        if (this.password != null) {
        	connProps.put(CMQC.USE_MQCSP_AUTHENTICATION_PROPERTY, true);
        	connProps.put(CMQC.PASSWORD_PROPERTY, this.password);
        }
        
		if (this.sslCipherSuite != null) connProps.put(CMQC.SSL_CIPHER_SUITE_PROPERTY, this.sslCipherSuite);
		
		// Connect to the queue manager
		int attempt = 0;
		for (String conn : listOfConn) {
			
			int p1;
			String hostname = conn.substring(0, p1 = conn.indexOf('('));
			int port = Integer.parseInt(conn.substring(p1 + 1, conn.indexOf(')')));
		
			connProps.put(CMQC.HOST_NAME_PROPERTY, hostname);
			connProps.put(CMQC.PORT_PROPERTY, port);
			
			try {
				log.trace("[{}] MQQueueManager {}.connect, connProps={}", Thread.currentThread().getId(), this.getClass().getName(), connProps);
				this.qmgr = new MQQueueManager(this.qmName, connProps);
			} catch (MQException mqe) {
				attempt++;
				if (attempt == listOfConn.size()) throw mqe;
			}
		}
        
        // Create associated PCF agent
        this.pcfAgent = new PCFMessageAgent(this.qmgr);
        
        log.trace("[{}]  Exit {}.connect", Thread.currentThread().getId(), this.getClass().getName());	
	}
	
	/**
	 * Disconnect from a queue manager
	 * 
	 */
	public void disconnect()
		throws MQException {
		
		log.trace("[{}] Entry {}.disconnect", Thread.currentThread().getId(), this.getClass().getName());
		
		if (this.qmgr != null) qmgr.disconnect();
		
		log.trace("[{}]  Exit {}.disconnect", Thread.currentThread().getId(), this.getClass().getName());	
	}
	
	/**
	 * Submit a PCF command and wait for a response
	 * 
	 * @param pcfRequest A PCFMessage object containing the PCF request
	 * @return pcfResponse An array of PCFMessage objects containing the PCF response 
	 */
	public PCFMessage[] submitPCF(PCFMessage pcfRequest)
		throws MQException, IOException {
		
		PCFMessage[] pcfResponse;
		
		log.trace("[{}] Entry {}.submitPCF, pcfRequest={}", Thread.currentThread().getId(), this.getClass().getName(), pcfRequest);

	    if (this.pcfAgent == null) throw new IllegalStateException("No PCF Agent available!");
	    
	    pcfResponse = this.pcfAgent.send(pcfRequest);
	    
	    log.trace("[{}]  Exit {}.submitPCF, pcfResponse={}", Thread.currentThread().getId(), this.getClass().getName(), pcfResponse);
	    
	    return pcfResponse; 
	}

	public String getQmName() {
		return qmName;
	}

	public void setQmName(String qmName) {
		this.qmName = qmName;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getConnNameList() {
		return connNameList;
	}

	public void setConnNameList(String connNameList) {
		this.connNameList = connNameList;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSslCipherSuite() {
		return sslCipherSuite;
	}

	public void setSslCipherSuite(String sslCipherSuite) {
		this.sslCipherSuite = sslCipherSuite;
	}

	@Override
	public String toString() {
		return "MQAgent [qmName=" + qmName + ", hostname=" + hostname + ", port=" + port + ", connectionNameList="
				+ connNameList + ", channelName=" + channelName + ", userId=" + userId + ", password=" + password
				+ ", sslCipherSuite=" + sslCipherSuite + "]";
	}

}
