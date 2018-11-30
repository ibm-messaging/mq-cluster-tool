@echo off

REM Copyright 2017, 2018 IBM Corporation
REM 
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM    http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

REM ---------------------------------------------------------------------------------
REM  Execute Xmqgrcls - Create a graph of IBM MQ clustered objects from CSV files
REM
REM  Requires:
REM     - Gremlin Console (http://tinkerpop.apache.org/)
REM     - IBM MQ Client (https://www-01.ibm.com/support/docview.wss?uid=swg24044791)
REM     - Java 8
REM ---------------------------------------------------------------------------------

REM Path to IBM MQ Cluster Tool JARS
set MQTOOLS=C:\MQ\*

REM Root directory for Gremlin Console install
set GREMLIN=C:\apache-tinkerpop-gremlin-console-3.3.3

REM Path to Gremlin core JARs
set LIBPATH=%GREMLIN%\lib\*

REM Path to TinkerGraph JARs
set EXTPATH=%GREMLIN%\ext\*

REM Path to additional JARs
set ADDL=%GREMLIN%\ext\tinkergraph-gremlin\lib\*

REM Classpath
set CP=%CLASSPATH%;%MQTOOLS%;%MQ_JAVA_LIB_PATH%;%LIBPATH%;%EXTPATH%;%ADDL%

REM Run IBM MQ Cluster Tool
java -cp %CP% com.ibm.xmq.cluster.Xmqgrcls %*