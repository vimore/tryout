
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cache_info complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cache_info">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="cache_id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="page_row_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="filtered_row_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="cache_row_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="expiration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errors" type="{urn:TaniumSOAP}error_list" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cache_info", propOrder = {

})
public class CacheInfo {

    @XmlElement(name = "cache_id")
    protected Long cacheId;
    @XmlElement(name = "page_row_count")
    protected Integer pageRowCount;
    @XmlElement(name = "filtered_row_count")
    protected Integer filteredRowCount;
    @XmlElement(name = "cache_row_count")
    protected Integer cacheRowCount;
    protected String expiration;
    protected ErrorList errors;

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
     * Gets the value of the pageRowCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPageRowCount() {
        return pageRowCount;
    }

    /**
     * Sets the value of the pageRowCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPageRowCount(Integer value) {
        this.pageRowCount = value;
    }

    /**
     * Gets the value of the filteredRowCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFilteredRowCount() {
        return filteredRowCount;
    }

    /**
     * Sets the value of the filteredRowCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFilteredRowCount(Integer value) {
        this.filteredRowCount = value;
    }

    /**
     * Gets the value of the cacheRowCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCacheRowCount() {
        return cacheRowCount;
    }

    /**
     * Sets the value of the cacheRowCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCacheRowCount(Integer value) {
        this.cacheRowCount = value;
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
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorList }
     *     
     */
    public ErrorList getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorList }
     *     
     */
    public void setErrors(ErrorList value) {
        this.errors = value;
    }

}
