
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for white_listed_url complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="white_listed_url">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="chunk_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="download_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="metadata" type="{urn:TaniumSOAP}metadata_list" minOccurs="0"/>
 *         &lt;element name="url_regex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "white_listed_url", propOrder = {

})
public class WhiteListedUrl {

    protected Integer id;
    @XmlElement(name = "chunk_id")
    protected String chunkId;
    @XmlElement(name = "download_seconds")
    protected Integer downloadSeconds;
    protected MetadataList metadata;
    @XmlElement(name = "url_regex")
    protected String urlRegex;

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
     * Gets the value of the chunkId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChunkId() {
        return chunkId;
    }

    /**
     * Sets the value of the chunkId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChunkId(String value) {
        this.chunkId = value;
    }

    /**
     * Gets the value of the downloadSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDownloadSeconds() {
        return downloadSeconds;
    }

    /**
     * Sets the value of the downloadSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDownloadSeconds(Integer value) {
        this.downloadSeconds = value;
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
     * Gets the value of the urlRegex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlRegex() {
        return urlRegex;
    }

    /**
     * Sets the value of the urlRegex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlRegex(String value) {
        this.urlRegex = value;
    }

}
