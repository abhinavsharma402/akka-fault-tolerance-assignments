package com.knoldus.controller

import java.io.{File, FileNotFoundException}

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.{ask,pipe}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.knoldus.modules
import com.knoldus.modules.{FileHandlings, LogStatus, Path}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source

/**
 * MainActor used to distribute actors to read different files
 */

class MainActor extends Actor with ActorLogging {
  override val supervisorStrategy: SupervisorStrategy = {
    val entries = 10
    OneForOneStrategy(maxNrOfRetries = entries, withinTimeRange = 1.minute) {
      case _: FileNotFoundException =>
        Stop
      case _: IllegalArgumentException => Stop
      case _: Exception => log.info("directory not found")
        Escalate
    }
  }

  override def receive: Receive = {
    case Path(path) =>

      val filesList = path.listFiles().toList.filter(_.isFile)
      val actors = 4
      val fileOperations = context.actorOf(RoundRobinPool(actors).props(Props[FileOperation]))
      implicit val timeout = Timeout(2.seconds)
      val result = filesList.map(file => {
        fileOperations ? file
        }.mapTo[LogStatus])
      val futureResult = Future.sequence(result)
      val finalAverage=futureResult.map(logStatusList => {
        val totalWarnings = logStatusList.foldLeft(0) { (sum, ele) => sum + ele.warnings }
        val averageWarnings = totalWarnings / logStatusList.length
        log.info(s"average warnings $averageWarnings")
        averageWarnings
      })
      finalAverage.pipeTo(sender())
    case _=>throw new Exception
  }
}

/**
 * FileOperation class used to  get data from files
 */
class FileOperation extends Actor with ActorLogging with FileHandlings {

  override def receive: Receive = {

    case file: File =>
      val result = getLogs(file)
      log.info(s"$result")
      sender() ! result
    case _ => log.info("invalid")
  }

  /**
   * getLogs methods used to get no. of errors,warnings,info ,file  name
   *
   * @param file take file
   * @return case class of logstatus
   */

  def getLogs(file: File): LogStatus = {

      val fileContent = readFile(file)

      val errors = readError(fileContent)
      val warnings = readWarnings(fileContent)
      val info = readInfo(fileContent)

      LogStatus(file.getName, errors, warnings, info)

  }

  /**
   * readFile method used to read content of file
   *
   * @param file take file
   * @return content of file
   */

  def readFile(file: File): (List[String]) = {

    Source.fromFile(file).getLines().toList
  }


  /**
   * readError method used to read errors of file
   *
   * @param filesContent take  content from file
   * @return errors of file
   */
  def readError(filesContent: List[String]): Int = {
    (filesContent.count(_.contains("[ERROR]")))


  }

  /**
   * readWarnings method used to read warnings of file
   *
   * @param filesContent take  content from file
   * @return warnings of file
   */

  def readWarnings(filesContent: List[String]): Int = {

    filesContent.count(_.contains("[WARN]"))

  }

  /**
   * readWarnings method used to read info of file
   *
   * @param filesContent take  content from file
   * @return info of file
   */

  def readInfo(filesContent: List[String]): Int = {
    filesContent.count(_.contains("[INFO]"))

  }


}



