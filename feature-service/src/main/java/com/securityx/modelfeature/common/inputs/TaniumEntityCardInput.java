package com.securityx.modelfeature.common.inputs;

import com.google.common.collect.Lists;

import java.util.List;

public class TaniumEntityCardInput {

    String startTime;
    String endTime;
    List<String> processList = Lists.newArrayList();
    List<String> md5List = Lists.newArrayList();

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getProcessList() {
        return processList;
    }

    public void setProcessList(List<String> processList) {
        this.processList = processList;
    }

    public List<String> getMd5List() {
        return md5List;
    }

    public void setMd5List(List<String> md5List) {
        this.md5List = md5List;
    }
}
