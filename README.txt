1) First, your hadoop needs to work. 
% echo $HADOOP_HOME prints out the path of your Hadoop directory
This means you should be able to get through all 10 steps in the hw pdf.
I’ve created some handy scripts to start and stop all the services called start.sh and stop.sh
% sh start.sh (will start the required services) and similar for stopping.

2) Second, your Java path should work as well. I compiled this using Java 8, but I’m sure if you compile the code with Java 6 or 7 it will work. You can set the java path the same way you do for hadoop in your .bashrc
% echo $JAVA_HOME (prints the path to your idk i.e. /Library/Java/JavaVirtualMachines/jdk1.8.0_40.jdk/Contents/Home)

3) I have included the source code. You should recompile the source files, wrap them in a jar, and delete the remote output folder everytime you make changes to your source code. To do this I wrapped all those commands in a shell file(redo.sh), and if your file system looks like mine (see view.jpg), then this recompile and run.

**Note, if you start the services and then stop them, you MAY have to delete the contents of hdfs/namenode/ and hdfs/datanode/ before starting the services again. One way to avoid this is just never stop the services, try to run the example in the pdf and if that works, compile with our source code. 
**If you see a blank when you run "jps", it is probably eclipse. Dont worry about it.

I was getting this error when I quit, if you get this on the first run with our source code, you’re caught up:
15/04/09 17:58:44 INFO mapreduce.Job: Task Id : attempt_1428607256561_0009_m_000009_1, Status : FAILED
Error: java.io.IOException: Type mismatch in key from map: expected org.apache.hadoop.io.Text, received WordChapter
	at org.apache.hadoop.mapred.MapTask$MapOutputBuffer.collect(MapTask.java:1069)
	at org.apache.hadoop.mapred.MapTask$NewOutputCollector.write(MapTask.java:712)
	at org.apache.hadoop.mapreduce.task.TaskInputOutputContextImpl.write(TaskInputOutputContextImpl.java:89)
	at org.apache.hadoop.mapreduce.lib.map.WrappedMapper$Context.write(WrappedMapper.java:112)
	at CountingIndexer$TokenizerMapper.map(CountingIndexer.java:30)
	at CountingIndexer$TokenizerMapper.map(CountingIndexer.java:18)
	at org.apache.hadoop.mapreduce.Mapper.run(Mapper.java:145)
	at org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:784)
	at org.apache.hadoop.mapred.MapTask.run(MapTask.java:341)
	at org.apache.hadoop.mapred.YarnChild$2.run(YarnChild.java:163)
	at java.security.AccessController.doPrivileged(Native Method)
	at javax.security.auth.Subject.doAs(Subject.java:422)
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1628)
	at org.apache.hadoop.mapred.YarnChild.main(YarnChild.java:159)
