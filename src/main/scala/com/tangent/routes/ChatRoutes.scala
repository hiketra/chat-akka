package com.tangent.routes

import akka.actor.ActorSystem
import com.tangent.util.CirceSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.tangent.chat.{ChatService, ChildChannelRequest, ChildMessageRequest, Message, RootChannelRequest}
import com.tangent.exceptions.FailureToRetrieveFileException
import com.tangent.model.ErrorResponse
import com.tangent.model.validationApi.ValidationRequest

class ChatRoutes (chatService: ChatService)(implicit val system: ActorSystem) extends CirceSupport{

  val routes = pathPrefix("message") {
    path("root-channel") {
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
    path("child-message") {
      post {
        entity(as[ChildMessageRequest]) { request => {
          complete(chatService.createChildMessage(request))
        }}
      }
    } ~ path("child-channel") {
      post {
        entity(as[ChildChannelRequest]) { request => {
          complete(chatService.createNewChildChannel(request))
        }
        }
      }
    } ~ path("child-messages" / Segment) { channelId =>
        get {
          complete(chatService.retrieveChildren(channelId))
        }
      }
    }
}
