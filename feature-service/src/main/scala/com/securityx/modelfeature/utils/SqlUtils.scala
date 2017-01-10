package com.securityx.modelfeature.utils

object SqlUtils {

  def getOperatorFromOperatorString(operatorString : String): String = {
    var returnOperator = ""

    if(operatorString.contains(Operators.CONTAINS.toString.toLowerCase)){
      returnOperator = "LIKE"
    } else if(operatorString.contains(Operators.NOT_EQUALS.toString.toLowerCase)){
      returnOperator = " != "
    } else if(operatorString.contains(Operators.EQUALS.toString.toLowerCase)){
      returnOperator = "="
    } else if(operatorString.contains(Operators.LESS_THAN_EQUAL.toString.toLowerCase)){
      returnOperator = "<="
    } else if(operatorString.contains(Operators.LESS_THAN.toString.toLowerCase)){
      returnOperator = "<"
    } else if(operatorString.contains(Operators.GREATER_THAN_EQUAL.toString.toLowerCase)){
      returnOperator = ">="
    } else if(operatorString.contains(Operators.GREATER_THAN.toString.toLowerCase)){
      returnOperator = ">"
    }
    returnOperator
  }


  /**
   * Ent_Host_Properties table store ip_addresses as Array of String.
   * eg: ["10.10.10.1"]
   * To run a sql query while multiple "Like" operators separated by Or/And,
   * we need to parse the list of ips (peers) and form the queryString. This method basically forms the query string
   *
   * @param values comma-separated ip addresses. eg: ""10.1.1.1", "10.2.2.2", "10.1.1.3""
   * @return returns where-clause of the query which can be appended to a Sql String.
   */
  def getLikeOperatorString(values: String, columnName: String): String = {
    val valuesArray = values.split(",")
    var likeOperatorString = ""
    valuesArray.foreach { peer =>
      var str = peer.replace("\"", "")
      str = str.replace("[", "")
      str = str.replace("]", "")
      str = str.trim
      str = columnName + " LIKE '%" + str + "%'"
      if (likeOperatorString.isEmpty) {
        likeOperatorString = str
      } else {
        likeOperatorString = likeOperatorString + " OR " + str
      }
    }

    likeOperatorString
  }

  def getEqualsOperatorString(values: String, columnName: String): String = {
    val valuesArray = values.split(",")
    var likeOperatorString = ""
    valuesArray.foreach { peer =>
      var str = peer.replace("\"", "")
      str = str.replace("[", "")
      str = str.replace("]", "")
      str = str.trim
      str = columnName + " = '" + str + "'"
      if (likeOperatorString.isEmpty) {
        likeOperatorString = str
      } else {
        likeOperatorString = likeOperatorString + " OR " + str
      }
    }

    likeOperatorString
  }
}
