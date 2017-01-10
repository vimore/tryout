package com.securityx.mef.log.mapreduce;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.securityx.log.parsed.avro.*;
import com.securityx.logcollection.parser.utils.E8UUIDGenerator;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.securityx.model.mef.field.api.SupportedFormats;

public class ParsedOutputConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParsedOutputConverter.class);
	public static final BiMap<SupportedFormats, String> outputDir = HashBiMap.create();
	public static final Map<SupportedFormats, Schema> typeMap = new HashMap<SupportedFormats, Schema>();
	public static Schema[] supportedSchemas = { ParsedOutput.SCHEMA$, DeviceinfoMef.SCHEMA$, DnsMef.SCHEMA$,
			FlowMef.SCHEMA$, HetMef.SCHEMA$, IamMef.SCHEMA$, UetMef.SCHEMA$, WebProxyMef.SCHEMA$, IamDBMef.SCHEMA$ , LogCollectionMef.SCHEMA$, SIEMIncidentMef.SCHEMA$};
	public static final Schema SCHEMA = Schema.createUnion(Arrays.asList(supportedSchemas));

	static {
		// output directories
		outputDir.put(SupportedFormats.WebProxyMef, "web_proxy_mef");
		outputDir.put(SupportedFormats.FlowMef, "flow_mef");
		outputDir.put(SupportedFormats.DnsMef, "dns_mef");
		outputDir.put(SupportedFormats.IAMMef, "iam_mef");
		outputDir.put(SupportedFormats.HETMef, "het_mef");
		outputDir.put(SupportedFormats.UETMef, "uet_mef");
		outputDir.put(SupportedFormats.HostCpuMef, "host_cpu_mef");
		outputDir.put(SupportedFormats.HostPortMef, "host_port_mef");
		outputDir.put(SupportedFormats.HostProcessMef, "host_process_mef");
		outputDir.put(SupportedFormats.HostJobMef, "host_job_mef");
		outputDir.put(SupportedFormats.IAMDBMef, "iam_db_mef");
		outputDir.put(SupportedFormats.LogCollectionMef, "log_collection_mef");
		outputDir.put(SupportedFormats.SIEMIncidentMef, "siem_incident_mef");
		// schemas
		typeMap.put(SupportedFormats.WebProxyMef, WebProxyMef.SCHEMA$);
		typeMap.put(SupportedFormats.FlowMef, FlowMef.SCHEMA$);
		typeMap.put(SupportedFormats.DnsMef, DnsMef.SCHEMA$);
		typeMap.put(SupportedFormats.IAMMef, IamMef.SCHEMA$);
		typeMap.put(SupportedFormats.HETMef, HetMef.SCHEMA$);
		typeMap.put(SupportedFormats.UETMef, UetMef.SCHEMA$);
		typeMap.put(SupportedFormats.HostCpuMef, DeviceinfoMef.SCHEMA$);
		typeMap.put(SupportedFormats.HostPortMef, DeviceinfoMef.SCHEMA$);
		typeMap.put(SupportedFormats.HostProcessMef, DeviceinfoMef.SCHEMA$);
		typeMap.put(SupportedFormats.HostJobMef, DeviceinfoMef.SCHEMA$);
		typeMap.put(SupportedFormats.IAMDBMef, IamDBMef.SCHEMA$);
		typeMap.put(SupportedFormats.LogCollectionMef, LogCollectionMef.SCHEMA$);
		typeMap.put(SupportedFormats.SIEMIncidentMef, SIEMIncidentMef.SCHEMA$);
	}

	public static SpecificRecord toMef(ParsedOutput output, SupportedFormats format) {
		SpecificRecord result = output;

		switch (format) {
		case BlueCoat:
			break;
		case CertMef:
			break;
		case DnsMef:
			result = toDnsMef(output);
			break;
		case FlowMef:
			result = toFlowMef(output);
			break;
		case HETMef:
			result = toHetMef(output);
			break;
		case HostCpuMef:
		case HostJobMef:
		case HostPortMef:
		case HostProcessMef:
			result = toDeviceInfo(output);
			break;
		case IAMDBMef:
			result = toIamDBMef(output);
			break;
		case IAMMef:
			result = toIamMef(output);
			break;
		case UETMef:
			result = toUetMef(output);
			break;
		case WebProxyMef:
			result = toWebProxyMef(output);
			break;
		case LogCollectionMef:
			result = toLogCollectionMef(output);
			break;
			case SIEMIncidentMef:
				result = toSIEMIncidentMef(output);
				break;
		default:
			break;
		}
		return result;
	}

	public static LogCollectionMef toLogCollectionMef(ParsedOutput output) {
		LogCollectionMef mef = new LogCollectionMef();
		setLogCollectionProperties(output, mef);
		return mef;
	}

	public static DeviceinfoMef toDeviceInfo(ParsedOutput output) {
		DeviceinfoMef mef = new DeviceinfoMef();
		setCommonProperties(output, mef);
		return mef;
	}

	public static DnsMef toDnsMef(ParsedOutput output) {
		DnsMef mef = new DnsMef();
		setCommonProperties(output, mef);
		return mef;
	}

	public static FlowMef toFlowMef(ParsedOutput output) {
		FlowMef mef = new FlowMef();
		setCommonProperties(output, mef);
		return mef;
	}

	public static HetMef toHetMef(ParsedOutput output) {
		HetMef mef = new HetMef();
		setCommonProperties(output, mef);
		return mef;
	}
	public static SIEMIncidentMef  toSIEMIncidentMef(ParsedOutput output) {
		SIEMIncidentMef mef = new SIEMIncidentMef();
		setCommonProperties(output, mef);
		return mef;
	}

	public static IamMef toIamMef(ParsedOutput output) {
		IamMef mef = new IamMef();
		String[] sanitizedColumns = { "destinationNameOrIp", "destinationUserName", "deviceNameOrIp", "sourceNameOrIp",
				"sourceUserName" };
		sanitizeColontoDot(output.getValues(), sanitizedColumns);
		setCommonProperties(output, mef);
		return mef;
	}

	public static IamDBMef toIamDBMef(ParsedOutput output) {
		IamDBMef mef = new IamDBMef();
		setCommonProperties(output, mef);
		try {
			BeanUtils.setProperty(mef, "creationTimeISO", unixToIso(output.getValues().get("creationDate")));
			BeanUtils.setProperty(mef, "lastModificationTimeISO", unixToIso(output.getValues().get("lastModificationDate")));
			BeanUtils.setProperty(mef, "badPasswordTimeISO", unixToIso(output.getValues().get("badPasswordTime")));
			BeanUtils.setProperty(mef, "lastLogoffTimeISO", unixToIso(output.getValues().get("lastLogoffTime")));
			BeanUtils.setProperty(mef, "lastLogonTimeISO", unixToIso(output.getValues().get("lastLogon")));
			BeanUtils.setProperty(mef, "pwdLastSetTimeISO", unixToIso(output.getValues().get("pwdLastSet")));
			BeanUtils.setProperty(mef, "accountExpiresTimeISO", unixToIso(output.getValues().get("accountExpires")));
			BeanUtils.setProperty(mef, "lockoutTimeISO", unixToIso(output.getValues().get("lockoutTime")));

		} catch (IllegalAccessException e) {
			LOGGER.error("conversion error ", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("conversion error ", e);
		}

		return mef;
	}


	public static UetMef toUetMef(ParsedOutput output) {
		UetMef mef = new UetMef();
		setCommonProperties(output, mef);
		return mef;
	}

	public static WebProxyMef toWebProxyMef(ParsedOutput output) {
		WebProxyMef mef = new WebProxyMef();
		String[] sanitizedColumns = { "destinationDnsDomain", "destinationNameOrIp", "requestClientApplication",
				"responseContentType", "requestScheme", "requestPath", "requestQuery", "requestReferer" };
		sanitizeStripControlChars(output.getValues(), sanitizedColumns);
		String[] removeColons = { "deviceAddress", "deviceNameOrIp", "sourceAddress", "sourceNameOrIp" };
		sanitizeColontoDot(output.getValues(), removeColons);
		output.setRawLog(CharMatcher.JAVA_ISO_CONTROL.removeFrom(output.getRawLog()));
		if (output.getValues().get("bytesIn") == null) {
			output.getValues().put("bytesIn", "0");
		}
		if (output.getValues().get("bytesOut") == null) {
			output.getValues().put("bytesOut", "0");
		}
		CharSequence requestReferer = output.getValues().get("requestReferer");
		if (requestReferer == null || requestReferer.toString().trim().length() == 0) {
			output.getValues().put("requestReferer", "-");
		}
		setCommonProperties(output, mef);
		return mef;
	}

	private static void sanitizeColontoDot(Map<CharSequence, CharSequence> values, String[] columns) {
		for (String col : columns) {
			CharSequence str = values.get(col);
			if (str != null && str.length() != 0) {
				values.put(col, str.toString().replaceAll(":", "."));
			}
		}
	}

	private static void sanitizeStripControlChars(Map<CharSequence, CharSequence> values, String[] columns) {
		for (String col : columns) {
			CharSequence str = values.get(col);
			if (str != null && str.length() != 0) {
				values.put(col, CharMatcher.JAVA_ISO_CONTROL.removeFrom(str));
			}
		}
	}

	private static String unixToIso(CharSequence time) {
		DateTimeZone.setDefault(DateTimeZone.UTC);
		Long longTime = null;
		if (time != null && time.length() != 0) {
			longTime = Long.parseLong(time.toString().trim());
		}
		DateTime result = new DateTime(longTime);
		return result.toString();
	}

	private static void setCommonProperties(ParsedOutput output, Object mef) {
		try {
			BeanUtils.setProperty(mef, "rawLog", output.getRawLog());
			BeanUtils.setProperty(mef, "outputFormat", output.getOutputFormat());
			BeanUtils.copyProperties(mef, output.getValues());
			//BeanUtils.setProperty(mef, "uuid", UUID.randomUUID().toString());
			CharSequence uuid = output.getValues().get("uuid");
			if (uuid == null || uuid.length() == 0) {
				BeanUtils.setProperty(mef, "uuid", getUuidGenerator().generateUUIfromRawLogOnly(output.getRawLog()));
			} else {
				BeanUtils.setProperty(mef, "uuid", uuid);
			}
			CharSequence externalLogSourceType = output.getValues().get("externalLogSourceType");
			if (externalLogSourceType == null || externalLogSourceType.length() == 0) {
				BeanUtils.setProperty(mef, "externalLogSourceType", "Unknown");
			}
			BeanUtils.setProperty(mef, "startTimeISO", unixToIso(output.getValues().get("startTime")));
		} catch (IllegalAccessException e) {
			LOGGER.error("conversion error ", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("conversion error ", e);
		}

	}

	private static void setLogCollectionProperties(ParsedOutput output, Object mef) {
		try {
			//BeanUtils.setProperty(mef, "logBytes", output.getValues().get("logBytes"));
			//BeanUtils.setProperty(mef, "logCollectionHost", output.getValues().get("logCollectionHost"));
			//BeanUtils.setProperty(mef, "receiptTime", output.getValues().get("receiptTime"));
			//BeanUtils.setProperty(mef, "outputFormat", output.getOutputFormat());
			//BeanUtils.setProperty(mef, "eventsPerFormat,", output.getValues().get("eventsPerFormat"));
			BeanUtils.copyProperties(mef, output.getValues());
			CharSequence uuid = output.getValues().get("uuid");
			if (uuid == null || uuid.length() == 0) {
				BeanUtils.setProperty(mef, "uuid", getUuidGenerator().generateUUIfromRawLogOnly(output.getRawLog()));
			} else {
				BeanUtils.setProperty(mef, "uuid", uuid);
			}
		} catch (IllegalAccessException e) {
			LOGGER.error("conversion error ", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("conversion error ", e);
		}

	}

	private static E8UUIDGenerator _uuidGenerator;

	public static E8UUIDGenerator getUuidGenerator(){
		if (_uuidGenerator == null){
			_uuidGenerator = new E8UUIDGenerator(3) ;
		}
		return  _uuidGenerator;
	}
}
