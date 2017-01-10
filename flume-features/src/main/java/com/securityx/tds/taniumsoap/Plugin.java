
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bundle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="input" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arguments" type="{urn:TaniumSOAP}plugin_argument_list" minOccurs="0"/>
 *         &lt;element name="sql_response" type="{urn:TaniumSOAP}plugin_sql" minOccurs="0"/>
 *         &lt;element name="script_response" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="metadata" type="{urn:TaniumSOAP}metadata_list" minOccurs="0"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="commands" type="{urn:TaniumSOAP}plugin_command_list" minOccurs="0"/>
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
@XmlType(name = "plugin", propOrder = {

})
public class Plugin {

    @XmlElement(required = true)
    protected String name;
    protected String bundle;
    protected String input;
    protected PluginArgumentList arguments;
    @XmlElement(name = "sql_response")
    protected PluginSql sqlResponse;
    @XmlElement(name = "script_response")
    protected String scriptResponse;
    protected String type;
    protected MetadataList metadata;
    protected String path;
    protected String filename;
    protected PluginCommandList commands;
    @XmlElement(name = "cache_row_id")
    protected Integer cacheRowId;

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
     * Gets the value of the bundle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Sets the value of the bundle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBundle(String value) {
        this.bundle = value;
    }

    /**
     * Gets the value of the input property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInput() {
        return input;
    }

    /**
     * Sets the value of the input property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInput(String value) {
        this.input = value;
    }

    /**
     * Gets the value of the arguments property.
     * 
     * @return
     *     possible object is
     *     {@link PluginArgumentList }
     *     
     */
    public PluginArgumentList getArguments() {
        return arguments;
    }

    /**
     * Sets the value of the arguments property.
     * 
     * @param value
     *     allowed object is
     *     {@link PluginArgumentList }
     *     
     */
    public void setArguments(PluginArgumentList value) {
        this.arguments = value;
    }

    /**
     * Gets the value of the sqlResponse property.
     * 
     * @return
     *     possible object is
     *     {@link PluginSql }
     *     
     */
    public PluginSql getSqlResponse() {
        return sqlResponse;
    }

    /**
     * Sets the value of the sqlResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PluginSql }
     *     
     */
    public void setSqlResponse(PluginSql value) {
        this.sqlResponse = value;
    }

    /**
     * Gets the value of the scriptResponse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScriptResponse() {
        return scriptResponse;
    }

    /**
     * Sets the value of the scriptResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScriptResponse(String value) {
        this.scriptResponse = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
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
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * Gets the value of the commands property.
     * 
     * @return
     *     possible object is
     *     {@link PluginCommandList }
     *     
     */
    public PluginCommandList getCommands() {
        return commands;
    }

    /**
     * Sets the value of the commands property.
     * 
     * @param value
     *     allowed object is
     *     {@link PluginCommandList }
     *     
     */
    public void setCommands(PluginCommandList value) {
        this.commands = value;
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
