
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for action complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="action">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="target_group" type="{urn:TaniumSOAP}group" minOccurs="0"/>
 *         &lt;element name="action_group" type="{urn:TaniumSOAP}group" minOccurs="0"/>
 *         &lt;element name="package_spec" type="{urn:TaniumSOAP}package_spec" minOccurs="0"/>
 *         &lt;element name="start_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expiration_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="skip_lock_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="expire_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="distribute_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="creation_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stopped_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="user" type="{urn:TaniumSOAP}user" minOccurs="0"/>
 *         &lt;element name="approver" type="{urn:TaniumSOAP}user" minOccurs="0"/>
 *         &lt;element name="history_saved_question" type="{urn:TaniumSOAP}saved_question" minOccurs="0"/>
 *         &lt;element name="saved_action" type="{urn:TaniumSOAP}saved_action" minOccurs="0"/>
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
@XmlType(name = "action", propOrder = {

})
public class Action {

    protected Integer id;
    protected String name;
    protected String comment;
    @XmlElement(name = "target_group")
    protected Group targetGroup;
    @XmlElement(name = "action_group")
    protected Group actionGroup;
    @XmlElement(name = "package_spec")
    protected PackageSpec packageSpec;
    @XmlElement(name = "start_time")
    protected String startTime;
    @XmlElement(name = "expiration_time")
    protected String expirationTime;
    protected String status;
    @XmlElement(name = "skip_lock_flag")
    protected Integer skipLockFlag;
    @XmlElement(name = "expire_seconds")
    protected Integer expireSeconds;
    @XmlElement(name = "distribute_seconds")
    protected Integer distributeSeconds;
    @XmlElement(name = "creation_time")
    protected String creationTime;
    @XmlElement(name = "stopped_flag")
    protected Integer stoppedFlag;
    protected User user;
    protected User approver;
    @XmlElement(name = "history_saved_question")
    protected SavedQuestion historySavedQuestion;
    @XmlElement(name = "saved_action")
    protected SavedAction savedAction;
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
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the targetGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Group }
     *     
     */
    public Group getTargetGroup() {
        return targetGroup;
    }

    /**
     * Sets the value of the targetGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Group }
     *     
     */
    public void setTargetGroup(Group value) {
        this.targetGroup = value;
    }

    /**
     * Gets the value of the actionGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Group }
     *     
     */
    public Group getActionGroup() {
        return actionGroup;
    }

    /**
     * Sets the value of the actionGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Group }
     *     
     */
    public void setActionGroup(Group value) {
        this.actionGroup = value;
    }

    /**
     * Gets the value of the packageSpec property.
     * 
     * @return
     *     possible object is
     *     {@link PackageSpec }
     *     
     */
    public PackageSpec getPackageSpec() {
        return packageSpec;
    }

    /**
     * Sets the value of the packageSpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageSpec }
     *     
     */
    public void setPackageSpec(PackageSpec value) {
        this.packageSpec = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTime(String value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the expirationTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the value of the expirationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirationTime(String value) {
        this.expirationTime = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
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
     * Gets the value of the distributeSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDistributeSeconds() {
        return distributeSeconds;
    }

    /**
     * Sets the value of the distributeSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDistributeSeconds(Integer value) {
        this.distributeSeconds = value;
    }

    /**
     * Gets the value of the creationTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationTime(String value) {
        this.creationTime = value;
    }

    /**
     * Gets the value of the stoppedFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStoppedFlag() {
        return stoppedFlag;
    }

    /**
     * Sets the value of the stoppedFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStoppedFlag(Integer value) {
        this.stoppedFlag = value;
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
     * Gets the value of the approver property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getApprover() {
        return approver;
    }

    /**
     * Sets the value of the approver property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setApprover(User value) {
        this.approver = value;
    }

    /**
     * Gets the value of the historySavedQuestion property.
     * 
     * @return
     *     possible object is
     *     {@link SavedQuestion }
     *     
     */
    public SavedQuestion getHistorySavedQuestion() {
        return historySavedQuestion;
    }

    /**
     * Sets the value of the historySavedQuestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link SavedQuestion }
     *     
     */
    public void setHistorySavedQuestion(SavedQuestion value) {
        this.historySavedQuestion = value;
    }

    /**
     * Gets the value of the savedAction property.
     * 
     * @return
     *     possible object is
     *     {@link SavedAction }
     *     
     */
    public SavedAction getSavedAction() {
        return savedAction;
    }

    /**
     * Sets the value of the savedAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link SavedAction }
     *     
     */
    public void setSavedAction(SavedAction value) {
        this.savedAction = value;
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
