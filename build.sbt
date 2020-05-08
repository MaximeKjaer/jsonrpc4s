inThisBuild(
  List(
    organization := "me.vican.jorge",
    homepage := Some(url("https://github.com/jvican/json4pc4")),
    licenses := Seq(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/jvican/jsonrpc4s"),
        "scm:git:git@github.com:jvican/jsonrpc4s.git"
      )
    ),
    developers := List(
      Developer(
        "jvican",
        "Jorge Vicente Cantero",
        "jorgevc@fastmail.es",
        url("https://jvican.github.io/")
      )
    )
  )
)

lazy val jsonrpc4s = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(
    name := "jsonrpc4s",
    scalaVersion := "2.13.1",
    scalacOptions ++= List(
      "-Yrangepos",
      "-deprecation",
      "-Xlint"
    ),
    bloopExportJarClassifiers := Some(Set("sources")),
    releaseEarlyWith := SonatypePublisher,
    publishTo := sonatypePublishToBundle.value,
    libraryDependencies ++= List(
      "io.monix" %%% "monix" % "3.2.1",
      "com.outr" %%% "scribe" % "2.7.12",
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % "2.2.3",
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.2.3" % Provided,
      "io.monix" %%% "minitest" % "2.8.2" % Test,
      "com.lihaoyi" %%% "pprint" % "0.5.9" % Test
    ),
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )

lazy val jsonrpc4sJVM = jsonrpc4s.jvm
lazy val jsonrpc4sJS = jsonrpc4s.js
