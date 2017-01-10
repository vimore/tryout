
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for client_status complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="client_status">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="host_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="computer_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipaddress_client" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipaddress_server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protocol_version" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="full_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last_registration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="send_state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receive_state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="port_number" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@XmlType(name = "client_status", propOrder = {

})
public class ClientStatus {

    @XmlElement(name = "host_name")
    protected String hostName;
    @XmlElement(name = "computer_id")
    protected String computerId;
    @XmlElement(name = "ipaddress_client")
    protected String ipaddressClient;
    @XmlElement(name = "ipaddress_server")
    protected String ipaddressServer;
    @XmlElement(name = "protocol_version")
    protected Integer protocolVersion;
    @XmlElement(name = "full_version")
    protected String fullVersion;
    @XmlElement(name = "last_registration")
    protected String lastRegistration;
    @XmlElement(name = "send_state")
    protected String sendState;
    @XmlElement(name = "receive_state")
    protected String receiveState;
    protected String status;
    @XmlElement(name = "port_number")
    protected Integer portNumber;
    @XmlElement(name = "cache_row_id")
    protected Integer cacheRowId;

    /**
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the value of the hostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostName(String value) {
        this.hostName = value;
    }

    /**
     * Gets the value of the computerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComputerId() {
        return computerId;
    }

    /**
     * Sets the value of the computerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComputerId(String value) {
        this.computerId = value;
    }

    /**
     * Gets the value of the ipaddressClient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpaddressClient() {
        return ipaddressClient;
    }

    /**
     * Sets the value of the ipaddressClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpaddressClient(String value) {
        this.ipaddressClient = value;
    }

    /**
     * Gets the value of the ipaddressServer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpaddressServer() {
        return ipaddressServer;
    }

    /**
     * Sets the value of the ipaddressServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpaddressServer(String value) {
        this.ipaddressServer = value;
    }

    /**
     * Gets the value of the protocolVersion property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets the value of the protocolVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProtocolVersion(Integer value) {
        this.protocolVersion = value;
    }

    /**
     * Gets the value of the fullVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullVersion() {
        return fullVersion;
    }

    /**
     * Sets the value of the fullVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullVersion(String value) {
        this.fullVersion = value;
    }

    /**
     * Gets the value of the lastRegistration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastRegistration() {
        return lastRegistration;
    }

    /**
     * Sets the value of the lastRegistration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastRegistration(String value) {
        this.lastRegistration = value;
    }

    /**
     * Gets the value of the sendState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendState() {
        return sendState;
    }

    /**
     * Sets the value of the sendState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendState(String value) {
        this.sendState = value;
    }

    /**
     * Gets the value of the receiveState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiveState() {
        return receiveState;
    }

    /**
     * Sets the value of the receiveState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiveState(String value) {
        this.receiveState = value;
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
     * Gets the value of the portNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPortNumber() {
        return portNumber;
    }

    /**
     * Sets the value of the portNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPortNumber(Integer value) {
        this.portNumber = value;
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
