lazy val akkaHttpVersion = "10.1.11"
lazy val akkaVersion    = "2.6.1"

val circeVersion = "0.11.1"

parallelExecution in Test := false //parallel execution of tests can sometimes

libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "1.7.5"
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.tangent",
      scalaVersion    := "2.13.1"
    )),
    name := "chat",
      libraryDependencies ++= Seq(
        "com.dimafeng" %% "neotypes" % "0.13.3",
        "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",
      "com.dimafeng" %% "neotypes" % "0.13.3",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.0.8",

      //third-party libs
      //json deserialising code
      "io.circe" %% "circe-parser" % "0.12.3",
      "io.circe" %% "circe-generic" % "0.12.3",
      "io.circe" %% "circe-core" % "0.12.3",
    "de.heikoseeberger" %% "akka-http-circe" % "1.29.1",
      //logging utilities
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

      //for scheduling
      "com.typesafe.akka" %% "akka-actor" % akkaVersion

    )
  )
