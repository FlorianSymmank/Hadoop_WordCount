# Requirements
"JAVA_HOME": "C:\\Program Files\\Java\\jdk1.8.0_311"  
"M2_HOME": "C:\\Program Files\\Java\\apache-maven-3.9.9"  

# Setup
`docker pull zoltannz/hadoop-ubuntu:2.8.1`  

# Enter
```
docker run -it -p 2122:2122 -p 8020:8020 -p 8030:8030 -p 8040:8040 -p 8042:8042 -p  8088:8088 -p 9000:9000 -p 10020:10020 -p 19888:19888 -p 49707:49707 -p 50010:50010 -p 50020:50020 -p 50070:50070 -p 50075:50075 -p 50090:50090 zoltannz/hadoop-ubuntu:2.8.1 /etc/bootstrap.sh -bash
```  

or  

```
docker ps
docker exec -it <container_name> /bin/bash
```

# Build
[F1] -> Tasks: Run Task -> Maven Build

# Run Job
```
# Copy data to hadoop
docker cp data <container_name>:/usr/local/hadoop/fs/data
docker cp stopwords/stopwords-combined.json <container_name>:/usr/local/hadoop/fs/data

# Copy jar
docker cp target/hadoop_wordcount-1.0-SNAPSHOT.jar <container_name>:/usr/local/hadoop/fs/hadoop_wordcount.jar

## Enter hadoop
docker exec -it -w /usr/local/hadoop <container_name> /bin/bash

# Upload input from hadoop to hdfs
bin/hdfs dfs -put fs/data /

# Verify files are uploaded
bin/hdfs dfs -ls /

# Reset outdir
bin/hdfs dfs -rm -r <output_dir>

# Run job
bin/hadoop jar fs/hadoop_wordcount.jar de.floriansymmank.Main /<language>/<book>.txt <output_dir>

# Check the output
bin/hdfs dfs -cat output/*

# Download result from hdfs to hadoop
bin/hdfs dfs -get output /tmp/output

# Reset outdir
bin/hdfs dfs -rm -r <output_dir>

## Exit hadoop
exit

# Copy from hadoop
docker cp <container_name>:/tmp/output output
```

# Run Local
`java -cp hadoop_wordcount-1.0-SNAPSHOT.jar de.floriansymmank.Main`