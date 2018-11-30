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
 * MQQueueManagerData Class
 *
 * A single queue manager
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQQueueManagerData {
	
	private final String CSV_SEPARATOR = ",";
	
	private String tag;
	private String qmName;
	private String qmId;
	private String description;
	private int commandLevel;
	private String platform;
	private String version;
	
	/**
	 * Constructor
	 *
	 */
	public MQQueueManagerData() {
		
		this.qmName = "";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getCommandLevel() {
		return commandLevel;
	}

	public void setCommandLevel(int commandLevel) {
		this.commandLevel = commandLevel;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * Parse a queue manager CSV record
	 *
	 * @param record A queue manager CSV record
	 */
	public void fromCSV(String record) {
		
		// Parse file record
		String recordArr[] = record.split(CSV_SEPARATOR);
					
		setTag(recordArr[0]);
		setQmName(recordArr[1]);
		setQmId(recordArr[2]);
		setDescription(recordArr[3]);
		setCommandLevel(Integer.parseInt(recordArr[4]));
		setPlatform(recordArr[5]);
		setVersion(recordArr[6]);
	}
	
	/**
	 * Format a queue manager CSV record
	 *
	 * @return csvRecord A queue manager formatted in CSV format
	 */
	public String toCSV() {
		return tag + CSV_SEPARATOR + qmName + CSV_SEPARATOR + qmId + CSV_SEPARATOR + description + 
				CSV_SEPARATOR + commandLevel + CSV_SEPARATOR + platform + CSV_SEPARATOR + version;
	}

	@Override
	public String toString() {
		return "MQQueueManagerData [tag=" + tag + ", qmName=" + qmName + ", qmId=" + qmId + ", description="
				+ description + ", commandLevel=" + commandLevel + ", platform=" + platform + ", version=" + version
				+ "]";
	}

}
