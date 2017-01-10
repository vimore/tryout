
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for saved_question complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="saved_question">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="question" type="{urn:TaniumSOAP}question" minOccurs="0"/>
 *         &lt;element name="public_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hidden_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="issue_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="issue_seconds_never_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="expire_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sort_column" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="query_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="packages" type="{urn:TaniumSOAP}package_spec_list" minOccurs="0"/>
 *         &lt;element name="row_count_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="keep_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="archive_enabled_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="most_recent_question_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="user" type="{urn:TaniumSOAP}user" minOccurs="0"/>
 *         &lt;element name="archive_owner" type="{urn:TaniumSOAP}user" minOccurs="0"/>
 *         &lt;element name="action_tracking_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mod_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mod_user" type="{urn:TaniumSOAP}user" minOccurs="0"/>
 *         &lt;element name="metadata" type="{urn:TaniumSOAP}metadata_list" minOccurs="0"/>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="cache_row_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "saved_question", propOrder = {

})
public class SavedQuestion {

    protected Integer id;
    protected String name;
    protected Question question;
    @XmlElement(name = "public_flag")
    protected Integer publicFlag;
    @XmlElement(name = "hidden_flag")
    protected Integer hiddenFlag;
    @XmlElement(name = "issue_seconds")
    protected Integer issueSeconds;
    @XmlElement(name = "issue_seconds_never_flag")
    protected Integer issueSecondsNeverFlag;
    @XmlElement(name = "expire_seconds")
    protected Integer expireSeconds;
    @XmlElement(name = "sort_column")
    protected Integer sortColumn;
    @XmlElement(name = "query_text")
    protected String queryText;
    protected PackageSpecList packages;
    @XmlElement(name = "row_count_flag")
    protected Integer rowCountFlag;
    @XmlElement(name = "keep_seconds")
    protected Integer keepSeconds;
    @XmlElement(name = "archive_enabled_flag")
    protected Integer archiveEnabledFlag;
    @XmlElement(name = "most_recent_question_id")
    protected Integer mostRecentQuestionId;
    protected User user;
    @XmlElement(name = "archive_owner")
    protected User archiveOwner;
    @XmlElement(name = "action_tracking_flag")
    protected Integer actionTrackingFlag;
    @XmlElement(name = "mod_time")
    protected String modTime;
    @XmlElement(name = "mod_user")
    protected User modUser;
    protected MetadataList metadata;
    protected Integer index;
    @XmlElement(name = "cache_row_id")
    protected Integer cacheRowId;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the question property.
     * 
     * @return
     *     possible object is
     *     {@link Question }
     *     
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Sets the value of the question property.
     * 
     * @param value
     *     allowed object is
     *     {@link Question }
     *     
     */
    public void setQuestion(Question value) {
        this.question = value;
    }

    /**
     * Gets the value of the publicFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPublicFlag() {
        return publicFlag;
    }

    /**
     * Sets the value of the publicFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPublicFlag(Integer value) {
        this.publicFlag = value;
    }

    /**
     * Gets the value of the hiddenFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHiddenFlag() {
        return hiddenFlag;
    }

    /**
     * Sets the value of the hiddenFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHiddenFlag(Integer value) {
        this.hiddenFlag = value;
    }

    /**
     * Gets the value of the issueSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIssueSeconds() {
        return issueSeconds;
    }

    /**
     * Sets the value of the issueSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIssueSeconds(Integer value) {
        this.issueSeconds = value;
    }

    /**
     * Gets the value of the issueSecondsNeverFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIssueSecondsNeverFlag() {
        return issueSecondsNeverFlag;
    }

    /**
     * Sets the value of the issueSecondsNeverFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIssueSecondsNeverFlag(Integer value) {
        this.issueSecondsNeverFlag = value;
    }

    /**
     * Gets the value of the expireSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * Sets the value of the expireSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExpireSeconds(Integer value) {
        this.expireSeconds = value;
    }

    /**
     * Gets the value of the sortColumn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSortColumn() {
        return sortColumn;
    }

    /**
     * Sets the value of the sortColumn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSortColumn(Integer value) {
        this.sortColumn = value;
    }

    /**
     * Gets the value of the queryText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryText() {
        return queryText;
    }

    /**
     * Sets the value of the queryText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryText(String value) {
        this.queryText = value;
    }

    /**
     * Gets the value of the packages property.
     * 
     * @return
     *     possible object is
     *     {@link PackageSpecList }
     *     
     */
    public PackageSpecList getPackages() {
        return packages;
    }

    /**
     * Sets the value of the packages property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageSpecList }
     *     
     */
    public void setPackages(PackageSpecList value) {
        this.packages = value;
    }

    /**
     * Gets the value of the rowCountFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowCountFlag() {
        return rowCountFlag;
    }

    /**
     * Sets the value of the rowCountFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowCountFlag(Integer value) {
        this.rowCountFlag = value;
    }

    /**
     * Gets the value of the keepSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKeepSeconds() {
        return keepSeconds;
    }

    /**
     * Sets the value of the keepSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeepSeconds(Integer value) {
        this.keepSeconds = value;
    }

    /**
     * Gets the value of the archiveEnabledFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArchiveEnabledFlag() {
        return archiveEnabledFlag;
    }

    /**
     * Sets the value of the archiveEnabledFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArchiveEnabledFlag(Integer value) {
        this.archiveEnabledFlag = value;
    }

    /**
     * Gets the value of the mostRecentQuestionId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMostRecentQuestionId() {
        return mostRecentQuestionId;
    }

    /**
     * Sets the value of the mostRecentQuestionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMostRecentQuestionId(Integer value) {
        this.mostRecentQuestionId = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setUser(User value) {
        this.user = value;
    }

    /**
     * Gets the value of the archiveOwner property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getArchiveOwner() {
        return archiveOwner;
    }

    /**
     * Sets the value of the archiveOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setArchiveOwner(User value) {
        this.archiveOwner = value;
    }

    /**
     * Gets the value of the actionTrackingFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getActionTrackingFlag() {
        return actionTrackingFlag;
    }

    /**
     * Sets the value of the actionTrackingFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setActionTrackingFlag(Integer value) {
        this.actionTrackingFlag = value;
    }

    /**
     * Gets the value of the modTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModTime() {
        return modTime;
    }

    /**
     * Sets the value of the modTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModTime(String value) {
        this.modTime = value;
    }

    /**
     * Gets the value of the modUser property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getModUser() {
        return modUser;
    }

    /**
     * Sets the value of the modUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setModUser(User value) {
        this.modUser = value;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * @return
     *     possible object is
     *     {@link MetadataList }
     *     
     */
    public MetadataList getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetadataList }
     *     
     */
    public void setMetadata(MetadataList value) {
        this.metadata = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIndex(Integer value) {
        this.index = value;
    }

    /**
     * Gets the value of the cacheRowId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCacheRowId() {
        return cacheRowId;
    }

    /**
     * Sets the value of the cacheRowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCacheRowId(Integer value) {
        this.cacheRowId = value;
    }

}
