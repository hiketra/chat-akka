package com.auth0

import akka.http.scaladsl.model.StatusCodes
import com.tangent.exceptions.FailureToRetrieveFileException
import com.tangent.model.managementApi.PopulationResponse
import com.tangent.tree.TreeUpdater

import scala.io.{BufferedSource, Source}

class ManagementRoutesSpec extends TestUtils {

  val managementRoutes = new ManagementRoutes(treeUpdater).managementRoutes

  "ManagementRoutes" should {
    "be able to populate a tree" in {
      treeUpdater.getCurrentTree.size shouldBe 0
      val request = postRequest("/management/force-update", "{}")
      request ~> managementRoutes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[PopulationResponse]
        val blacklistSize = Source.fromResource("ExampleBlacklist.txt").getLines().toList.size
        response.numberOfEntries shouldBe blacklistSize
        treeUpdater.getCurrentTree.size shouldBe blacklistSize
      }
    }

    "return a 503 error response if GitHub is unreachable" in {
      val treeUpdaterWithNoConnectivity = new TreeUpdater(List("ExampleBlacklist.txt")) {
        override def retrieveFile(file: String): Option[BufferedSource] = {
          None
        }
      }

      val managementRoutes2 = new ManagementRoutes(treeUpdaterWithNoConnectivity).managementRoutes

      val request = postRequest("/management/force-update", "{}")
      request ~> managementRoutes2 ~> check {
        status shouldBe StatusCodes.ServiceUnavailable
      }
    }
  }

}
