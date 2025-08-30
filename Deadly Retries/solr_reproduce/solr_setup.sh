#!/bin/bash

# 构建instrumentation工具
cd /Users/lizhenyu/Desktop/baseline/vcstudy/Deadly\ Retries/hbase-instrumentation-master/instrument-libs
mvn clean verify
cd ..
./build-solr.sh

# 复制instrumentation JAR到Solr
cp solr-instrumentation-1.0-SNAPSHOT.jar /Users/lizhenyu/Desktop/AutoPilotEval/solr-8.11.4/solr/server/lib/ext/
cp instrument-libs/target/instrument-libs-1.0-SNAPSHOT.jar /Users/lizhenyu/Desktop/AutoPilotEval/solr-8.11.4/solr/server/lib/ext/

# 复制AspectJ weaver
cp /Users/lizhenyu/IdeaProjects/vcstudy/deadlyretries/hbase-instrumentation-master/aspectjweaver-1.9.7.jar /Users/lizhenyu/Desktop/AutoPilotEval/solr-8.11.4/solr/server/lib/ext/

### Add solr_opt in solr.in.sh for server endpoint
###SOLR_OPTS="$SOLR_OPTS -javaagent:/opt/Solr/solr/server/lib/aspectjweaver.jar"

# 设置监控服务器地址（假设监控服务器运行在172.17.0.1:44444）
###SOLR_OPTS="$SOLR_OPTS -Dpfl.monitor.endpoint=10.10.1.3"