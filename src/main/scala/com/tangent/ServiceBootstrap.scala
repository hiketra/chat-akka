package com.tangent

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.concat
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.tangent.chat.ChatService
import com.tangent.neo4j.{Neo4jConfig, Neo4jDriver}
import com.tangent.routes.ChatRoutes
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.sslconfig.util.ConfigLoader

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

object ServiceBootstrap extends LazyLogging {

  def main(args: Array[String]): Unit = {

    implicit lazy val system: ActorSystem = ActorSystem("chat-service")
    implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

    implicit lazy val timeout: Timeout = 10.seconds

    val neo4jDriver = new Neo4jDriver(Neo4jConfig(ConfigFactory.load().getConfig("service.neo4j")))
    val chatService = new ChatService(neo4jDriver)
    val chatRoutes = new ChatRoutes(chatService).routes



    val concatRoutes = concat(cors(){chatRoutes})


    val interface = "0.0.0.0"
    val port = 8080

    Http().bindAndHandle(concatRoutes, interface, port)

    logger.info(s"Server online, $interface:$port")

  }

}
