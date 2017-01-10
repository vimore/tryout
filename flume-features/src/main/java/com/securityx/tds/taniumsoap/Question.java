
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for question complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="question">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="selects" type="{urn:TaniumSOAP}select_list" minOccurs="0"/>
 *         &lt;element name="context_group" type="{urn:TaniumSOAP}group" minOccurs="0"/>
 *         &lt;element name="group" type="{urn:TaniumSOAP}group" minOccurs="0"/>
 *         &lt;element name="expire_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="skip_lock_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="expiration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="user" type="{urn:TaniumSOAP}user" minOccurs="0"/>
 *         &lt;element name="management_rights_group" type="{urn:TaniumSOAP}group" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="query_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hidden_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="action_tracking_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="force_computer_id_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="saved_question" type="{urn:TaniumSOAP}saved_question" minOccurs="0"/>
 *         &lt;element name="cache_row_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "question", propOrder = {

})
public class Question {

    protected Integer id;
    protected SelectList selects;
    @XmlElement(name = "context_group")
    protected Group contextGroup;
    protected Group group;
    @XmlElement(name = "expire_seconds")
    protected Integer expireSeconds;
    @XmlElement(name = "skip_lock_flag")
    protected Integer skipLockFlag;
    protected String expiration;
    protected User user;
    @XmlElement(name = "management_rights_group")
    protected Group managementRightsGroup;
    protected String name;
    @XmlElement(name = "query_text")
    protected String queryText;
    @XmlElement(name = "hidden_flag")
    protected Integer hiddenFlag;
    @XmlElement(name = "action_tracking_flag")
    protected Integer actionTrackingFlag;
    @XmlElement(name = "force_computer_id_flag")
    protected Integer forceComputerIdFlag;
    @XmlElement(name = "saved_question")
    protected SavedQuestion savedQuestion;
    @XmlElement(name = "cache_row_id")
    protected Integer cacheRowId;
    protected Integer index;

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
     * Gets the value of the selects property.
     * 
     * @return
     *     possible object is
     *     {@link SelectList }
     *     
     */
    public SelectList getSelects() {
        return selects;
    }

    /**
     * Sets the value of the selects property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectList }
     *     
     */
    public void setSelects(SelectList value) {
        this.selects = value;
    }

    /**
     * Gets the value of the contextGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Group }
     *     
     */
    public Group getContextGroup() {
        return contextGroup;
    }

    /**
     * Sets the value of the contextGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Group }
     *     
     */
    public void setContextGroup(Group value) {
        this.contextGroup = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link Group }
     *     
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link Group }
     *     
     */
    public void setGroup(Group value) {
        this.group = value;
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
     * Gets the value of the skipLockFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkipLockFlag() {
        return skipLockFlag;
    }

    /**
     * Sets the value of the skipLockFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkipLockFlag(Integer value) {
        this.skipLockFlag = value;
    }

    /**
     * Gets the value of the expiration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     * Sets the value of the expiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiration(String value) {
        this.expiration = value;
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
     * Gets the value of the managementRightsGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Group }
     *     
     */
    public Group getManagementRightsGroup() {
        return managementRightsGroup;
    }

    /**
     * Sets the value of the managementRightsGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Group }
     *     
     */
    public void setManagementRightsGroup(Group value) {
        this.managementRightsGroup = value;
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
     * Gets the value of the forceComputerIdFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getForceComputerIdFlag() {
        return forceComputerIdFlag;
    }

    /**
     * Sets the value of the forceComputerIdFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setForceComputerIdFlag(Integer value) {
        this.forceComputerIdFlag = value;
    }

    /**
     * Gets the value of the savedQuestion property.
     * 
     * @return
     *     possible object is
     *     {@link SavedQuestion }
     *     
     */
    public SavedQuestion getSavedQuestion() {
        return savedQuestion;
    }

    /**
     * Sets the value of the savedQuestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link SavedQuestion }
     *     
     */
    public void setSavedQuestion(SavedQuestion value) {
        this.savedQuestion = value;
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

}
