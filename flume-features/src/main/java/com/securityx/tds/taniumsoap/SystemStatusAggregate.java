
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for system_status_aggregate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="system_status_aggregate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="send_forward_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="send_backward_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="send_none_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="send_ok_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="receive_forward_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="receive_backward_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="receive_none_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="receive_ok_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="slowlink_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="blocked_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="leader_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="normal_count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="versions" type="{urn:TaniumSOAP}version_aggregate_list"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "system_status_aggregate", propOrder = {
    "sendForwardCount",
    "sendBackwardCount",
    "sendNoneCount",
    "sendOkCount",
    "receiveForwardCount",
    "receiveBackwardCount",
    "receiveNoneCount",
    "receiveOkCount",
    "slowlinkCount",
    "blockedCount",
    "leaderCount",
    "normalCount",
    "versions"
})
public class SystemStatusAggregate {

    @XmlElement(name = "send_forward_count")
    protected int sendForwardCount;
    @XmlElement(name = "send_backward_count")
    protected int sendBackwardCount;
    @XmlElement(name = "send_none_count")
    protected int sendNoneCount;
    @XmlElement(name = "send_ok_count")
    protected int sendOkCount;
    @XmlElement(name = "receive_forward_count")
    protected int receiveForwardCount;
    @XmlElement(name = "receive_backward_count")
    protected int receiveBackwardCount;
    @XmlElement(name = "receive_none_count")
    protected int receiveNoneCount;
    @XmlElement(name = "receive_ok_count")
    protected int receiveOkCount;
    @XmlElement(name = "slowlink_count")
    protected int slowlinkCount;
    @XmlElement(name = "blocked_count")
    protected int blockedCount;
    @XmlElement(name = "leader_count")
    protected int leaderCount;
    @XmlElement(name = "normal_count")
    protected int normalCount;
    @XmlElement(required = true)
    protected VersionAggregateList versions;

    /**
     * Gets the value of the sendForwardCount property.
     * 
     */
    public int getSendForwardCount() {
        return sendForwardCount;
    }

    /**
     * Sets the value of the sendForwardCount property.
     * 
     */
    public void setSendForwardCount(int value) {
        this.sendForwardCount = value;
    }

    /**
     * Gets the value of the sendBackwardCount property.
     * 
     */
    public int getSendBackwardCount() {
        return sendBackwardCount;
    }

    /**
     * Sets the value of the sendBackwardCount property.
     * 
     */
    public void setSendBackwardCount(int value) {
        this.sendBackwardCount = value;
    }

    /**
     * Gets the value of the sendNoneCount property.
     * 
     */
    public int getSendNoneCount() {
        return sendNoneCount;
    }

    /**
     * Sets the value of the sendNoneCount property.
     * 
     */
    public void setSendNoneCount(int value) {
        this.sendNoneCount = value;
    }

    /**
     * Gets the value of the sendOkCount property.
     * 
     */
    public int getSendOkCount() {
        return sendOkCount;
    }

    /**
     * Sets the value of the sendOkCount property.
     * 
     */
    public void setSendOkCount(int value) {
        this.sendOkCount = value;
    }

    /**
     * Gets the value of the receiveForwardCount property.
     * 
     */
    public int getReceiveForwardCount() {
        return receiveForwardCount;
    }

    /**
     * Sets the value of the receiveForwardCount property.
     * 
     */
    public void setReceiveForwardCount(int value) {
        this.receiveForwardCount = value;
    }

    /**
     * Gets the value of the receiveBackwardCount property.
     * 
     */
    public int getReceiveBackwardCount() {
        return receiveBackwardCount;
    }

    /**
     * Sets the value of the receiveBackwardCount property.
     * 
     */
    public void setReceiveBackwardCount(int value) {
        this.receiveBackwardCount = value;
    }

    /**
     * Gets the value of the receiveNoneCount property.
     * 
     */
    public int getReceiveNoneCount() {
        return receiveNoneCount;
    }

    /**
     * Sets the value of the receiveNoneCount property.
     * 
     */
    public void setReceiveNoneCount(int value) {
        this.receiveNoneCount = value;
    }

    /**
     * Gets the value of the receiveOkCount property.
     * 
     */
    public int getReceiveOkCount() {
        return receiveOkCount;
    }

    /**
     * Sets the value of the receiveOkCount property.
     * 
     */
    public void setReceiveOkCount(int value) {
        this.receiveOkCount = value;
    }

    /**
     * Gets the value of the slowlinkCount property.
     * 
     */
    public int getSlowlinkCount() {
        return slowlinkCount;
    }

    /**
     * Sets the value of the slowlinkCount property.
     * 
     */
    public void setSlowlinkCount(int value) {
        this.slowlinkCount = value;
    }

    /**
     * Gets the value of the blockedCount property.
     * 
     */
    public int getBlockedCount() {
        return blockedCount;
    }

    /**
     * Sets the value of the blockedCount property.
     * 
     */
    public void setBlockedCount(int value) {
        this.blockedCount = value;
    }

    /**
     * Gets the value of the leaderCount property.
     * 
     */
    public int getLeaderCount() {
        return leaderCount;
    }

    /**
     * Sets the value of the leaderCount property.
     * 
     */
    public void setLeaderCount(int value) {
        this.leaderCount = value;
    }

    /**
     * Gets the value of the normalCount property.
     * 
     */
    public int getNormalCount() {
        return normalCount;
    }

    /**
     * Sets the value of the normalCount property.
     * 
     */
    public void setNormalCount(int value) {
        this.normalCount = value;
    }

    /**
     * Gets the value of the versions property.
     * 
     * @return
     *     possible object is
     *     {@link VersionAggregateList }
     *     
     */
    public VersionAggregateList getVersions() {
        return versions;
    }

    /**
     * Sets the value of the versions property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionAggregateList }
     *     
     */
    public void setVersions(VersionAggregateList value) {
        this.versions = value;
    }

}
