package com.tangent.chat

import java.util.UUID

import com.tangent.util.Util.CypherUtils._
import com.tangent.util.Util.ZdtUtils._

object Model {

  case class AssociateChannelRequest(subId: String, channelId: String)

  case class User(
      subId: String,
      avatar: Option[String],
      subscribedChannels: List[String],
      joinedSince: String
  ) {
    def toCypherObj: String = {
      raw"""
                 | {
                 |  ${("subId", subId).toCyph()}
                 |  ${("avatar", avatar).toCyph()}
                 |  ${("subscribedChannels", subscribedChannels).toCyph()}
                 |  ${("joinedSince", joinedSince).toCyph(true)}
                 | }
                 |""".stripMargin
    }
  }

  case class CreateUserRequest(subId: String, avatar: Option[String]) {
    def toUser = {
      User(
        subId = subId,
        avatar = avatar,
        subscribedChannels = Nil,
        joinedSince = currentTime
      )
    }
  }

  case class Message(
      messageId: String,
      subId: String,
      avatar: Option[String],
      timestamp: String,
      parentId: Option[String],
      message: String,
      isRootChannel: Boolean = false,
      hasChildren: Boolean = false,
      channelSince: Option[String] = None,
      channelDescription: Option[String] = None,
      channelName: Option[String] = None
  ) {

    def toCypherObj = {
      raw"""
                   | {
                   |  ${("subId", subId).toCyph()}
                   |  ${("avatar", avatar).toCyph()}
                   |  ${("timestamp", timestamp).toCyph()}
                   |  ${("parentId", parentId).toCyph()}
                   |  ${("message", message).toCyph()}
                   |  ${("messageId", messageId).toCyph()}
                   |  ${("isRootChannel", isRootChannel).toCyph()}
                   |  ${("hasChildren", hasChildren).toCyph()}
                   |  ${("channelSince", channelSince).toCyph()}
                   |  ${("channelDescription", channelDescription).toCyph()}
                   |  ${("channelName", channelName).toCyph(true)}
                   | }
                   |""".stripMargin
    }
  }

  case class ChildMessageRequest(
      parentId: String,
      subId: String,
      avatar: Option[String],
      message: String
  ) {
    def toMessage = {
      Message(
        message = message,
        parentId = Some(parentId),
        subId = subId,
        avatar = avatar,
        timestamp = currentTime,
        messageId = UUID.randomUUID().toString
      )
    }
  }

  case class ChildChannelRequest(
      parentId: String,
      channelDescription: Option[String],
      avatar: Option[String],
      channelName: Option[String],
      message: String,
      subId: String
  ) {
    def toMessage = {
      val creationTimestamp = currentTime
      Message(
        messageId = UUID.randomUUID().toString,
        subId = subId,
        parentId = Some(parentId),
        channelDescription = channelDescription,
        channelName = channelName,
        avatar = avatar,
        hasChildren = true,
        message = message,
        timestamp = creationTimestamp,
        isRootChannel = false,
        channelSince = Some(creationTimestamp)
      )
    }
  }

  case class RootChannelRequest(
      channelName: Option[String],
      channelDescription: Option[String],
      subId: String,
      avatar: Option[String],
      message: String
  ) {
    def toMessage = {
      val creationTimestamp = currentTime
      Message(
        channelName = channelName,
        channelDescription = channelDescription,
        message = message,
        subId = subId,
        avatar = avatar,
        isRootChannel = true,
        hasChildren = true,
        channelSince = Some(creationTimestamp),
        timestamp = creationTimestamp,
        messageId = UUID.randomUUID().toString,
        parentId = None
      )
    }
  }

}
