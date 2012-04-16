package project

import sbt._
import Keys._
import sbt.Package._
import java.util.jar.Attributes.Name._
import com.github.siasia.WebPlugin._
import com.github.siasia.PluginKeys._
import com.typesafe.startscript.StartScriptPlugin

object LiftProject extends Build {

  lazy val buildSettings = Seq(
    organization := "com.owlunit",
    version      := "0.1-SNAPSHOT",
    scalaVersion := "2.9.1"
  )

  lazy val root = Project(
    id = "ii",
    base = file("."),
    settings = defaultSettings ++ webSettings ++ StartScriptPlugin.startScriptForClassesSettings ++ Seq(
      libraryDependencies ++= Dependencies.lift ++ Dependencies.webPlugin,
      scanDirectories in Compile := Nil,
      mainClass in Compile := Some("JettyLauncher")
    )
  )

  /////////////////////
  // Settings
  /////////////////////

  seq(webSettings: _*)
  seq(StartScriptPlugin.startScriptForClassesSettings: _*)

  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.owlunit",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.9.1",
    resolvers ++= defaultResolvers,
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions ++= Seq("-Xlint:unchecked"),
    publishTo := Some(Resolver.file("file",  new File( "/Users/anton/Dev/Owls/repo/owlunit.github.com/repo/ivy/" )) ),
    StartScriptPlugin.stage in Compile := Unit
  )


  /////////////////////
  // Dependencies
  /////////////////////
  
  val defaultResolvers = Seq(
    ScalaToolsSnapshots,
    "Liftmodules repo" at "https://repository-liftmodules.forge.cloudbees.com/release"
  )
  
  object Dependencies {

    val lift = Seq(
      Dependency.liftMapper,
      Dependency.liftWebKit,
      Dependency.liftWizard,
      Dependency.liftWidgets,
      Dependency.liftMongo,
      Dependency.auth
    )

    val webPlugin = Seq(
      Dependency.jettyWebapp % "container",
      Dependency.jettyWebapp % "compile->default",
      Dependency.jettyServer % "compile->default",
      Dependency.servlet     % "compile->default"
    )

  }

  object Dependency {

    // Versions

    object V {
      val Lift      = "2.4"
      val Jetty     = "7.3.1.v20110307"
    }

    // Compile

    val dispatch    = "net.databinder"            %% "dispatch-http"       % "0.8.8"

    val liftWebKit  = "net.liftweb"               %% "lift-webkit"         % V.Lift    % "compile->default"
    val liftMapper  = "net.liftweb"               %% "lift-mapper"         % V.Lift    % "compile->default"
    val liftWizard  = "net.liftweb"               %% "lift-wizard"         % V.Lift    % "compile->default"
    val liftWidgets = "net.liftweb"               %% "lift-widgets"        % V.Lift    % "compile->default"
    val liftJson    = "net.liftweb"               %% "lift-json"           % V.Lift    % "compile->default"
    val liftMongo   = "net.liftweb"               %% "lift-mongodb-record" % V.Lift    % "compile->default"
    val rogue       = "com.foursquare"            %% "rogue"               % "1.1.6"   intransitive()
    val auth        = "net.liftmodules"           %% "mongoauth"           % (V.Lift + "-0.3")

    val jettyWebapp = "org.eclipse.jetty"         %  "jetty-webapp"        % V.Jetty
    val jettyServer = "org.eclipse.jetty"         %  "jetty-server"        % V.Jetty
    val servlet     = "org.eclipse.jetty"         %  "jetty-servlet"       % V.Jetty
    val logging     = "com.codahale"              %% "logula"              % "2.1.3"

  }

}

