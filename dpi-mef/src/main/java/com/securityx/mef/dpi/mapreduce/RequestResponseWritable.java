package com.securityx.mef.dpi.mapreduce;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A custom writable used as Map output key
 */
public class RequestResponseWritable implements WritableComparable<RequestResponseWritable>  {
    //CUSTOMER_ID,EVENT_TIME,REC_ID,SESSION_ID,REQ_RES_ID, REQ_RES_TYPE
    private String customerId;
    private String appProtocol ;
    private long timeStamp;
    private long sessionId ;
    private int reqResId ;

    public RequestResponseWritable() {

    }
    public RequestResponseWritable(String customerId, String appProtocol, long timeStamp,long sessionId, int reqResId) {
        this.customerId = customerId;
        this.appProtocol = appProtocol;
        this.timeStamp = timeStamp ;
        this.sessionId = sessionId;
        this.reqResId = reqResId;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //write and read order should be same
        Text.writeString(dataOutput, customerId);
        Text.writeString(dataOutput, appProtocol);
        dataOutput.writeLong(timeStamp);
        dataOutput.writeLong(sessionId);
        dataOutput.writeInt(reqResId);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        //write and read order should be same
        customerId = Text.readString(dataInput);
        appProtocol = Text.readString(dataInput);
        timeStamp = dataInput.readLong();
        sessionId = dataInput.readLong();
        reqResId = dataInput.readInt();
    }

    @Override
    public int compareTo(RequestResponseWritable o) {
        int cmp = this.appProtocol.compareTo(o.appProtocol) ;
        if (cmp != 0 ) {
            return  cmp;
        }

        if (timeStamp != o.timeStamp) {
            return (timeStamp < o.timeStamp? -1:1);
        }

        if (sessionId != o.sessionId) {
            return (sessionId < o.sessionId? -1:1);
        }

        if (reqResId != o.reqResId) {
            return (reqResId < o.reqResId? -1:1);
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestResponseWritable that = (RequestResponseWritable) o;

        if (reqResId != that.reqResId) return false;
        if (sessionId != that.sessionId) return false;
        if (timeStamp != that.timeStamp) return false;
        if (appProtocol != null ? !appProtocol.equals(that.appProtocol) : that.appProtocol != null) return false;
        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = customerId != null ? customerId.hashCode() : 0;
        result = 31 * result + (appProtocol != null ? appProtocol.hashCode() : 0);
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + (int) (sessionId ^ (sessionId >>> 32));
        result = 31 * result + reqResId;
        return result;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getReqResId() {
        return reqResId;
    }

    public String getAppProtocol() {
        return appProtocol;
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
