package com.auth0

import scala.io.Source

object GenerateLuaBlacklistArray extends App {

  println(Source.fromResource("ExampleBlacklist.txt").getLines().take(50).map(ip => s""" "$ip", \n """).mkString(""))

}
