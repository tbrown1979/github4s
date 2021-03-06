pgpPassphrase := Some(getEnvVar("PGP_PASSPHRASE").getOrElse("").toCharArray)
pgpPublicRing := file(s"$gpgFolder/pubring.gpg")
pgpSecretRing := file(s"$gpgFolder/secring.gpg")

lazy val root = (project in file("."))
  .dependsOn(github4sJVM, github4sJS, scalaz, docs)
  .aggregate(github4sJVM, github4sJS, scalaz, docs)
  .settings(noPublishSettings: _*)

lazy val github4s = (crossProject in file("github4s"))
  .settings(moduleName := "github4s")
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      "token" -> sys.env.getOrElse("GITHUB4S_ACCESS_TOKEN", "")),
    buildInfoPackage := "github4s"
  )
  .crossDepSettings(commonCrossDeps: _*)
  .settings(standardCommonDeps: _*)
  .jvmSettings(jvmDeps: _*)
  .jsSettings(jsDeps: _*)
  .jsSettings(sharedJsSettings: _*)
  .jsSettings(testSettings: _*)

lazy val github4sJVM = github4s.jvm
lazy val github4sJS  = github4s.js

lazy val docs = (project in file("docs"))
  .dependsOn(scalaz)
  .settings(moduleName := "github4s-docs")
  .settings(micrositeSettings: _*)
  .settings(docsDependencies: _*)
  .settings(noPublishSettings: _*)
  .enablePlugins(MicrositesPlugin)

lazy val scalaz = (project in file("scalaz"))
  .settings(moduleName := "github4s-scalaz")
  .settings(scalazDependencies: _*)
  .dependsOn(github4sJVM)
