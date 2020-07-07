package com.tangent.chat

import com.tangent.chat.Model._
import com.tangent.util.Util.CypherUtils._
import com.tangent.neo4j.Neo4jDriver
import com.tangent.util.CirceSupport
import neotypes.implicits.mappers.results._
import neotypes.implicits.syntax.string._
import scala.concurrent.ExecutionContext.Implicits.global
import com.tangent.chat.Model.CreateUserRequest
import scala.concurrent.Future


class UserService(driver: Neo4jDriver) extends CirceSupport {
  val nDriver = driver.driver

  def createUser(user: CreateUserRequest): Future[User] = {
    s"CREATE (u: User ${user.toUser.toCypherObj}) RETURN u;".query[User].single(nDriver.session)
  }

  def channelAssociation(associateChannelRequest: AssociateChannelRequest): Future[User] = {
    raw"""MATCH (u: User) WHERE u.subId = "${associateChannelRequest.subId}" SET u.subscribedChannels = u.subscribedChannels + ${associateChannelRequest.channelId} RETURN u;""".query[User].single(nDriver.session)
  }

  def retrieveUserOrUpdate(user: CreateUserRequest): Future[User] = {
    try {
      raw"""MATCH (u: User) WHERE u.subId = "${user.subId}" SET u.avatar = "${user.avatar}"""".query[User].single(nDriver.session)
    } catch {
      case e => {
        createUser(user);
      }
    }
  }

}