package com.securityx.mef.dpi.mapreduce;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class SecurityPacketFilesInfo {

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        System.out.println("oozie prop file " +  System.getProperty("oozie.action.output.properties"));
        String outputProp = System.getProperty("oozie.action.output.properties");
        if (outputProp == null)
            outputProp = "/tmp/oozie.properties";
        File file = new File(outputProp);
        Properties props = new Properties();

        FileSystem dfs = FileSystem.get(conf);

        FileStatus[] statuses = dfs.globStatus(new Path("/securityx/flume/livestream/2013/10/31/*/*packet*"))  ;

        StringBuilder matchedFilesStr = new StringBuilder();

        if (statuses != null) {
            for(int i = 0 ; i < statuses.length ; i ++) {
                System.out.println(" name is " + statuses[i].getPath().toString())  ;
                if (matchedFilesStr.length() > 0 )
                    matchedFilesStr.append(",") ;
                matchedFilesStr.append(statuses[i].getPath().toString()) ;
            }

            props.setProperty("INPUTFILES", matchedFilesStr.toString());
            if (statuses.length >0)
                props.setProperty("HASFILES", "YES");
            else
                props.setProperty("HASFILES", "NO");


            OutputStream os = new FileOutputStream(file);
            props.store(os, "");
            os.close();
            System.out.println(file.getAbsolutePath());
        }
        else {
            System.out.println(" statuses is null ");
        }
    }
}
