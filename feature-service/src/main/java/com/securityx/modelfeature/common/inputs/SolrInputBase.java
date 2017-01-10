package com.securityx.modelfeature.common.inputs;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by harish on 1/30/15.
 */
public class SolrInputBase {

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;

    private Map<String, List<String>> queryParams;

    //this will be to limit the facets we show (applicable for search summary also)
    private int facetLimit = 5;

    //this is to limit the number of rows in the search query (view logs)
    private int numRows = 100;

    private int pageNo = 1;

    private String sortField;
    private String sortOrder;

    private boolean summarize = true;

    public int getFacetLimit() {
        return facetLimit;
    }

    public void setFacetLimit(int facetLimit) {
        this.facetLimit = facetLimit;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

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

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isSummarize() {
        return summarize;
    }

    public void setSummarize(boolean summarize) {
        this.summarize = summarize;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }
}
