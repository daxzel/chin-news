
name := "chin-news"

version := "1.0"

scalaVersion := "2.11.7"


libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.6"
libraryDependencies += "io.argonaut" %% "argonaut" % "6.0.4"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.0"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.10.0",
  "org.http4s" %% "http4s-dsl"          % "0.10.0",
  "org.http4s" %% "http4s-argonaut"     % "0.10.0",
  "org.http4s" %% "http4s-jetty"        % "0.10.0"
)

assemblyMergeStrategy in assembly := {
  case PathList("application.conf") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

val packageAllTask = TaskKey[Unit]("packageAll")

packageAllTask := {
  val assembled = assembly.toTask.value

  val targetPath = baseDirectory.value.getAbsolutePath + "/target/scala-2.11/"
  val packagedDir = new File (targetPath + "/packaged")
  packagedDir.mkdir()
  IO.copyFile(new File(baseDirectory.value.getAbsolutePath.concat("/src/main/resources/application.conf")),
    new File(packagedDir.getAbsoluteFile + "/application.conf"))
  IO.copyFile(assembled, new File(packagedDir.getAbsoluteFile + "/" + assembled.name))
  println("Packaged dir is created")
}

