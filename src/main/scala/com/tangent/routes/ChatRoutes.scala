package com.tangent.routes

import akka.actor.ActorSystem
import com.tangent.util.CirceSupport
import akka.http.scaladsl.server.Directives._
import com.tangent.chat.{ChatService}
import com.tangent.chat.Model._
import com.tangent.chat.UserService
class ChatRoutes(chatService: ChatService, userService: UserService)(implicit
    val system: ActorSystem
) extends CirceSupport {

  val routes = pathPrefix("chat") {
    pathPrefix("channel") {
      path("root") {
        post {
          entity(as[RootChannelRequest]) { request =>
            {
              complete(chatService.createRootChannel(request))
            }
          }
        } ~
          get {
            complete(chatService.retrieveRootChannels())
          }
      } ~
        path("child") {
          post {
            entity(as[ChildChannelRequest]) { request =>
              {
                complete(chatService.createNewChildChannel(request))
              }
            }
          }
        }
    } ~ pathPrefix("message") {
      post {
        entity(as[ChildMessageRequest]) { request =>
          {
            complete(chatService.createChildMessage(request))
          }
        }
      } ~ path(Segment) { channelId =>
        get {
          complete(chatService.retrieveChildren(channelId))
        }
      }
    }
  } ~ pathPrefix("user") {
    put {
      entity(as[CreateUserRequest]) { request =>
        {
          complete(userService.createUser(request))
        }
      }
    } ~
      post {
        entity(as[CreateUserRequest]) { request =>
          {
            complete(userService.createUser(request))
          }
        }
      } ~ path("associate") {
      entity(as[AssociateChannelRequest]) { request =>
        {
          complete(userService.channelAssociation(request))
        }
      }
    }
  }
}
