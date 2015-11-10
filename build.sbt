name := "chin-news"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.6"
libraryDependencies += "io.argonaut" %% "argonaut" % "6.0.4"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.10.0",
  "org.http4s" %% "http4s-dsl"          % "0.10.0",
  "org.http4s" %% "http4s-argonaut"     % "0.10.0",
  "org.http4s" %% "http4s-jetty"        % "0.10.0"

)