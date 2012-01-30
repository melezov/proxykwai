import sbt._
import Keys._

//  ===========================================================================

object BuildSettings {
  val defaultSettings =
    Defaults.defaultSettings ++
    Resolvers.settings ++
    Publishing.settings ++ Seq(
      organization := "hr.element.proxykwai",

      crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0"),
      scalaVersion <<= (crossScalaVersions) { versions => versions.head },
      
      scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "UTF-8", "-optimise"), // , "-Yrepl-sync"
      javacOptions := Seq("-deprecation", "-encoding", "UTF-8", "-source", "1.5", "-target", "1.5"),
      
      unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)( _ :: Nil),
      unmanagedSourceDirectories in Test    <<= (scalaSource in Test   )( _ :: Nil)
    )   


  val bsProxyKwai = 
    defaultSettings ++ Seq(
      name         := "ProxyKwai",
      version      := "0.0.1-SNAPSHOT",

      unmanagedSourceDirectories in Compile <<= (javaSource in Compile)( _ :: Nil),      
      autoScalaLibrary := false,
      crossPaths := false
  )
}

//  ---------------------------------------------------------------------------

object Dependencies {
  val jasmin = "net.sf.jasmin" % "jasmin" % "2.4"

  val scalaTest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
  
  val depsProxyKwai = 
    libraryDependencies := Seq(
      jasmin,
      scalaTest
    )
}

//  ---------------------------------------------------------------------------

object ProxyKwaiBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val proxyKwai = Project(
    "ProxyKwai",
    file("proxykwai"),
    settings = bsProxyKwai :+ depsProxyKwai
  )
}


//  ===========================================================================

object Repositories {
  val ElementNexus     = "Element Nexus"     at "http://maven.element.hr/nexus/content/groups/public/"
  val ElementReleases  = "Element Releases"  at "http://maven.element.hr/nexus/content/repositories/releases/"
  val ElementSnapshots = "Element Snapshots" at "http://maven.element.hr/nexus/content/repositories/snapshots/"
} 

//  ---------------------------------------------------------------------------

object Resolvers {
  import Repositories._

  val settings = Seq(
    resolvers := Seq(ElementNexus, ElementReleases, ElementSnapshots),
    externalResolvers <<= resolvers map { rs =>
      Resolver.withDefaultResolvers(rs, mavenCentral = false, scalaTools = false)
    }
  )
} 

//  ---------------------------------------------------------------------------

object Publishing {
  import Repositories._

  val settings = Seq(
    publishTo <<= (version) { version => Some(
      if (version.endsWith("SNAPSHOT")) ElementSnapshots else ElementReleases
    )},
    credentials += Credentials(Path.userHome / ".publish" / "element.credentials"),
    publishArtifact in (Compile, packageDoc) := false
  )
}  

//  ===========================================================================
