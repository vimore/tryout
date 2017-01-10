
package com.securityx.tds.taniumsoap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for saved_action_policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="saved_action_policy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="saved_question_id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="saved_question_group_id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="row_filter_group_id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="pair_group_id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="max_age" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="min_count" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "saved_action_policy", propOrder = {

})
public class SavedActionPolicy {

    @XmlElement(name = "saved_question_id")
    protected Long savedQuestionId;
    @XmlElement(name = "saved_question_group_id")
    protected Long savedQuestionGroupId;
    @XmlElement(name = "row_filter_group_id")
    protected Long rowFilterGroupId;
    @XmlElement(name = "pair_group_id")
    protected Long pairGroupId;
    @XmlElement(name = "max_age")
    protected Long maxAge;
    @XmlElement(name = "min_count")
    protected Integer minCount;

    /**
     * Gets the value of the savedQuestionId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSavedQuestionId() {
        return savedQuestionId;
    }

    /**
     * Sets the value of the savedQuestionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSavedQuestionId(Long value) {
        this.savedQuestionId = value;
    }

    /**
     * Gets the value of the savedQuestionGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSavedQuestionGroupId() {
        return savedQuestionGroupId;
    }

    /**
     * Sets the value of the savedQuestionGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSavedQuestionGroupId(Long value) {
        this.savedQuestionGroupId = value;
    }

    /**
     * Gets the value of the rowFilterGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRowFilterGroupId() {
        return rowFilterGroupId;
    }

    /**
     * Sets the value of the rowFilterGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRowFilterGroupId(Long value) {
        this.rowFilterGroupId = value;
    }

    /**
     * Gets the value of the pairGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPairGroupId() {
        return pairGroupId;
    }

    /**
     * Sets the value of the pairGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPairGroupId(Long value) {
        this.pairGroupId = value;
    }

    /**
     * Gets the value of the maxAge property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the value of the maxAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxAge(Long value) {
        this.maxAge = value;
    }

    /**
     * Gets the value of the minCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinCount() {
        return minCount;
    }

    /**
     * Sets the value of the minCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinCount(Integer value) {
        this.minCount = value;
    }

}
