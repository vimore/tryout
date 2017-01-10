package com.securityx.modelfeature.utils

import java.text.DecimalFormat

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable.{Map => MutableMap}

object MiscUtils {
  val iso_format: org.joda.time.format.DateTimeFormatter = ISODateTimeFormat.dateTime();
  val df: DecimalFormat = new DecimalFormat("00");

  /**
   * Takes a camel cased identifier name and returns an underscore separated
   * name
   *
   * Example:
   * camelToUnderscores("thisIsA1Test") == "this_is_a_1_test"
   */
  def camelToUnderscores(name: String) = "[A-Z\\d]".r.replaceAllIn(name, { m =>
    "_" + m.group(0).toLowerCase()
  })

  /*
  * Takes an underscore separated identifier name and returns a camel cased one
  *
  * Example:
  * underscoreToCamel("this_is_a_1_test") == "thisIsA1Test"
  */

  def underscoreToCamel(name: String) = "_([a-z\\d])".r.replaceAllIn(name, { m =>
    m.group(1).toUpperCase()
  })

  def getEndOfHour(time: String): String = {
    // Convert the input time to the last millisecond of the given hour.
    val endOfHour = new org.joda.time.DateTime(time, DateTimeZone.UTC).plusHours(1).hourOfDay.roundFloorCopy.minusMillis(1);
    endOfHour.toString(iso_format)
  }

  def getBeginOfDay(time: String): String = {
    // Truncate an arbitrary datetime to the first hour of the day UTC.
    val beginOfDay = new org.joda.time.DateTime(time, DateTimeZone.UTC).dayOfWeek.roundFloorCopy
    beginOfDay.toString(iso_format)
  }

  def getBeginOfDayDateTime(time: String): DateTime = {
    // Truncate an arbitrary datetime to the first hour of the day UTC.
    val beginOfDay = new org.joda.time.DateTime(time, DateTimeZone.UTC).dayOfWeek.roundFloorCopy
    beginOfDay
  }

  def getEndOfDay(time: String): String = {
    // Return the last millisecond of the day specified by the input time.
    val endOfDay = new org.joda.time.DateTime(time, DateTimeZone.UTC).plusDays(1).dayOfWeek.roundFloorCopy.minusMillis(1);
    endOfDay.toString(iso_format)
  }

  def getPercentage(num1: Double, num2: Double): Double = {
    if (num2 == 0) {
      0d
    }
    (num1 / num2) * 100
  }

  def getYearString(time: String): String = {
    val dateTime: DateTime = new DateTime(time, DateTimeZone.UTC)
    df.format(dateTime.getYear)
  }

  def getMonthOfYearString(time: String): String = {
    val dateTime: DateTime = new DateTime(time, DateTimeZone.UTC)
    df.format(dateTime.getMonthOfYear)
  }

  def getDayOfMonthString(time: String): String = {
    val dateTime: DateTime = new DateTime(time, DateTimeZone.UTC)
    df.format(dateTime.getDayOfMonth)
  }

  /**
   * Converts DateTime String into YYYYMMDD
   * eg: if input dateTime = "2015-01-13T00:00:00.000Z",  output String will be 20150113
   *
   * @param time String representing DateTime
   * @return date String with format YYYYMMDD
   */
  def getYMDString(time: String): String = {
    val dateTime: DateTime = new DateTime(time, DateTimeZone.UTC)
    df.format(dateTime.getYear) + df.format(dateTime.getMonthOfYear) + df.format(dateTime.getDayOfMonth)
  }

  /**
   * Converts DateTime String into YYYY-MM-DD
   * eg: if input dateTime = "2015-01-13T00:00:00.000Z",  output String will be 20150113
   *
   * @param time String representing DateTime
   * @return date String with format YYYY-MM-DD
   */
  def getYMDSeparatedString(time: String): String = {
    val dateTime: DateTime = new DateTime(time, DateTimeZone.UTC)
    df.format(dateTime.getYear) + "-" + df.format(dateTime.getMonthOfYear) + "-" + df.format(dateTime.getDayOfMonth)
  }

  /**
   * Checks if the String is null/empty or not.
   * If null/empty, returns "NA" or else returns the original string.
   * @param str
   * @return
   */
  def stringNullCheck(str: String) = {
    var ret = "N/A"
    if (str != null && str.nonEmpty) {
      ret = str
    }
    ret
  }

  def stringReplaceNullValue(str: String, conf: FeatureServiceConfiguration) = {
    var ret = str
    val REGEX = "^" + conf.getFixNullValue.getNullValue + "$"
    val EMPTY = ""
    if (str == null) {
      ret = EMPTY
    }
    if (conf.getFixNullValue.isEnabled && str != null && str.equals(conf.getFixNullValue.getNullValue)) {
      ret = str.replaceAll(REGEX, EMPTY)
    }
    ret
  }

  def getQueryFragmmentReplaceNullValueExists(name: String, columnName: EntityThreat.Value, sqlStr: String, conf: FeatureServiceConfiguration) = {
    var sql = sqlStr
    var keyExists = false
    if(name!=null){
      if (name.nonEmpty) {
        sql = sql + " AND " + columnName + " = ? "
      }else if (conf.getFixNullValue.isEnabled) {
        sql = sql + " AND " + columnName + " = '" + conf.getFixNullValue.getNullValue + "' "
      } else {
        // search for empty string
        sql = sql + " AND " + columnName + " = '' "
      }
      keyExists = true
    } else {
      sql = sql + " AND " + columnName + " IS NULL "
    }
    (sql, keyExists)
  }

  def getQueryFragmmentReplaceNullValue(name: String, columnName: EntityThreat.Value, sqlStr: String, conf: FeatureServiceConfiguration) = {
    val (sql, keyExists) = getQueryFragmmentReplaceNullValueExists(name, columnName, sqlStr, conf)
    sql
  }
}
