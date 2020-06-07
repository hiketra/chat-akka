package com.auth0

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.model.{ContentTypes, HttpMethods, HttpRequest}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tangent.tree.TreeUpdater
import com.tangent.util.CirceSupport
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures

import scala.io.Source

trait TestUtils extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with CirceSupport  {

  lazy val testKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[Nothing] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.toClassic

  val treeUpdater = new TreeUpdater(List("ExampleBlacklist.txt")) {
    override def retrieveAndParseFile(file: String): List[String] = {
      Source.fromResource(file).getLines().toList //makes the unit tests fully-closed/deterministic
    }
  }

  def postRequest(uri: String, requestBody: String): HttpRequest = HttpRequest(HttpMethods.POST, uri).withEntity(
    ContentTypes.`application/json`, requestBody)


}
