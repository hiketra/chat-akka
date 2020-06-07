package com.tangent.chat

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.tangent.neo4j.Neo4jDriver
import com.tangent.util.CirceSupport
import com.typesafe.config.Config
import neotypes.implicits.mappers.results._
import neotypes.implicits.syntax.string._
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.AutoDerivation

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag
import Model._

import com.tangent.util.Util.ZdtUtils._

class ChatService(driver: Neo4jDriver) extends CirceSupport {
  val nDriver = driver.driver

  def createRootChannel(rootChannelRequest: RootChannelRequest) = {
    s"CREATE (m: Message:Channel ${rootChannelRequest.toMessage.toCypherObj}) RETURN m;".query[Message].single(nDriver.session)
  }

  def retrieveRootChannels(): Future[List[Message]] = {
    s"MATCH (c: Message) WHERE c.isRootChannel = TRUE RETURN c;".query[Message].list(nDriver.session)
  }

  def createChildMessage(childMessageRequest: ChildMessageRequest): Future[Message] = {
      raw""" MATCH (parent: Message) WHERE parent.messageId = "${childMessageRequest.parentId}"
           | CREATE (child: Message ${childMessageRequest.toMessage.toCypherObj}),
           | (parent)-[:HAS_CHILD_MESSAGE]->(child)
           | RETURN child;
           |""".stripMargin.query[Message].single(nDriver.session)
  }

  def retrieveChildren(messageId: String): Future[List[Message]] = {
    raw"""MATCH (m: Message) WHERE m.messageId = "${messageId}" MATCH (m)-[:HAS_CHILD_MESSAGE]->(a:Message) RETURN a;""".query[Message].list(nDriver.session)
  }

  def createNewChildChannel(childChannelRequest: ChildChannelRequest): Future[Message] = {
   lazy val upgradeParentMessage = raw"""MATCH (newChannel: Message) WHERE newChannel.messageId = "${childChannelRequest.parentId}"
         | SET newChannel += {hasChildren: true, channelDescription: ${childChannelRequest.channelDescription.fold("null")(s => raw""""$s"""")},
         | channelSince: "${currentTime}", channelName: ${childChannelRequest.channelName.fold("null")(s => raw""""$s"""")}}
         | RETURN newChannel;
         |""".stripMargin.query[Message].single(nDriver.session)
    lazy val childMessage = createChildMessage(ChildMessageRequest(childChannelRequest.parentId,
      childChannelRequest.subId, childChannelRequest.message))
    for {
      message <- upgradeParentMessage
      _ <- childMessage
    } yield message
  }

  def createMessage(message: Message): Future[Message] = {
    s"CREATE (m: Message ${message.toCypherObj}) RETURN m;".query[Message].single(nDriver.session)
  }

}
