
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for plugin_sql complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin_sql">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="columns" type="{urn:TaniumSOAP}plugin_sql_column"/>
 *         &lt;element name="result_row" type="{urn:TaniumSOAP}plugin_sql_result" maxOccurs="unbounded"/>
 *         &lt;element name="rows_affected" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="result_count" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "plugin_sql", propOrder = {
    "columns",
    "resultRow",
    "rowsAffected",
    "resultCount"
})
public class PluginSql {

    @XmlElement(required = true)
    protected PluginSqlColumn columns;
    @XmlElement(name = "result_row", required = true)
    protected List<PluginSqlResult> resultRow;
    @XmlElement(name = "rows_affected")
    protected Long rowsAffected;
    @XmlElement(name = "result_count")
    protected Long resultCount;

    /**
     * Gets the value of the columns property.
     * 
     * @return
     *     possible object is
     *     {@link PluginSqlColumn }
     *     
     */
    public PluginSqlColumn getColumns() {
        return columns;
    }

    /**
     * Sets the value of the columns property.
     * 
     * @param value
     *     allowed object is
     *     {@link PluginSqlColumn }
     *     
     */
    public void setColumns(PluginSqlColumn value) {
        this.columns = value;
    }

    /**
     * Gets the value of the resultRow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultRow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultRow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginSqlResult }
     * 
     * 
     */
    public List<PluginSqlResult> getResultRow() {
        if (resultRow == null) {
            resultRow = new ArrayList<PluginSqlResult>();
        }
        return this.resultRow;
    }

    /**
     * Gets the value of the rowsAffected property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRowsAffected() {
        return rowsAffected;
    }

    /**
     * Sets the value of the rowsAffected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRowsAffected(Long value) {
        this.rowsAffected = value;
    }

    /**
     * Gets the value of the resultCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getResultCount() {
        return resultCount;
    }

    /**
     * Sets the value of the resultCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setResultCount(Long value) {
        this.resultCount = value;
    }

}
