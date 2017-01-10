
package com.securityx.tds.taniumsoap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for object_list complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="object_list">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="question" type="{urn:TaniumSOAP}question" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="questions" type="{urn:TaniumSOAP}question_list" minOccurs="0"/>
 *         &lt;element name="group" type="{urn:TaniumSOAP}group" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="groups" type="{urn:TaniumSOAP}group_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="saved_question" type="{urn:TaniumSOAP}saved_question" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="saved_questions" type="{urn:TaniumSOAP}saved_question_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="archived_question" type="{urn:TaniumSOAP}archived_question" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="archived_questions" type="{urn:TaniumSOAP}archived_question_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parse_job" type="{urn:TaniumSOAP}parse_job" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parse_jobs" type="{urn:TaniumSOAP}parse_job_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parse_result_group" type="{urn:TaniumSOAP}parse_result_group" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parse_result_groups" type="{urn:TaniumSOAP}parse_result_group_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="action" type="{urn:TaniumSOAP}action" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="actions" type="{urn:TaniumSOAP}action_list" minOccurs="0"/>
 *         &lt;element name="saved_action" type="{urn:TaniumSOAP}saved_action" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="saved_actions" type="{urn:TaniumSOAP}saved_action_list" minOccurs="0"/>
 *         &lt;element name="action_stop" type="{urn:TaniumSOAP}action_stop" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="action_stops" type="{urn:TaniumSOAP}action_stop_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="package_spec" type="{urn:TaniumSOAP}package_spec" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="package_specs" type="{urn:TaniumSOAP}package_spec_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sensor" type="{urn:TaniumSOAP}sensor" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sensors" type="{urn:TaniumSOAP}sensor_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user" type="{urn:TaniumSOAP}user" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="users" type="{urn:TaniumSOAP}user_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="roles" type="{urn:TaniumSOAP}user_role_list" minOccurs="0"/>
 *         &lt;element name="client_status" type="{urn:TaniumSOAP}client_status" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="system_setting" type="{urn:TaniumSOAP}system_setting" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="saved_action_approval" type="{urn:TaniumSOAP}saved_action_approval" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="system_status" type="{urn:TaniumSOAP}system_status_list" minOccurs="0"/>
 *         &lt;element name="system_settings" type="{urn:TaniumSOAP}system_settings_list" minOccurs="0"/>
 *         &lt;element name="client_count" type="{urn:TaniumSOAP}client_count" minOccurs="0"/>
 *         &lt;element name="plugin" type="{urn:TaniumSOAP}plugin" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="plugins" type="{urn:TaniumSOAP}plugin_list" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="white_listed_url" type="{urn:TaniumSOAP}white_listed_url" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="white_listed_urls" type="{urn:TaniumSOAP}white_listed_url_list" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "object_list", propOrder = {
    "question",
    "questions",
    "group",
    "groups",
    "savedQuestion",
    "savedQuestions",
    "archivedQuestion",
    "archivedQuestions",
    "parseJob",
    "parseJobs",
    "parseResultGroup",
    "parseResultGroups",
    "action",
    "actions",
    "savedAction",
    "savedActions",
    "actionStop",
    "actionStops",
    "packageSpec",
    "packageSpecs",
    "sensor",
    "sensors",
    "user",
    "users",
    "roles",
    "clientStatus",
    "systemSetting",
    "savedActionApproval",
    "systemStatus",
    "systemSettings",
    "clientCount",
    "plugin",
    "plugins",
    "whiteListedUrl",
    "whiteListedUrls"
})
public class ObjectList {

