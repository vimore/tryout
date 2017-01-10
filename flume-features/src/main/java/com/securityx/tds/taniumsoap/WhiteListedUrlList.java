
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for white_listed_url_list complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="white_listed_url_list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="white_listed_url" type="{urn:TaniumSOAP}white_listed_url" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "white_listed_url_list", propOrder = {
    "whiteListedUrl"
})
public class WhiteListedUrlList {

    @XmlElement(name = "white_listed_url")
    protected List<WhiteListedUrl> whiteListedUrl;

    /**
     * Gets the value of the whiteListedUrl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the whiteListedUrl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWhiteListedUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WhiteListedUrl }
     * 
     * 
     */
    public List<WhiteListedUrl> getWhiteListedUrl() {
        if (whiteListedUrl == null) {
            whiteListedUrl = new ArrayList<WhiteListedUrl>();
        }
        return this.whiteListedUrl;
    }

}
