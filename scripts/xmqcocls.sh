#!/bin/sh

# Copyright 2018 IBM Corporation
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# --------------------------------------------------------------------------------------
#  Execute Xmqcocls - Collect IBM MQ cluster data to build a graph of clustered objects 
#
#  Requires:
#     - Gremlin Console (http://tinkerpop.apache.org/)
#     - IBM MQ Client (https://www-01.ibm.com/support/docview.wss?uid=swg24044791)
#     - Java 8
# --------------------------------------------------------------------------------------

# Path to IBM MQ Cluster Tool JARS
export MQTOOLS=/mqtools/*

# Root directory for Gremlin Console install
export GREMLIN=/apache-tinkerpop-gremlin-console-3.3.3

# Path to Gremlin core JARs
export LIBPATH=$GREMLIN/lib/*

# Path to TinkerGraph JARs
export EXTPATH=$GREMLIN/ext/*

# Path to additional JARs
export ADDL=$GREMLIN/ext/tinkergraph-gremlin/lib/*

# Classpath
export CP=$CLASSPATH:$MQTOOLS:$MQ_JAVA_LIB_PATH:$LIBPATH:$EXTPATH:$ADDL

# Run IBM MQ Cluster Tool
java -cp $CP com.ibm.xmq.cluster.Xmqcocls $*
