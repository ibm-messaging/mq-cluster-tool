package com.ibm.xmq.cluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
 * MQQueueManagerInventory Class
 *
 * Manage the inventory of queue managers
 *
 * @author Oliver Fisse (IBM) - fisse@us.ibm.com
 * @version 1.0
 */
public class MQQueueManagerInventory {
	
	private static final Logger log = LoggerFactory.getLogger(MQQueueManagerInventory.class);

	private final String INVENTORY_SEPARATOR = ":";
	
	private BufferedReader inventory;
	private int totalInventoryEntries;
	private int currentInventoryEntry;
	private Hashtable<String, String> qmTagMap; 
	private String fileName;
	private List<MQQueueManagerInventoryData> qmi;
	
	
	/**
	 * Constructor
	 *
	 */
	public MQQueueManagerInventory(String fileName, List<String> tagList) 
		throws FileNotFoundException, IOException {
		
		log.trace("[{}] Entry {}.constructor, fileName={}, tageList={}", Thread.currentThread().getId(), this.getClass().getName(), fileName, tagList);
		
		String entry;
		
		this.qmi = new ArrayList<MQQueueManagerInventoryData>();
		this.qmTagMap = new Hashtable<String, String>();
		
		this.fileName = fileName;
		this.inventory = new BufferedReader(new FileReader(fileName));
		
		// Load inventory
		while ((entry = this.inventory.readLine()) != null) {
			// Parse file entry
			String entriesArr[] = entry.split(INVENTORY_SEPARATOR);
			
			MQQueueManagerInventoryData qmid = new MQQueueManagerInventoryData();
			qmid.setTag(entriesArr[0]);
			if (tagList.size() != 0) {
				boolean tagMatched = false;
				for (String tag : tagList) {
					if (StringUtils.matchWildcard(qmid.getTag(), tag)) {
						tagMatched = true;
						break;
					}
				}
				
				if (!tagMatched) continue;
			}
			qmid.setQmName(entriesArr[1]);
			qmid.setConnNameList(entriesArr[2]);
			qmid.setChannelName(entriesArr[3]);
			
			this.qmTagMap.put(entriesArr[1], entriesArr[0]);
			
			this.qmi.add(qmid);
			this.totalInventoryEntries++;
		}
		
		this.inventory.close();
		
		log.trace("[{}] Exit {}.constructor, inventory={}", Thread.currentThread().getId(), this.getClass().getName(), this.qmi);
	}
	
	public MQQueueManagerInventoryData next()
		throws IOException {
		
		log.trace("[{}] Entry {}.next", Thread.currentThread().getId(), this.getClass().getName());
		
		MQQueueManagerInventoryData qmid;
		
		if (this.currentInventoryEntry < this.totalInventoryEntries) {
			qmid = this.qmi.get(this.currentInventoryEntry);
			this.currentInventoryEntry++;
		} else qmid = null;
		
		log.trace("[{}] Exity {}.next, entry={}", Thread.currentThread().getId(), this.getClass().getName(), qmid);
		
		return qmid;
	}

	public String getFileName() {
		return fileName;
	}
	
	public int getCurrentInventoryEntry() {
		return this.currentInventoryEntry;
	}

	public int getTotalInventoryEntries() {
		return totalInventoryEntries;
	}
	
	public String getTag(String qmName) {
		String tag = this.qmTagMap.get(qmName);
		
		if (tag == null) return "unknown";
		else return tag;
	}
	
}
