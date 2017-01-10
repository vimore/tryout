package com.securityx.modelfeature.config;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by harish on 2/2/15.
 */
public class SearchConfiguration {

    private SearchDefault searchDefault = new SearchDefault();
    private List<FieldInfo> webSearchSummary = Lists.newLinkedList();
    private List<FieldInfo> adSearchSummary = Lists.newLinkedList();
    private List<FieldInfo> taniumHostInfoMefSummary = Lists.newLinkedList();

    public SearchDefault getSearchDefault() {
        return searchDefault;
    }

    public void setSearchDefault(SearchDefault searchDefault) {
        this.searchDefault = searchDefault;
    }

    public List<FieldInfo> getWebSearchSummary() {
        return webSearchSummary;
    }

    public void setWebSearchSummary(List<FieldInfo> webSearchSummary) {
        this.webSearchSummary = webSearchSummary;
    }

    public List<FieldInfo> getAdSearchSummary() {
        return adSearchSummary;
    }

    public void setAdSearchSummary(List<FieldInfo> adSearchSummary) {
        this.adSearchSummary = adSearchSummary;
    }

    public List<FieldInfo> getTaniumHostInfoMefSummary() {
        return taniumHostInfoMefSummary;
    }

    public void setTaniumHostInfoMefSummary(List<FieldInfo> taniumHostInfoMefSummary) {
        this.taniumHostInfoMefSummary = taniumHostInfoMefSummary;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("searchDefault", searchDefault)
                .add("webSearchSummary", webSearchSummary)
                .add("adSearchSummary", adSearchSummary)
                .toString();
    }

    public static class SearchDefault {
        private int facetLimit;
        private int startRow;
        private int numRows;
        private String sortField;
        private String sortOrder;

        public int getFacetLimit() {
            return facetLimit;
        }

        public void setFacetLimit(int facetLimit) {
            this.facetLimit = facetLimit;
        }

        public int getStartRow() {
            return startRow;
        }

        public void setStartRow(int startRow) {
            this.startRow = startRow;
        }

        public int getNumRows() {
            return numRows;
        }

        public void setNumRows(int numRows) {
            this.numRows = numRows;
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

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("facetLimit", facetLimit)
                    .add("startRow", startRow)
                    .add("numRows", numRows)
                    .add("sortField", sortField)
                    .add("sortOrder", sortOrder)
                    .toString();
        }
    }


    public static class FieldInfo {
        private String field;
        private String name;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("field", field)
                    .add("name", name)
                    .toString();
        }
    }

}