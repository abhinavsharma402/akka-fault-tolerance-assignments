package com.knoldus.view

import java.io.File

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.knoldus.controller.MainActor
import com.knoldus.modules.Path
import com.typesafe.config.ConfigFactory

object Driver extends  App {
  val config = ConfigFactory.load()
  val system = ActorSystem("LogFilesActorSystem", config.getConfig("configuration"))
  val confStr = "fixed-dispatcher"
  val actor = 3

  /*def strategy:SupervisorStrategy={
    val entries=10
    OneForOneStrategy (maxNrOfRetries = entries, withinTimeRange = 1.minute) {
    case _: FileNotFoundException => Resume
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }*/
  //}
  val ref = system.actorOf((Props[MainActor] withDispatcher (confStr)), "mainActor")

  import system.dispatcher

  val pathObj = new File("src/main/resources/logfiles")

  //val filesOrFoldersList = pathObj.listFiles().toList
  implicit val timeout = Timeout(5. seconds)
  // for (filesOrFolders <- filesOrFoldersList){
  //  ref ! filesOrFolders

  system.scheduler.scheduleAtFixedRate(0.seconds,6.seconds,ref,Path(pathObj))

}

