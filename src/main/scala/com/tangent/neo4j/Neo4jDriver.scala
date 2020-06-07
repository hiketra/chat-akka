package com.tangent.neo4j

import com.typesafe.config.Config
import neotypes.{Driver, GraphDatabase}
import neotypes.implicits._
import org.neo4j.driver.v1.AuthTokens
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class Neo4jConfig(user: String, password: String, host: String)
object Neo4jConfig {
  def apply(config: Config): Neo4jConfig = {
    Neo4jConfig(config.getString("user"), config.getString("password"), config.getString("host"))
  }
}

class Neo4jDriver(config: Neo4jConfig) {

  val driver = GraphDatabase.driver(config.host, AuthTokens.basic(config.user, config.password))

}
