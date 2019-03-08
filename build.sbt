import sbt.url


val versions = new {
  val scala = "2.12.8"
  //  val scala = "2.12.1"
  val finatra = "18.2.0"
  val scalatest = "3.0.5"
  val mockito = "1.10.19"
  val guice = "4.0"
  val play = "2.5.12"
  val scalapact = "2.1.3"
  val junit = "4.12"
  val json4s = "3.5.3"
  val mustache = "0.9.5"
}

lazy val commonSettings = Seq(
  version := "0.0.1",
  organization := "one.xingyi",
  publishMavenStyle := true,
  scalaVersion := versions.scala,
  scalacOptions ++= Seq("-feature"),
  libraryDependencies += "org.mockito" % "mockito-all" % versions.mockito % "test",
  libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest % "test"
)

lazy val publishSettings = commonSettings ++ Seq(
  pomIncludeRepository := { _ => false },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  homepage := Some(url("http://example.com")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/phil-rice/pact-stubber"),
      "scm:git@github.com/phil-rice/pact-stubber.git"
    )
  ),
  developers := List(
    Developer(
      id = "phil",
      name = "Phil Rice",
      email = "phil.rice@iee.org",
      url = url("https://www.linkedin.com/in/phil-rice-53959460")
    )
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  })


lazy val reflectionSettings = publishSettings ++ Seq(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % versions.scala,
  libraryDependencies += "org.scala-lang" % "scala-compiler" % versions.scala
)

lazy val json4sSettings = publishSettings ++ Seq(
  libraryDependencies += "org.json4s" %% "json4s-native" % versions.json4s
)

lazy val mustacheSettings = publishSettings ++ Seq(
  libraryDependencies += "com.github.spullara.mustache.java" % "scala-extensions-2.11" % versions.mustache
)

lazy val scalatestSettings = publishSettings ++ Seq(
  libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest
)

lazy val core = (project in file("modules/core")).
  settings(publishSettings: _*)


lazy val test = (project in file("modules/test")).
  settings(publishSettings: _*).
  dependsOn(core % "test->test;compile->compile").
  dependsOn(json4s % "test->test;compile->compile").
  aggregate(core)



lazy val json4s = (project in file("modules/json4s")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  settings(json4sSettings: _*)

val simplewebframework = (project in file("modules/simplewebframework")).
  dependsOn(core % "test->test;compile->compile").
  aggregate(core).
  settings(mustacheSettings)

val mustache = (project in file("helpers/mustache")).
  dependsOn(core % "test->test;compile->compile").
  aggregate(core).
  settings(mustacheSettings)

val helpersTest = (project in file("helpers/helpersTest")).
  dependsOn(mustache % "test->test;compile->compile").
  dependsOn(json4s % "test->test;compile->compile").
  aggregate(mustache, json4s).
  settings(mustacheSettings)


lazy val model1 = (project in file("demo/model1")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val model2 = (project in file("demo/model2")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val model3 = (project in file("demo/model3")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val backendShared = (project in file("demo/backendShared")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(model1 % "test->test;compile->compile").aggregate(model1).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val backend1 = (project in file("demo/backend1")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(backendShared % "test->test;compile->compile").aggregate(backendShared).
  dependsOn(model1 % "test->test;compile->compile").aggregate(model1).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val backend2 = (project in file("demo/backend2")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(backendShared % "test->test;compile->compile").aggregate(backendShared).
  dependsOn(model2 % "test->test;compile->compile").aggregate(model2).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val backend3 = (project in file("demo/backend3")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(backendShared % "test->test;compile->compile").aggregate(backendShared).
  dependsOn(model3 % "test->test;compile->compile").aggregate(model3).
  dependsOn(json4s % "test->test;compile->compile").
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)

lazy val website = (project in file("demo/website")).
  dependsOn(core % "test->test;compile->compile").aggregate(core).
  dependsOn(json4s % "test->test;compile->compile").
  dependsOn(mustache % "test->test;compile->compile").
  dependsOn(simplewebframework % "test->test;compile->compile").
  dependsOn(model1 % "test->test;compile->compile").aggregate(model1).
  //  dependsOn(tagless % "test->test;compile->compile").aggregate(tagless).
  settings(publishArtifact := false).
  settings(publishSettings: _*)


val restScala = (project in file(".")).
  settings(publishSettings).
  settings(publishArtifact := false).
  aggregate(
    core, //
    backend1,
    backend2,
    backend3,
    website,
    json4s, //
    simplewebframework,
    helpersTest,
    test
  )