    protected List<Question> question;
    protected QuestionList questions;
    protected List<Group> group;
    protected List<GroupList> groups;
    @XmlElement(name = "saved_question")
    protected List<SavedQuestion> savedQuestion;
    @XmlElement(name = "saved_questions")
    protected List<SavedQuestionList> savedQuestions;
    @XmlElement(name = "archived_question")
    protected List<ArchivedQuestion> archivedQuestion;
    @XmlElement(name = "archived_questions")
    protected List<ArchivedQuestionList> archivedQuestions;
    @XmlElement(name = "parse_job")
    protected List<ParseJob> parseJob;
    @XmlElement(name = "parse_jobs")
    protected List<ParseJobList> parseJobs;
    @XmlElement(name = "parse_result_group")
    protected List<ParseResultGroup> parseResultGroup;
    @XmlElement(name = "parse_result_groups")
    protected List<ParseResultGroupList> parseResultGroups;
    protected List<Action> action;
    protected ActionList actions;
    @XmlElement(name = "saved_action")
    protected List<SavedAction> savedAction;
    @XmlElement(name = "saved_actions")
    protected SavedActionList savedActions;
    @XmlElement(name = "action_stop")
    protected List<ActionStop> actionStop;
    @XmlElement(name = "action_stops")
    protected List<ActionStopList> actionStops;
    @XmlElement(name = "package_spec")
    protected List<PackageSpec> packageSpec;
    @XmlElement(name = "package_specs")
    protected List<PackageSpecList> packageSpecs;
    protected List<Sensor> sensor;
    protected List<SensorList> sensors;
    protected List<User> user;
    protected List<UserList> users;
    protected UserRoleList roles;
    @XmlElement(name = "client_status")
    protected List<ClientStatus> clientStatus;
    @XmlElement(name = "system_setting")
    protected List<SystemSetting> systemSetting;
    @XmlElement(name = "saved_action_approval")
    protected List<SavedActionApproval> savedActionApproval;
    @XmlElement(name = "system_status")
    protected SystemStatusList systemStatus;
    @XmlElement(name = "system_settings")
    protected SystemSettingsList systemSettings;
    @XmlElement(name = "client_count")
    protected ClientCount clientCount;
    protected List<Plugin> plugin;
    protected List<PluginList> plugins;
    @XmlElement(name = "white_listed_url")
    protected List<WhiteListedUrl> whiteListedUrl;
    @XmlElement(name = "white_listed_urls")
    protected WhiteListedUrlList whiteListedUrls;

    /**
     * Gets the value of the question property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the question property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuestion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Question }
     * 
     * 
     */
    public List<Question> getQuestion() {
        if (question == null) {
            question = new ArrayList<Question>();
        }
        return this.question;
    }

    /**
     * Gets the value of the questions property.
     * 
     * @return
     *     possible object is
     *     {@link QuestionList }
     *     
     */
    public QuestionList getQuestions() {
        return questions;
    }

    /**
     * Sets the value of the questions property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuestionList }
     *     
     */
    public void setQuestions(QuestionList value) {
        this.questions = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the group property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group }
     * 
     * 
     */
    public List<Group> getGroup() {
        if (group == null) {
            group = new ArrayList<Group>();
        }
        return this.group;
    }

    /**
     * Gets the value of the groups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GroupList }
     * 
     * 
     */
    public List<GroupList> getGroups() {
        if (groups == null) {
            groups = new ArrayList<GroupList>();
        }
        return this.groups;
    }

    /**
     * Gets the value of the savedQuestion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the savedQuestion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSavedQuestion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SavedQuestion }
     * 
     * 
     */
    public List<SavedQuestion> getSavedQuestion() {
        if (savedQuestion == null) {
            savedQuestion = new ArrayList<SavedQuestion>();
        }
        return this.savedQuestion;
    }

    /**
     * Gets the value of the savedQuestions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the savedQuestions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSavedQuestions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SavedQuestionList }
     * 
     * 
     */
    public List<SavedQuestionList> getSavedQuestions() {
        if (savedQuestions == null) {
            savedQuestions = new ArrayList<SavedQuestionList>();
        }
        return this.savedQuestions;
    }

    /**
     * Gets the value of the archivedQuestion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archivedQuestion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchivedQuestion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchivedQuestion }
     * 
     * 
     */
    public List<ArchivedQuestion> getArchivedQuestion() {
        if (archivedQuestion == null) {
            archivedQuestion = new ArrayList<ArchivedQuestion>();
        }
        return this.archivedQuestion;
    }

    /**
     * Gets the value of the archivedQuestions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archivedQuestions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchivedQuestions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchivedQuestionList }
     * 
     * 
     */
    public List<ArchivedQuestionList> getArchivedQuestions() {
        if (archivedQuestions == null) {
            archivedQuestions = new ArrayList<ArchivedQuestionList>();
        }
        return this.archivedQuestions;
    }

