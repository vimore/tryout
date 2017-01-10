
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for options complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="options">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="flags" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hide_errors_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="include_answer_times_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="row_counts_only_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="aggregate_over_time_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="most_recent_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="include_hashes_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hide_no_results_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="use_user_context_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="return_lists_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="return_cdata_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="pct_done_limit" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="context_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sample_frequency" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sample_start" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sample_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="suppress_scripts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="row_start" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="row_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sort_order" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cache_filters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="filter" type="{urn:TaniumSOAP}cache_filter" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="filter_string" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="filter_not_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="recent_result_buckets" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cache_id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="cache_expiration" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="cache_sort_fields" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="include_user_details" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="include_hidden_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "options", propOrder = {

})
public class Options {

    protected Integer flags;
    @XmlElement(name = "hide_errors_flag")
    protected Integer hideErrorsFlag;
    @XmlElement(name = "include_answer_times_flag")
    protected Integer includeAnswerTimesFlag;
    @XmlElement(name = "row_counts_only_flag")
    protected Integer rowCountsOnlyFlag;
    @XmlElement(name = "aggregate_over_time_flag")
    protected Integer aggregateOverTimeFlag;
    @XmlElement(name = "most_recent_flag")
    protected Integer mostRecentFlag;
    @XmlElement(name = "include_hashes_flag")
    protected Integer includeHashesFlag;
    @XmlElement(name = "hide_no_results_flag")
    protected Integer hideNoResultsFlag;
    @XmlElement(name = "use_user_context_flag")
    protected Integer useUserContextFlag;
    @XmlElement(name = "return_lists_flag")
    protected Integer returnListsFlag;
    @XmlElement(name = "return_cdata_flag")
    protected Integer returnCdataFlag;
    @XmlElement(name = "pct_done_limit")
    protected Integer pctDoneLimit;
    @XmlElement(name = "context_id")
    protected Integer contextId;
    @XmlElement(name = "sample_frequency")
    protected Integer sampleFrequency;
    @XmlElement(name = "sample_start")
    protected Integer sampleStart;
    @XmlElement(name = "sample_count")
    protected Integer sampleCount;
    @XmlElement(name = "suppress_scripts")
    protected Integer suppressScripts;
    @XmlElement(name = "row_start")
    protected Integer rowStart;
    @XmlElement(name = "row_count")
    protected Integer rowCount;
    @XmlElement(name = "sort_order")
    protected String sortOrder;
    @XmlElement(name = "cache_filters")
    protected Options.CacheFilters cacheFilters;
    @XmlElement(name = "filter_string")
    protected String filterString;
    @XmlElement(name = "filter_not_flag")
    protected Integer filterNotFlag;
    @XmlElement(name = "recent_result_buckets")
    protected String recentResultBuckets;
    @XmlElement(name = "cache_id")
    protected Long cacheId;
    @XmlElement(name = "cache_expiration")
    protected Integer cacheExpiration;
    @XmlElement(name = "cache_sort_fields")
    protected String cacheSortFields;
    @XmlElement(name = "include_user_details")
    protected Integer includeUserDetails;
    @XmlElement(name = "include_hidden_flag")
    protected Integer includeHiddenFlag;

