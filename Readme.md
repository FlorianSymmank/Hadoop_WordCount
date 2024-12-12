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

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.WordCountDriver en /data/en/en_10.txt /output/en_10_wordcount /data/stopwords.json

bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.SortByCountDriver /output/en_10_wordcount/part-r-00000 /output/de_100_sorted 

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