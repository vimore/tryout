package com.securityx.model.mef.morphline.command.util;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Split key value pairs set date2timestamp { inputFieldName : "message" #
 * optional Defaults to "message" dateFormat : " " # optional Default ","
 * precision : "ms" # optional s, ms, ns Default "ms" timeZone : "UTC" #
 * optional, default "UTC" Locale : "" # IETF BCP 47 language code, default
 * "en_US" }
 */
public class DateToTimeStampCommandBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("date2timestamp");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    List<String> inputFieldNames = null;
    String inputFieldName = null;
    if (config.hasPath("inputFieldNames")) {
      inputFieldNames = config.getStringList("inputFieldNames");
    }
    if (config.hasPath("inputFieldName")) {
      inputFieldName = config.getString("inputFieldName");
    }
    if (inputFieldNames == null || inputFieldNames.isEmpty()) {
      inputFieldNames = new ArrayList();
    }
    if (inputFieldName == null || "".equals(inputFieldName)) {
      if ( inputFieldNames.isEmpty())
       inputFieldNames.add("time");
    } else {
      inputFieldNames.add(inputFieldName);
    }

    String dateFormat = config.getString("dateFormat");
    if (dateFormat == null) {
      dateFormat = "yyyy-MM-dd HH:mm:ss";//2013-11-17 01:04:39
    }

    String precision = config.getString("precision");
    if (precision == null) {
      precision = "ms";
    }
    String timeZoneStr = config.getString("timeZone");
    if (timeZoneStr == null) {
      timeZoneStr = "UTC";
    }
    boolean forceTz = false;
    if (config.hasPath("forceTimeZone")) {
      forceTz = config.getBoolean("forceTimeZone");
    }
      Boolean guessYear;
    if (config.hasPath("guessYear")) {
      guessYear = config.getBoolean("guessYear");
      if (guessYear == null) {
        guessYear = false;
      }
    } else {
      guessYear = false;
    }

    String locale = null;
    if (config.hasPath("locale")) {
      locale = config.getString("locale");
    }
    if (locale == null) {
      locale = "en_US";
    }
    TimeZone tz = TimeZone.getTimeZone(timeZoneStr);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    simpleDateFormat.setTimeZone(tz);
    simpleDateFormat.setDateFormatSymbols(DateFormatSymbols.getInstance(new Locale(locale)));
    Date startOf21stCentury = new GregorianCalendar(2000, 0, 1).getTime();
    simpleDateFormat.set2DigitYearStart(startOf21stCentury);
    return new DateToTimeStampCommand(config,
            context,
            parent,
            child,
            inputFieldNames,
            simpleDateFormat,
            precision, guessYear, forceTz);
  }
}
