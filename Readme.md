# Overview
## Data
Dataset is located in `data` folder and contains text files in different languages.  

## Stopwords
Stopwords are located in `stopwords` folder and contains json files with stopwords for each language.

## WordCount
WordCount is a simple Hadoop job that counts the number of words in a text file.
- [WordCountDriver](src/main/java/de/floriansymmank/WordCountDriver.java)  
- [WordCountMapper](src/main/java/de/floriansymmank/WordCountMapper.java)
- [WordCountReducer](src/main/java/de/floriansymmank/WordCountReducer.java)

## SortByCount
SortByCount is a simple Hadoop job that sorts the output of WordCount by count.
- [SortByCountDriver](src/main/java/de/floriansymmank/SortByCountDriver.java)
- [SortByCountMapper](src/main/java/de/floriansymmank/SortByCountMapper.java)
- [SortByCountReducer](src/main/java/de/floriansymmank/SortByCountReducer.java)

# How to run

## Requirements
"JAVA_HOME": "C:\\Program Files\\Java\\jdk1.8.0_311"  
"M2_HOME": "C:\\Program Files\\Java\\apache-maven-3.9.9"  

## Setup
`docker pull zoltannz/hadoop-ubuntu:2.8.1`  

## Dashboard
`http://localhost:50070/dfshealth.html#tab-overview`

## Create Container
```
docker run -it -p 2122:2122 -p 8020:8020 -p 8030:8030 -p 8040:8040 -p 8042:8042 -p  8088:8088 -p 9000:9000 -p 10020:10020 -p 19888:19888 -p 49707:49707 -p 50010:50010 -p 50020:50020 -p 50070:50070 -p 50075:50075 -p 50090:50090 zoltannz/hadoop-ubuntu:2.8.1 /etc/bootstrap.sh -bash
```  

or 

```
docker ps
docker exec -it loving_nightingale /bin/bash
```

## Build
[F1] -> Tasks: Run Task -> Maven Build

## Run Job
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
# bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver <lang> /data/<lang>/<text_file>.txt /output/<lang>_wordcount /data/stopwords.json /output/stats.txt # first job: wordcount

# bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/<lang>_wordcount/part-r-00000 /output/<lang>_sorted # second job: sort by count

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver de /data/de/de_all.txt /output/de_all_wordcount /data/stopwords.json /output/stats.txt
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/de_all_wordcount/part-r-00000 /output/de_all_sorted

# Check the output
bin/hdfs dfs -cat /output/de_all_wordcount/*
bin/hdfs dfs -cat /output/de_all_sorted/*

# Download result from hdfs to hadoop
bin/hdfs dfs -get /output/* /tmp/output

# Reset outdir
bin/hdfs dfs -rm -r /output/*

## Exit hadoop
exit

# Copy from hadoop
docker cp loving_nightingale:/tmp/output .
```
## Run Local
`java -cp hadoop_wordcount-1.0-SNAPSHOT.jar de.floriansymmank.WordCountDriver <> <> <>`

## Return to home
``` bash
cd /usr/local/hadoop-2.8.1 # hadoop home
```

## Hadoop Commands
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

Leave Safe Mode
``` bash
bin/hdfs dfsadmin -safemode leave
```