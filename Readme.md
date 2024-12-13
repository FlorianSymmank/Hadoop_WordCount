# Requirements
"JAVA_HOME": "C:\\Program Files\\Java\\jdk1.8.0_311"  
"M2_HOME": "C:\\Program Files\\Java\\apache-maven-3.9.9"  

# Setup
`docker pull zoltannz/hadoop-ubuntu:2.8.1`  

# Dashboard
`http://localhost:50070/dfshealth.html#tab-overview`

# Create Container
```
docker run -it -p 2122:2122 -p 8020:8020 -p 8030:8030 -p 8040:8040 -p 8042:8042 -p  8088:8088 -p 9000:9000 -p 10020:10020 -p 19888:19888 -p 49707:49707 -p 50010:50010 -p 50020:50020 -p 50070:50070 -p 50075:50075 -p 50090:50090 zoltannz/hadoop-ubuntu:2.8.1 /etc/bootstrap.sh -bash
```  

or 

```
docker ps
docker exec -it loving_nightingale /bin/bash
```

# Build
[F1] -> Tasks: Run Task -> Maven Build

# Run Job
``` bash
# Copy data to hadoop
docker cp data loving_nightingale:/usr/local/hadoop/fs/ # book-data, takes a while
docker cp stopwords/stopwords.json loving_nightingale:/usr/local/hadoop/fs/data/ # stopwords

# Copy jar
docker cp target/hadoop_wordcount-1.0-SNAPSHOT.jar loving_nightingale:/usr/local/hadoop/fs/hadoop_wordcount.jar

## Enter hadoop
docker exec -it -w /usr/local/hadoop loving_nightingale /bin/bash

# Upload input from hadoop to hdfs
# takes a while
bin/hdfs dfs -put fs/data/de /data
bin/hdfs dfs -put fs/data/en /data
bin/hdfs dfs -put fs/data/es /data
bin/hdfs dfs -put fs/data/fr /data
bin/hdfs dfs -put fs/data/it /data
bin/hdfs dfs -put fs/data/nl /data
bin/hdfs dfs -put fs/data/ru /data
bin/hdfs dfs -put fs/data/uk /data
bin/hdfs dfs -put fs/data/stopwords.json /data/stopwords.json

# Verify files are uploaded
bin/hdfs dfs -ls /data

# Reset outdir
bin/hdfs dfs -rm -r /output/*

# Run job
# bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver <lang> /data/<lang>/<lang>.txt /output/<lang>_wordcount /data/stopwords.json # first job: wordcount

# bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/<lang>_wordcount/part-r-00000 /output/<lang>_sorted # second job: sort by count

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_all.txt /output/de_all_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_all_wordcount/part-r-00000 /output/de_all_sorted

# Check the output
bin/hdfs dfs -cat /output/de_wordcount/*
bin/hdfs dfs -cat /output/de_sorted/*

# Download result from hdfs to hadoop
bin/hdfs dfs -get /output/* /tmp/output

# Reset outdir
bin/hdfs dfs -rm -r /output/*

## Exit hadoop
exit

# Copy from hadoop
docker cp loving_nightingale:/tmp/output .
```

``` bash
cd /usr/local/hadoop-2.8.1 # hadoop home
```

# Run Local
`java -cp hadoop_wordcount-1.0-SNAPSHOT.jar de.floriansymmank.WordCountDriver <> <> <>`

``` bash	
export HADOOP_HOME=/usr/local/hadoop-2.8.1
```

To Stop Hadoop Services:
``` bash
$HADOOP_HOME/sbin/stop-dfs.sh   # Stops HDFS
$HADOOP_HOME/sbin/stop-yarn.sh  # Stops YARN
``` 
To Start Hadoop Services:
``` bash
$HADOOP_HOME/sbin/start-dfs.sh  # Starts HDFS
$HADOOP_HOME/sbin/start-yarn.sh  # Starts YARN
``` 
Notes
- Make sure to run these commands as the user who installed and configured Hadoop.
- You can also check the status of the services using:
``` bash
ps -ef | grep hadoop | grep -P  'namenode|datanode|tasktracker|jobtracker'
$HADOOP_HOME/bin/hdfs dfsadmin -report
```
- After restarting, your configuration changes should take effect.

``` bash
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_1GB.txt /output/de_1GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_1GB_wordcount/part-r-00000 /output/de_1GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_2GB.txt /output/de_2GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_2GB_wordcount/part-r-00000 /output/de_2GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_3GB.txt /output/de_3GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_3GB_wordcount/part-r-00000 /output/de_3GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_4GB.txt /output/de_4GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_4GB_wordcount/part-r-00000 /output/de_4GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_5GB.txt /output/de_5GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_5GB_wordcount/part-r-00000 /output/de_5GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_6GB.txt /output/de_6GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_6GB_wordcount/part-r-00000 /output/de_6GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_7GB.txt /output/de_7GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_7GB_wordcount/part-r-00000 /output/de_7GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_8GB.txt /output/de_8GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_8GB_wordcount/part-r-00000 /output/de_8GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_9GB.txt /output/de_9GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_9GB_wordcount/part-r-00000 /output/de_9GB_sorted

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_10GB.txt /output/de_10GB_wordcount /data/stopwords.json
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_10GB_wordcount/part-r-00000 /output/de_10GB_sorted
```