package com.auth0

import akka.http.scaladsl.model.{ContentTypes, HttpMethods, HttpRequest, StatusCodes}
import com.auth0.model.validationApi.ValidationRequest
import com.tangent.model.validationApi.{ValidationRequest, ValidationResponse}
import io.circe.syntax._

class ValidationRoutesSpec extends TestUtils {

  treeUpdater.populateTree //mimicking service bootstrap

  val validationRoutes = new ValidatorRoutes(treeUpdater).validatorRoutes

  "ValidationRoutes" should {
    "return a 200 response with a response indicating the IP is blacklisted when sent a blacklisted IP" in {
      val ip = "41.78.92.0/22"

      val request = HttpRequest(HttpMethods.POST, "/validate").withEntity(ContentTypes.`application/json`,
        ValidationRequest(ip, Some("core-auth")).asJson.toString())

      request ~> validationRoutes ~> check {
        status should ===(StatusCodes.OK)

        val response = responseAs[ValidationResponse]

        response.blacklistStatus shouldBe true
        response.suppliedIp shouldBe ip
        response.blacklistFile shouldBe Some("ExampleBlacklist.txt")
      }
    }

    "return a 200 response with a response indicating the IP is not blacklisted when sent an IP not in the blacklist" in {
      val ip = "127.0.0.1"

      val request = HttpRequest(HttpMethods.POST, "/validate").withEntity(ContentTypes.`application/json`,
        ValidationRequest(ip, Some("core-auth")).asJson.toString())

      request ~> validationRoutes ~> check {
        status should ===(StatusCodes.OK)

        val response = responseAs[ValidationResponse]

        response.blacklistStatus shouldBe false
        response.suppliedIp shouldBe ip
        response.blacklistFile shouldBe None
      }
    }
  }

}
