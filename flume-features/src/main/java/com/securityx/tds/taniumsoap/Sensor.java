
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sensor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sensor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="string_count" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="queries" type="{urn:TaniumSOAP}sensor_query_list" minOccurs="0"/>
 *         &lt;element name="source_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="parameters" type="{urn:TaniumSOAP}parameter_list" minOccurs="0"/>
 *         &lt;element name="parameter_definition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="value_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="max_age_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ignore_case_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="exclude_from_parse_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="delimiter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subcolumns" type="{urn:TaniumSOAP}sensor_subcolumn_list" minOccurs="0"/>
 *         &lt;element name="creation_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modification_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last_modified_by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="string_hints" type="{urn:TaniumSOAP}sensor_string_hints" minOccurs="0"/>
 *         &lt;element name="preview_sensor_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="metadata" type="{urn:TaniumSOAP}metadata_list" minOccurs="0"/>
 *         &lt;element name="hidden_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@XmlType(name = "sensor", propOrder = {

})
public class Sensor {

    protected Long id;
    protected String name;
    protected Long hash;
    @XmlElement(name = "string_count")
    protected Long stringCount;
    protected String category;
    protected String description;
    protected SensorQueryList queries;
    @XmlElement(name = "source_id")
    protected Integer sourceId;
    protected ParameterList parameters;
    @XmlElement(name = "parameter_definition")
    protected String parameterDefinition;
    @XmlElement(name = "value_type")
    protected String valueType;
    @XmlElement(name = "max_age_seconds")
    protected Integer maxAgeSeconds;
    @XmlElement(name = "ignore_case_flag")
    protected Integer ignoreCaseFlag;
    @XmlElement(name = "exclude_from_parse_flag")
    protected Integer excludeFromParseFlag;
    protected String delimiter;
    protected SensorSubcolumnList subcolumns;
    @XmlElement(name = "creation_time")
    protected String creationTime;
    @XmlElement(name = "modification_time")
    protected String modificationTime;
    @XmlElement(name = "last_modified_by")
    protected String lastModifiedBy;
    @XmlElement(name = "string_hints")
    protected SensorStringHints stringHints;
    @XmlElement(name = "preview_sensor_flag")
    protected Integer previewSensorFlag;
    protected MetadataList metadata;
    @XmlElement(name = "hidden_flag")
    protected Integer hiddenFlag;
    @XmlElement(name = "cache_row_id")
    protected Integer cacheRowId;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
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
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHash(Long value) {
        this.hash = value;
    }

    /**
     * Gets the value of the stringCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStringCount() {
        return stringCount;
    }

    /**
     * Sets the value of the stringCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStringCount(Long value) {
        this.stringCount = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the queries property.
     * 
     * @return
     *     possible object is
     *     {@link SensorQueryList }
     *     
     */
    public SensorQueryList getQueries() {
        return queries;
    }

    /**
     * Sets the value of the queries property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensorQueryList }
     *     
     */
    public void setQueries(SensorQueryList value) {
        this.queries = value;
    }

    /**
     * Gets the value of the sourceId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * Sets the value of the sourceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSourceId(Integer value) {
        this.sourceId = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterList }
     *     
     */
    public ParameterList getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterList }
     *     
     */
    public void setParameters(ParameterList value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the parameterDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameterDefinition() {
        return parameterDefinition;
    }

    /**
     * Sets the value of the parameterDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameterDefinition(String value) {
        this.parameterDefinition = value;
    }

    /**
     * Gets the value of the valueType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the valueType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueType(String value) {
        this.valueType = value;
    }

    /**
     * Gets the value of the maxAgeSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    /**
     * Sets the value of the maxAgeSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxAgeSeconds(Integer value) {
        this.maxAgeSeconds = value;
    }

    /**
     * Gets the value of the ignoreCaseFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIgnoreCaseFlag() {
        return ignoreCaseFlag;
    }

    /**
     * Sets the value of the ignoreCaseFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIgnoreCaseFlag(Integer value) {
        this.ignoreCaseFlag = value;
    }

    /**
     * Gets the value of the excludeFromParseFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExcludeFromParseFlag() {
        return excludeFromParseFlag;
    }

    /**
     * Sets the value of the excludeFromParseFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExcludeFromParseFlag(Integer value) {
        this.excludeFromParseFlag = value;
    }

    /**
     * Gets the value of the delimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the value of the delimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelimiter(String value) {
        this.delimiter = value;
    }

    /**
     * Gets the value of the subcolumns property.
     * 
     * @return
     *     possible object is
     *     {@link SensorSubcolumnList }
     *     
     */
    public SensorSubcolumnList getSubcolumns() {
        return subcolumns;
    }

    /**
     * Sets the value of the subcolumns property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensorSubcolumnList }
     *     
     */
    public void setSubcolumns(SensorSubcolumnList value) {
        this.subcolumns = value;
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
     * Gets the value of the modificationTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModificationTime() {
        return modificationTime;
    }

    /**
     * Sets the value of the modificationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModificationTime(String value) {
        this.modificationTime = value;
    }

    /**
     * Gets the value of the lastModifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the value of the lastModifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastModifiedBy(String value) {
        this.lastModifiedBy = value;
    }

    /**
     * Gets the value of the stringHints property.
     * 
     * @return
     *     possible object is
     *     {@link SensorStringHints }
     *     
     */
    public SensorStringHints getStringHints() {
        return stringHints;
    }

    /**
     * Sets the value of the stringHints property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensorStringHints }
     *     
     */
    public void setStringHints(SensorStringHints value) {
        this.stringHints = value;
    }

    /**
     * Gets the value of the previewSensorFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPreviewSensorFlag() {
        return previewSensorFlag;
    }

    /**
     * Sets the value of the previewSensorFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPreviewSensorFlag(Integer value) {
        this.previewSensorFlag = value;
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