    /**
     * Gets the value of the parseJob property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parseJob property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParseJob().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParseJob }
     * 
     * 
     */
    public List<ParseJob> getParseJob() {
        if (parseJob == null) {
            parseJob = new ArrayList<ParseJob>();
        }
        return this.parseJob;
    }

    /**
     * Gets the value of the parseJobs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parseJobs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParseJobs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParseJobList }
     * 
     * 
     */
    public List<ParseJobList> getParseJobs() {
        if (parseJobs == null) {
            parseJobs = new ArrayList<ParseJobList>();
        }
        return this.parseJobs;
    }

    /**
     * Gets the value of the parseResultGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parseResultGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParseResultGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParseResultGroup }
     * 
     * 
     */
    public List<ParseResultGroup> getParseResultGroup() {
        if (parseResultGroup == null) {
            parseResultGroup = new ArrayList<ParseResultGroup>();
        }
        return this.parseResultGroup;
    }

    /**
     * Gets the value of the parseResultGroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parseResultGroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParseResultGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParseResultGroupList }
     * 
     * 
     */
    public List<ParseResultGroupList> getParseResultGroups() {
        if (parseResultGroups == null) {
            parseResultGroups = new ArrayList<ParseResultGroupList>();
        }
        return this.parseResultGroups;
    }

    /**
     * Gets the value of the action property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the action property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Action }
     * 
     * 
     */
    public List<Action> getAction() {
        if (action == null) {
            action = new ArrayList<Action>();
        }
        return this.action;
    }

    /**
     * Gets the value of the actions property.
     * 
     * @return
     *     possible object is
     *     {@link ActionList }
     *     
     */
    public ActionList getActions() {
        return actions;
    }

    /**
     * Sets the value of the actions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionList }
     *     
     */
    public void setActions(ActionList value) {
        this.actions = value;
    }

    /**
     * Gets the value of the savedAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the savedAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSavedAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SavedAction }
     * 
     * 
     */
    public List<SavedAction> getSavedAction() {
        if (savedAction == null) {
            savedAction = new ArrayList<SavedAction>();
        }
        return this.savedAction;
    }

    /**
     * Gets the value of the savedActions property.
     * 
     * @return
     *     possible object is
     *     {@link SavedActionList }
     *     
     */
    public SavedActionList getSavedActions() {
        return savedActions;
    }

    /**
     * Sets the value of the savedActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link SavedActionList }
     *     
     */
    public void setSavedActions(SavedActionList value) {
        this.savedActions = value;
    }

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

    /**
     * Gets the value of the actionStops property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionStops property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionStops().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActionStopList }
     * 
     * 
     */
    public List<ActionStopList> getActionStops() {
        if (actionStops == null) {
            actionStops = new ArrayList<ActionStopList>();
        }
        return this.actionStops;
    }

    /**
     * Gets the value of the packageSpec property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the packageSpec property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackageSpec().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackageSpec }
     * 
     * 
     */
    public List<PackageSpec> getPackageSpec() {
        if (packageSpec == null) {
            packageSpec = new ArrayList<PackageSpec>();
        }
        return this.packageSpec;
    }

    /**
     * Gets the value of the packageSpecs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the packageSpecs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackageSpecs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackageSpecList }
     * 
     * 
     */
    public List<PackageSpecList> getPackageSpecs() {
        if (packageSpecs == null) {
            packageSpecs = new ArrayList<PackageSpecList>();
        }
        return this.packageSpecs;
    }

    /**
     * Gets the value of the sensor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sensor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSensor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sensor }
     * 
     * 
     */
    public List<Sensor> getSensor() {
        if (sensor == null) {
            sensor = new ArrayList<Sensor>();
        }
        return this.sensor;
    }

    /**
     * Gets the value of the sensors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sensors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSensors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SensorList }
     * 
     * 
     */
    public List<SensorList> getSensors() {
        if (sensors == null) {
            sensors = new ArrayList<SensorList>();
        }
        return this.sensors;
    }

