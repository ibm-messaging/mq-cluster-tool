package com.ibm.xmq.cluster;

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
 * MQClusterQueueManagerData Class
 *
 * A single cluster queue manager
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQClusterQueueManagerData {
	
	private final String CSV_SEPARATOR = ",";
	
	private String sourceQmName;
	private String tag;
	private String qmName;
	private String qmId;
	private String qmType;
	private String cluster;
	private String channelName;
	private String connectionName;
	private String definitionType;
	private String sslCAuth;
	private String sslCiph;
	private String sslPeer;
	private String status;
	private String xmitQ;
	private String version;
	
	
	/**
	 * Constructor
	 *
	 */
	public MQClusterQueueManagerData() {
		
		this.qmName = "";
	}

	public String getSourceQmName() {
		return sourceQmName;
	}

	public void setSourceQmName(String sourceQmName) {
		this.sourceQmName = sourceQmName;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getQmName() {
		return qmName;
	}

	public void setQmName(String qmName) {
		this.qmName = qmName;
	}

	public String getQmId() {
		return qmId;
	}

	public void setQmId(String qmId) {
		this.qmId = qmId;
	}

	public String getQmType() {
		return qmType;
	}

	public void setQmType(String qmType) {
		this.qmType = qmType;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getDefinitionType() {
		return definitionType;
	}

	public void setDefinitionType(String definitionType) {
		this.definitionType = definitionType;
	}
	
	public String getSslCAuth() {
		return sslCAuth;
	}

	public void setSslCAuth(String sslCAuth) {
		this.sslCAuth = sslCAuth;
	}

	public String getSslCiph() {
		return sslCiph;
	}

	public void setSslCiph(String sslCiph) {
		this.sslCiph = sslCiph;
	}

	public String getSslPeer() {
		return sslPeer;
	}

	public void setSslPeer(String sslPeer) {
		this.sslPeer = sslPeer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getXmitQ() {
		return xmitQ;
	}

	public void setXmitQ(String xmitQ) {
		this.xmitQ = xmitQ;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * Parse a cluster queue manager CSV record
	 *
	 * @param record A cluster queue manager CSV record
	 */
	public void fromCSV(String record) {
		
		// Parse file record
		String recordArr[] = record.split(CSV_SEPARATOR);
	
		setSourceQmName(recordArr[0]);
		setTag(recordArr[1]);
		setQmName(recordArr[2]);
		setQmId(recordArr[3]);
		setQmType(recordArr[4]); 
		setCluster(recordArr[5]);
		setChannelName(recordArr[6]);
		setConnectionName(recordArr[7]);
		setDefinitionType(recordArr[8]);
		setSslCAuth(recordArr[9]);
		setSslCiph(recordArr[10]);
		setSslPeer(recordArr[11]);
		setStatus(recordArr[12]);
		setXmitQ(recordArr[13]);
		setVersion(recordArr[14]);	
	}
	
	/**
	 * Format a cluster queue manager CSV record
	 *
	 * @return csvRecord A cluster queue manager formatted in CSV format
	 */
	public String toCSV() {
		return sourceQmName + CSV_SEPARATOR + tag + CSV_SEPARATOR + qmName + CSV_SEPARATOR + qmId + CSV_SEPARATOR + 
				qmType + CSV_SEPARATOR + cluster + CSV_SEPARATOR + channelName + CSV_SEPARATOR + connectionName + 
				CSV_SEPARATOR + definitionType + CSV_SEPARATOR + sslCAuth + CSV_SEPARATOR + sslCiph + CSV_SEPARATOR +
				sslPeer + CSV_SEPARATOR + status + CSV_SEPARATOR + xmitQ + CSV_SEPARATOR + version;
	}

	@Override
	public String toString() {
		return "MQClusterQueueManagerData [sourceQmName=" + sourceQmName + ", tag=" + tag + ", qmName=" + qmName
				+ ", qmId=" + qmId + ", qmType=" + qmType + ", cluster=" + cluster + ", channelName=" + channelName
				+ ", connectionName=" + connectionName + ", definitionType=" + definitionType + ", sslCAuth=" + sslCAuth
				+ ", sslCiph=" + sslCiph + ", sslPeer=" + sslPeer + ", status=" + status + ", xmitQ=" + xmitQ
				+ ", version=" + version + "]";
	}

}
