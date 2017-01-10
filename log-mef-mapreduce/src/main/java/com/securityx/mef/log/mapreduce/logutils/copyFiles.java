package com.securityx.mef.log.mapreduce.logutils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;


// vv FileCopyWithProgress
public class copyFiles {
  public static void main(String[] args) throws Exception {

    Configuration conf = new Configuration();
    conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
    conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
    FileSystem fs = FileSystem.get(conf);
    for(String arg : args ) {
        try {
            System.out.println("Argument:<" + arg + ">");
            String dayPath = arg.substring(0, arg.length() - 3);
            fs.mkdirs(new Path(dayPath));
            fs.copyFromLocalFile(new Path(arg), new Path(dayPath));
        } catch (Exception e) {
            System.out.println("Continue w/ next copy path: <"+e.getMessage()+">");
        }
    }
  }
}
