1. 首先跑solr_setup.sh，构建.jar，copy. （1） instrument-libs-1.0-SNAPSHOT.jar" （2） xxx-instrumentation-1.0-SNAPSHOT.jar
2. 把.jar需要的和aspectj上传到server上去。
3. 在solr.in.sh要加上

如下参数
SOLR_OPTS="$SOLR_OPTS -javaagent:/opt/Solr/solr/server/lib/ext/aspectjweaver.jar"

# 设置监控服务器地址（假设监控服务器运行在172.17.0.1:44444）
SOLR_OPTS="$SOLR_OPTS -Dpfl.monitor.endpoint=10.10.1.3"

CASSANDRA是：
JVM_OPTS="$JVM_OPTS -javaagent:$CASSANDRA_HOME/lib/aspectjweaver.jar"
JVM_OPTS="$JVM_OPTS -Dpfl.monitor.endpoint=$PFL_MONITOR_ENDPOINT"
CLASSPATH="$CLASSPATH:$CASSANDRA_HOME/lib/pfl-cassandra-instrument.jar"

对于hbase:
请看：/Users/lizhenyu/Desktop/Evaluation/hbase-14598-0.98下

hdfs 和 yarn的instrumentation完全一样
