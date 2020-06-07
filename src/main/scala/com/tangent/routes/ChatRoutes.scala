package com.tangent.routes

import akka.actor.ActorSystem
import com.tangent.util.CirceSupport
import akka.http.scaladsl.server.Directives._
import com.tangent.chat.{ChatService}
import com.tangent.chat.Model._
class ChatRoutes (chatService: ChatService)(implicit val system: ActorSystem) extends CirceSupport{

  val routes = pathPrefix("chat") {
    pathPrefix("channel") {
      path("root") {
      post {
        entity(as[RootChannelRequest]) { request => {
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
        entity(as[ChildChannelRequest]) { request => {
          complete(chatService.createNewChildChannel(request))
        }}
      }
    }
  } ~ pathPrefix("message") {
     post {
        entity(as[ChildMessageRequest]) { request => {
          complete(chatService.createChildMessage(request))
        }}
      } ~ path(Segment) { channelId =>
        get {
          complete(chatService.retrieveChildren(channelId))
        }
      }
  }
}
}
