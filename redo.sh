javac -classpath `hadoop classpath` -d build Hadoop/src/CountingIndexer.java Hadoop/src/WordChapter.java Hadoop/src/ChapterCount.java Hadoop/src/ChapterArray.java
jar -cvf CountingIndexer.java -C build/ .
hdfs dfs -rm -r /output
hadoop jar CountingIndexer.jar CountingIndexer /input /output
