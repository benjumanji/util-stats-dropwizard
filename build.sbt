seq(bintraySettings:_*)

organization := "io.artfuldodge"

name := "util-stats-dropwizard"

version := "0.2"

description := "A stats reciever backed by a dropwizard metrics registry"

licenses ++= Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.twitter" %% "util-stats" % "6.23.0",
  "io.dropwizard.metrics" % "metrics-core" % "3.1.0"
)

