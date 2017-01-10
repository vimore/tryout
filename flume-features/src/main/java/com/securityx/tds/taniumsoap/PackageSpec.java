
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for package_spec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="package_spec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="display_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="files" type="{urn:TaniumSOAP}package_file_list" minOccurs="0"/>
 *         &lt;element name="file_templates" type="{urn:TaniumSOAP}package_file_template_list" minOccurs="0"/>
 *         &lt;element name="command" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="command_timeout" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="expire_seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hidden_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="signature" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="source_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="verify_group_id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="parameters" type="{urn:TaniumSOAP}parameter_list" minOccurs="0"/>
 *         &lt;element name="parameter_definition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sensors" type="{urn:TaniumSOAP}sensor_list" minOccurs="0"/>
 *         &lt;element name="creation_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modification_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last_modified_by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="available_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deleted_flag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="metadata" type="{urn:TaniumSOAP}metadata_list" minOccurs="0"/>
 *         &lt;element name="last_update" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "package_spec", propOrder = {

})
public class PackageSpec {

    protected Integer id;
    protected String name;
    @XmlElement(name = "display_name")
    protected String displayName;
    protected PackageFileList files;
    @XmlElement(name = "file_templates")
    protected PackageFileTemplateList fileTemplates;
    protected String command;
    @XmlElement(name = "command_timeout")
    protected Integer commandTimeout;
    @XmlElement(name = "expire_seconds")
    protected Integer expireSeconds;
    @XmlElement(name = "hidden_flag")
    protected Integer hiddenFlag;
    protected String signature;
    @XmlElement(name = "source_id")
    protected Integer sourceId;
    @XmlElement(name = "verify_group_id")
    protected Integer verifyGroupId;
    protected ParameterList parameters;
    @XmlElement(name = "parameter_definition")
    protected String parameterDefinition;
    protected SensorList sensors;
    @XmlElement(name = "creation_time")
    protected String creationTime;
    @XmlElement(name = "modification_time")
    protected String modificationTime;
    @XmlElement(name = "last_modified_by")
    protected String lastModifiedBy;
    @XmlElement(name = "available_time")
    protected String availableTime;
    @XmlElement(name = "deleted_flag")
    protected Integer deletedFlag;
    protected MetadataList metadata;
    @XmlElement(name = "last_update")
    protected String lastUpdate;
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
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the files property.
     * 
     * @return
     *     possible object is
     *     {@link PackageFileList }
     *     
     */
    public PackageFileList getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageFileList }
     *     
     */
    public void setFiles(PackageFileList value) {
        this.files = value;
    }

    /**
     * Gets the value of the fileTemplates property.
     * 
     * @return
     *     possible object is
     *     {@link PackageFileTemplateList }
     *     
     */
    public PackageFileTemplateList getFileTemplates() {
        return fileTemplates;
    }

    /**
     * Sets the value of the fileTemplates property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageFileTemplateList }
     *     
     */
    public void setFileTemplates(PackageFileTemplateList value) {
        this.fileTemplates = value;
    }

    /**
     * Gets the value of the command property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommand(String value) {
        this.command = value;
    }

    /**
     * Gets the value of the commandTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCommandTimeout() {
        return commandTimeout;
    }

    /**
     * Sets the value of the commandTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCommandTimeout(Integer value) {
        this.commandTimeout = value;
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
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignature(String value) {
        this.signature = value;
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
     * Gets the value of the verifyGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVerifyGroupId() {
        return verifyGroupId;
    }

    /**
     * Sets the value of the verifyGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVerifyGroupId(Integer value) {
        this.verifyGroupId = value;
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
     * Gets the value of the sensors property.
     * 
     * @return
     *     possible object is
     *     {@link SensorList }
     *     
     */
    public SensorList getSensors() {
        return sensors;
    }

    /**
     * Sets the value of the sensors property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensorList }
     *     
     */
    public void setSensors(SensorList value) {
        this.sensors = value;
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
     * Gets the value of the availableTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvailableTime() {
        return availableTime;
    }

    /**
     * Sets the value of the availableTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvailableTime(String value) {
        this.availableTime = value;
    }

    /**
     * Gets the value of the deletedFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDeletedFlag() {
        return deletedFlag;
    }

    /**
     * Sets the value of the deletedFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDeletedFlag(Integer value) {
        this.deletedFlag = value;
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
     * Gets the value of the lastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the value of the lastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUpdate(String value) {
        this.lastUpdate = value;
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
