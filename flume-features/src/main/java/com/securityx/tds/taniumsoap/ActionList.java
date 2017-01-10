
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for action_list complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="action_list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="info" type="{urn:TaniumSOAP}action_list_info" minOccurs="0"/>
 *         &lt;element name="action" type="{urn:TaniumSOAP}action" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cache_info" type="{urn:TaniumSOAP}cache_info" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "action_list", propOrder = {
    "info",
    "action",
    "cacheInfo"
})
public class ActionList {

    protected ActionListInfo info;
    protected List<Action> action;
    @XmlElement(name = "cache_info")
    protected CacheInfo cacheInfo;

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link ActionListInfo }
     *     
     */
    public ActionListInfo getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionListInfo }
     *     
     */
    public void setInfo(ActionListInfo value) {
        this.info = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the action property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Action }
     * 
     * 
     */
    public List<Action> getAction() {
        if (action == null) {
            action = new ArrayList<Action>();
        }
        return this.action;
    }

    /**
     * Gets the value of the cacheInfo property.
     * 
     * @return
     *     possible object is
     *     {@link CacheInfo }
     *     
     */
    public CacheInfo getCacheInfo() {
        return cacheInfo;
    }

    /**
     * Sets the value of the cacheInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link CacheInfo }
     *     
     */
    public void setCacheInfo(CacheInfo value) {
        this.cacheInfo = value;
    }

}
