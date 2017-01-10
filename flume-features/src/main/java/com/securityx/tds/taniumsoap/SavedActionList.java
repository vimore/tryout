
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for saved_action_list complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="saved_action_list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="saved_action" type="{urn:TaniumSOAP}saved_action" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "saved_action_list", propOrder = {
    "savedAction",
    "cacheInfo"
})
public class SavedActionList {

    @XmlElement(name = "saved_action")
    protected List<SavedAction> savedAction;
    @XmlElement(name = "cache_info")
    protected CacheInfo cacheInfo;

    /**
     * Gets the value of the savedAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the savedAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSavedAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SavedAction }
     * 
     * 
     */
    public List<SavedAction> getSavedAction() {
        if (savedAction == null) {
            savedAction = new ArrayList<SavedAction>();
        }
        return this.savedAction;
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
