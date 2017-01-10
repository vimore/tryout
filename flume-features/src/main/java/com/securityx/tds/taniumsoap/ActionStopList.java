
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for action_stop_list complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="action_stop_list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action_stop" type="{urn:TaniumSOAP}action_stop" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "action_stop_list", propOrder = {
    "actionStop"
})
public class ActionStopList {

    @XmlElement(name = "action_stop")
    protected List<ActionStop> actionStop;

    /**
     * Gets the value of the actionStop property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionStop property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionStop().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActionStop }
     * 
     * 
     */
    public List<ActionStop> getActionStop() {
        if (actionStop == null) {
            actionStop = new ArrayList<ActionStop>();
        }
        return this.actionStop;
    }

}
