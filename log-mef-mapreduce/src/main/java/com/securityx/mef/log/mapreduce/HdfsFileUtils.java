package com.securityx.mef.log.mapreduce;


import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


public class HdfsFileUtils {
    /**
     * Generates a a working dir based on user's homedirectory and jobId
     *
     * @param fs
     * @return
     */
    public static String genJobTmpDir(FileSystem fs) {
        String uuid = UUID.randomUUID().toString();
        return fs.getHomeDirectory().toString() + Path.SEPARATOR + "_tmp" + Path.SEPARATOR + uuid;
    }

    /**
     * move files
     *
     * @param jobId
     * @param fs
     * @param srcDir
     * @param destDir
     * @return
     * @throws IOException
     */

    public static List<Path> moveToPersistentDirWithoutOverlapping(String jobId, FileSystem fs,  Path srcDir, Path destDir, Pattern filePattern) throws IOException {
        boolean res = true;
        FileStatus[] status;
        String filename;
        Path file;
        Path dest;
        FileContext myFContext = null;

        myFContext = FileContext.getFileContext(fs.getConf());

        List<Path> out = new ArrayList<Path>();
        Path fqDestDir = myFContext.makeQualified(destDir);
        assert (fs.isDirectory(srcDir));
        if (!fs.exists(fqDestDir)) {
            fs.mkdirs(fqDestDir);
        }

        status = fs.listStatus(new Path(srcDir.toString()));

        for (int i = 0; i < status.length; i++) {
            file = status[i].getPath();
            if (accept(file, filePattern)) {
                filename = jobId + "-" +file.getName().replaceAll("\\.avro",   "_" + String.valueOf(System.currentTimeMillis()) + ".avro");
                dest = new Path(fqDestDir.toString() + Path.SEPARATOR + filename);
                res = res && fs.rename(file, dest);
                if (res) {
                    out.add(dest);
                }
                if (!res) {
                    System.err.println("failed to move " + file.toString() + " to " + dest.toString());
                }
            }
        }

        return out;
    }

    public static List<Path> renameToPersistentDirWithoutOverlapping(FileSystem fs , Path srcDir, Path destDir, Pattern filePattern) throws IOException {
        boolean res = true;
        FileStatus[] status;
        String filename;
        Path file;
        Path dest;
        FileContext myFContext = null;
        myFContext = FileContext.getFileContext(fs.getConf());

        List<Path> out = new ArrayList<Path>();
        Path fqDestDir = myFContext.makeQualified(destDir);
        assert (fs.isDirectory(srcDir));
        if (!fs.exists(fqDestDir)) {
            fs.mkdirs(fqDestDir);
        }

        status = fs.globStatus(new Path(srcDir.toString() + "/" + srcDir.toString() + "/*/*/*/*/*"));
        //System.err.println("nb of files found:"+String.valueOf(status.length)) ;
        for (int i = 0; i < status.length; i++) {
            file = status[i].getPath();
            //System.err.println("processing: "+file.toString()) ;
            if (accept(file, filePattern)) {
                filename = file.getName().replaceAll("part-r-\\d+\\.avro", String.valueOf(System.currentTimeMillis()));
                dest = new Path(file.getParent() + Path.SEPARATOR + filename);
                res = res && fs.rename(file, dest);
                if (res) {
                    out.add(dest);
                }
                if (!res) {
                    System.err.println("failed to move " + file.toString() + " to " + dest.toString());
                }
            }
        }

        return out;
    }

    private static boolean accept(Path path, Pattern pattern) {
        return pattern.matcher(path.getName()).matches();
    }


}
