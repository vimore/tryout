
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sensor_subcolumn_list complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sensor_subcolumn_list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subcolumn" type="{urn:TaniumSOAP}sensor_subcolumn" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sensor_subcolumn_list", propOrder = {
    "subcolumn"
})
public class SensorSubcolumnList {

    protected List<SensorSubcolumn> subcolumn;

    /**
     * Gets the value of the subcolumn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subcolumn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubcolumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SensorSubcolumn }
     * 
     * 
     */
    public List<SensorSubcolumn> getSubcolumn() {
        if (subcolumn == null) {
            subcolumn = new ArrayList<SensorSubcolumn>();
        }
        return this.subcolumn;
    }

}