    /**
     * Gets the value of the user property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the user property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link User }
     * 
     * 
     */
    public List<User> getUser() {
        if (user == null) {
            user = new ArrayList<User>();
        }
        return this.user;
    }

    /**
     * Gets the value of the users property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the users property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserList }
     * 
     * 
     */
    public List<UserList> getUsers() {
        if (users == null) {
            users = new ArrayList<UserList>();
        }
        return this.users;
    }

    /**
     * Gets the value of the roles property.
     * 
     * @return
     *     possible object is
     *     {@link UserRoleList }
     *     
     */
    public UserRoleList getRoles() {
        return roles;
    }

    /**
     * Sets the value of the roles property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserRoleList }
     *     
     */
    public void setRoles(UserRoleList value) {
        this.roles = value;
    }

    /**
     * Gets the value of the clientStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clientStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClientStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClientStatus }
     * 
     * 
     */
    public List<ClientStatus> getClientStatus() {
        if (clientStatus == null) {
            clientStatus = new ArrayList<ClientStatus>();
        }
        return this.clientStatus;
    }

    /**
     * Gets the value of the systemSetting property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the systemSetting property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSystemSetting().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SystemSetting }
     * 
     * 
     */
    public List<SystemSetting> getSystemSetting() {
        if (systemSetting == null) {
            systemSetting = new ArrayList<SystemSetting>();
        }
        return this.systemSetting;
    }

    /**
     * Gets the value of the savedActionApproval property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the savedActionApproval property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSavedActionApproval().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SavedActionApproval }
     * 
     * 
     */
    public List<SavedActionApproval> getSavedActionApproval() {
        if (savedActionApproval == null) {
            savedActionApproval = new ArrayList<SavedActionApproval>();
        }
        return this.savedActionApproval;
    }

    /**
     * Gets the value of the systemStatus property.
     * 
     * @return
     *     possible object is
     *     {@link SystemStatusList }
     *     
     */
    public SystemStatusList getSystemStatus() {
        return systemStatus;
    }

    /**
     * Sets the value of the systemStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemStatusList }
     *     
     */
    public void setSystemStatus(SystemStatusList value) {
        this.systemStatus = value;
    }

    /**
     * Gets the value of the systemSettings property.
     * 
     * @return
     *     possible object is
     *     {@link SystemSettingsList }
     *     
     */
    public SystemSettingsList getSystemSettings() {
        return systemSettings;
    }

    /**
     * Sets the value of the systemSettings property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemSettingsList }
     *     
     */
    public void setSystemSettings(SystemSettingsList value) {
        this.systemSettings = value;
    }

    /**
     * Gets the value of the clientCount property.
     * 
     * @return
     *     possible object is
     *     {@link ClientCount }
     *     
     */
    public ClientCount getClientCount() {
        return clientCount;
    }

    /**
     * Sets the value of the clientCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientCount }
     *     
     */
    public void setClientCount(ClientCount value) {
        this.clientCount = value;
    }

    /**
     * Gets the value of the plugin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the plugin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlugin().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Plugin }
     * 
     * 
     */
    public List<Plugin> getPlugin() {
        if (plugin == null) {
            plugin = new ArrayList<Plugin>();
        }
        return this.plugin;
    }

    /**
     * Gets the value of the plugins property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the plugins property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlugins().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PluginList }
     * 
     * 
     */
    public List<PluginList> getPlugins() {
        if (plugins == null) {
            plugins = new ArrayList<PluginList>();
        }
        return this.plugins;
    }

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

    /**
     * Gets the value of the whiteListedUrls property.
     * 
     * @return
     *     possible object is
     *     {@link WhiteListedUrlList }
     *     
     */
    public WhiteListedUrlList getWhiteListedUrls() {
        return whiteListedUrls;
    }

    /**
     * Sets the value of the whiteListedUrls property.
     * 
     * @param value
     *     allowed object is
     *     {@link WhiteListedUrlList }
     *     
     */
    public void setWhiteListedUrls(WhiteListedUrlList value) {
        this.whiteListedUrls = value;
    }

}