    /**
     * Gets the value of the flags property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFlags() {
        return flags;
    }

    /**
     * Sets the value of the flags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFlags(Integer value) {
        this.flags = value;
    }

    /**
     * Gets the value of the hideErrorsFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHideErrorsFlag() {
        return hideErrorsFlag;
    }

    /**
     * Sets the value of the hideErrorsFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHideErrorsFlag(Integer value) {
        this.hideErrorsFlag = value;
    }

    /**
     * Gets the value of the includeAnswerTimesFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIncludeAnswerTimesFlag() {
        return includeAnswerTimesFlag;
    }

    /**
     * Sets the value of the includeAnswerTimesFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIncludeAnswerTimesFlag(Integer value) {
        this.includeAnswerTimesFlag = value;
    }

    /**
     * Gets the value of the rowCountsOnlyFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowCountsOnlyFlag() {
        return rowCountsOnlyFlag;
    }

    /**
     * Sets the value of the rowCountsOnlyFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowCountsOnlyFlag(Integer value) {
        this.rowCountsOnlyFlag = value;
    }

    /**
     * Gets the value of the aggregateOverTimeFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAggregateOverTimeFlag() {
        return aggregateOverTimeFlag;
    }

    /**
     * Sets the value of the aggregateOverTimeFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAggregateOverTimeFlag(Integer value) {
        this.aggregateOverTimeFlag = value;
    }

    /**
     * Gets the value of the mostRecentFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMostRecentFlag() {
        return mostRecentFlag;
    }

    /**
     * Sets the value of the mostRecentFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMostRecentFlag(Integer value) {
        this.mostRecentFlag = value;
    }

    /**
     * Gets the value of the includeHashesFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIncludeHashesFlag() {
        return includeHashesFlag;
    }

    /**
     * Sets the value of the includeHashesFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIncludeHashesFlag(Integer value) {
        this.includeHashesFlag = value;
    }

    /**
     * Gets the value of the hideNoResultsFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHideNoResultsFlag() {
        return hideNoResultsFlag;
    }

    /**
     * Sets the value of the hideNoResultsFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHideNoResultsFlag(Integer value) {
        this.hideNoResultsFlag = value;
    }

    /**
     * Gets the value of the useUserContextFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUseUserContextFlag() {
        return useUserContextFlag;
    }

    /**
     * Sets the value of the useUserContextFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUseUserContextFlag(Integer value) {
        this.useUserContextFlag = value;
    }

    /**
     * Gets the value of the returnListsFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReturnListsFlag() {
        return returnListsFlag;
    }

    /**
     * Sets the value of the returnListsFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReturnListsFlag(Integer value) {
        this.returnListsFlag = value;
    }

    /**
     * Gets the value of the returnCdataFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReturnCdataFlag() {
        return returnCdataFlag;
    }

    /**
     * Sets the value of the returnCdataFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReturnCdataFlag(Integer value) {
        this.returnCdataFlag = value;
    }

    /**
     * Gets the value of the pctDoneLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPctDoneLimit() {
        return pctDoneLimit;
    }

    /**
     * Sets the value of the pctDoneLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPctDoneLimit(Integer value) {
        this.pctDoneLimit = value;
    }

    /**
     * Gets the value of the contextId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getContextId() {
        return contextId;
    }

    /**
     * Sets the value of the contextId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setContextId(Integer value) {
        this.contextId = value;
    }

    /**
     * Gets the value of the sampleFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSampleFrequency() {
        return sampleFrequency;
    }

    /**
     * Sets the value of the sampleFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSampleFrequency(Integer value) {
        this.sampleFrequency = value;
    }

    /**
     * Gets the value of the sampleStart property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSampleStart() {
        return sampleStart;
    }

    /**
     * Sets the value of the sampleStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSampleStart(Integer value) {
        this.sampleStart = value;
    }

    /**
     * Gets the value of the sampleCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSampleCount() {
        return sampleCount;
    }

    /**
     * Sets the value of the sampleCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSampleCount(Integer value) {
        this.sampleCount = value;
    }

    /**
     * Gets the value of the suppressScripts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSuppressScripts() {
        return suppressScripts;
    }

    /**
     * Sets the value of the suppressScripts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSuppressScripts(Integer value) {
        this.suppressScripts = value;
    }

    /**
     * Gets the value of the rowStart property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowStart() {
        return rowStart;
    }

    /**
     * Sets the value of the rowStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowStart(Integer value) {
        this.rowStart = value;
    }

    /**
     * Gets the value of the rowCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * Sets the value of the rowCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowCount(Integer value) {
        this.rowCount = value;
    }

    /**
     * Gets the value of the sortOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortOrder(String value) {
        this.sortOrder = value;
    }

    /**
     * Gets the value of the cacheFilters property.
     * 
     * @return
     *     possible object is
     *     {@link Options.CacheFilters }
     *     
     */
    public Options.CacheFilters getCacheFilters() {
        return cacheFilters;
    }

    /**
     * Sets the value of the cacheFilters property.
     * 
     * @param value
     *     allowed object is
     *     {@link Options.CacheFilters }
     *     
     */
    public void setCacheFilters(Options.CacheFilters value) {
        this.cacheFilters = value;
    }

    /**
     * Gets the value of the filterString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilterString() {
        return filterString;
    }

    /**
     * Sets the value of the filterString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterString(String value) {
        this.filterString = value;
    }

    /**
     * Gets the value of the filterNotFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFilterNotFlag() {
        return filterNotFlag;
    }

    /**
     * Sets the value of the filterNotFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFilterNotFlag(Integer value) {
        this.filterNotFlag = value;
    }

    /**
     * Gets the value of the recentResultBuckets property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecentResultBuckets() {
        return recentResultBuckets;
    }

    /**
     * Sets the value of the recentResultBuckets property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecentResultBuckets(String value) {
        this.recentResultBuckets = value;
    }

    /**
     * Gets the value of the cacheId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCacheId() {
        return cacheId;
    }

    /**
     * Sets the value of the cacheId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCacheId(Long value) {
        this.cacheId = value;
    }

    /**
     * Gets the value of the cacheExpiration property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCacheExpiration() {
        return cacheExpiration;
    }

    /**
     * Sets the value of the cacheExpiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCacheExpiration(Integer value) {
        this.cacheExpiration = value;
    }

    /**
     * Gets the value of the cacheSortFields property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCacheSortFields() {
        return cacheSortFields;
    }

    /**
     * Sets the value of the cacheSortFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCacheSortFields(String value) {
        this.cacheSortFields = value;
    }

    /**
     * Gets the value of the includeUserDetails property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIncludeUserDetails() {
        return includeUserDetails;
    }

    /**
     * Sets the value of the includeUserDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIncludeUserDetails(Integer value) {
        this.includeUserDetails = value;
    }

    /**
     * Gets the value of the includeHiddenFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIncludeHiddenFlag() {
        return includeHiddenFlag;
    }

    /**
     * Sets the value of the includeHiddenFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIncludeHiddenFlag(Integer value) {
        this.includeHiddenFlag = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="filter" type="{urn:TaniumSOAP}cache_filter" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "filter"
    })
    public static class CacheFilters {

        protected List<CacheFilter> filter;

        /**
         * Gets the value of the filter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the filter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFilter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CacheFilter }
         * 
         * 
         */
        public List<CacheFilter> getFilter() {
            if (filter == null) {
                filter = new ArrayList<CacheFilter>();
            }
            return this.filter;
        }

    }

}
