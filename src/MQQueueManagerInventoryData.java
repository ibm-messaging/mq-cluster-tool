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
 * MQQueueManagerInventoryData Class
 *
 * A single entry from the queue manager inventory
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQQueueManagerInventoryData {
	
	private String tag;
	private String qmName;
	private String connNameList;
	private String channelName;
	
	/**
	 * Constructor
	 *
	 */
	public MQQueueManagerInventoryData() {
		
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

	@Override
	public String toString() {
		return "MQQueueManagerInventoryData [tag=" + tag + ", qmName=" + qmName + ", connNameList=" + connNameList
				+ ", channelName=" + channelName + "]";
	}

}
