seq(bintraySettings:_*)

organization := "io.artfuldodge"

name := "util-stats-dropwizard"

version := "0.3.2"

description := "A stats reciever backed by a dropwizard metrics registry"

licenses ++= Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "com.twitter" %% "util-stats" % "6.25.0",
  "io.dropwizard.metrics" % "metrics-core" % "3.1.2"
)

