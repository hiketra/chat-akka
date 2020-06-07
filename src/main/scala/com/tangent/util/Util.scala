package com.tangent.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Util {

  object ZdtUtils {
    val formatter = DateTimeFormatter.ISO_TIME
    def currentTime: String = ZonedDateTime.now().format(formatter)
    def fromString(t: String): ZonedDateTime = ZonedDateTime.parse(t, formatter)
  }

  implicit class Sanitiser(s: String) {
    def sanitiseUserStringInput() = {
      s.replace(raw""""""", raw"""\""")
    }
  }

  object CypherUtils {

    implicit class KVConversionBool(s: (String, Boolean)) {
      def toCyph(terminateObj: Boolean = false) = raw""" ${s._1} : ${s._2.toString}${if(!terminateObj) "," else ""}"""
    }

    implicit class KVConversionString(s: (String, String)) {
      def toCyph(terminateObj: Boolean = false) = raw""" ${s._1} : "${s._2.sanitiseUserStringInput}"${if(!terminateObj) "," else ""}"""
    }
    implicit class KVOptString(s: (String, Option[String])) {
      def toCyph(terminateObj: Boolean = false) = raw""" ${s._1} : ${s._2.fold("null")(s => raw""" "${s.sanitiseUserStringInput}" """)} ${if(!terminateObj) "," else ""} """
    }

    implicit class KVOptBoolean(s: (String, Option[Boolean])) {
      def toCyph(terminateObj: Boolean = false) = raw""" ${s._1} : ${s._2.fold("null")(s => s.toString)}${if(!terminateObj) "," else ""}"""
    }

  }

}
