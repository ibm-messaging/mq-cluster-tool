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
 * MQClusterQueueData Class
 *
 * A single cluster queue for a queue manager
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQClusterQueueData {
	
private final String CSV_SEPARATOR = ",";
	
	private String qmName;
	private String queue;
	private String cluster;
	private String clusQmgr;
	private String clusQT;
	private int clWlPrty;
	private int clWlRank;
	private String defBind;
	private String descr;
	private String put;
	private String qmId;
	
	public MQClusterQueueData() {
		
	}

	public String getQmName() {
		return qmName;
	}

	public void setQmName(String qmName) {
		this.qmName = qmName;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getClusQmgr() {
		return clusQmgr;
	}

	public void setClusQmgr(String clusQmgr) {
		this.clusQmgr = clusQmgr;
	}
	
	public String getClusQT() {
		return clusQT;
	}

	public void setClusQT(String clusQT) {
		this.clusQT = clusQT;
	}

	public int getClWlPrty() {
		return clWlPrty;
	}

	public void setClWlPrty(int clWlPrty) {
		this.clWlPrty = clWlPrty;
	}

	public int getClWlRank() {
		return clWlRank;
	}

	public void setClWlRank(int clWlRank) {
		this.clWlRank = clWlRank;
	}

	public String getDefBind() {
		return defBind;
	}

	public void setDefBind(String defBind) {
		this.defBind = defBind;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getPut() {
		return put;
	}

	public void setPut(String put) {
		this.put = put;
	}

	public String getQmId() {
		return qmId;
	}

	public void setQmId(String qmId) {
		this.qmId = qmId;
	}
	
	/**
	 * Parse a cluster queue CSV record
	 *
	 * @param record A cluster queue CSV record
	 */
	public void fromCSV(String record) {
		
		// Parse file record
		String recordArr[] = record.split(CSV_SEPARATOR);
					
		setQmName(recordArr[0]);
		setQueue(recordArr[1]);
		setCluster(recordArr[2]);
		setClusQmgr(recordArr[3]);
		setClusQT(recordArr[4]);
		setClWlPrty(Integer.parseInt(recordArr[5]));
		setClWlRank(Integer.parseInt(recordArr[6]));
		setDefBind(recordArr[7]);
		setDescr(recordArr[8]);
		setPut(recordArr[9]);
		setQmId(recordArr[10]);
	}
	
	/**
	 * Format a cluster queue CSV record
	 *
	 * @return csvRecord A cluster queue formatted in CSV format
	 */
	public String toCSV() {
		return qmName + CSV_SEPARATOR + queue + CSV_SEPARATOR + cluster + CSV_SEPARATOR + clusQmgr + CSV_SEPARATOR + clusQT + CSV_SEPARATOR + 
				clWlPrty + CSV_SEPARATOR + clWlRank + CSV_SEPARATOR + defBind + CSV_SEPARATOR + descr + CSV_SEPARATOR + 
				put + CSV_SEPARATOR + qmId;
	}

	@Override
	public String toString() {
		return "MQClusterQueueData [qmName=" + qmName + ", queue=" + queue + ", cluster=" + cluster + ", clusQmgr="
				+ clusQmgr + ", clusrQT=" + clusQT + ", clWlPrty=" + clWlPrty + ", clWlRank=" + clWlRank + ", defBind="
				+ defBind + ", descr=" + descr + ", put=" + put + ", qmId=" + qmId + "]";
	}
	
}
