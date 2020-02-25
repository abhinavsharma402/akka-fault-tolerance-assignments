package com.knoldus

import akka.routing.RoundRobinPool
import java.io.{File, FileNotFoundException}

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.io.Source

/**
 *
 * @param file     name of file
 * @param error    no. of errors
 * @param warnings no. of warnings
 * @param info     no, of info
 */
case class LogStatus(file: String, error: Int, warnings: Int, info: Int)

/**
 * FileOperation class used to  get data from files
 */
class FileOperation extends Actor with ActorLogging {
  val entries=10
  override val supervisorStrategy =

    OneForOneStrategy(maxNrOfRetries = entries, withinTimeRange = 1.minute) {
      case _: FileNotFoundException => Resume
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }


  override def receive: Receive = {
    case file: File =>
      val result = getLogs(file)
      log.info(result.toString)
    case _ => log.info("invalid")
  }

  /**
   * getLogs methods used to get no. of errors,warnings,info ,file  name
   *
   * @param file take file
   * @return case class of logstatus
   */

  def getLogs(file: File): LogStatus = {
    try {
      val fileContent = readFile(file)

      val errors = readError(fileContent)
      val warnings = readWarnings(fileContent)
      val info = readInfo(fileContent)

      LogStatus(file.getName, errors, warnings, info)
    }
  }

  /**
   * readFile method used to read content of file
   *
   * @param file take file
   * @return content of file
   */

  def readFile(file: File): (List[String]) = {
    if (file.isFile) {
      Source.fromFile(file).getLines().toList
    }
    else {
      throw new Exception("not file")
    }
  }

  /**
   * checkFile method used to check files in folders
   *
   * @param  filesOrFolder take list of files
   * @return list of files
   */


  def checkFile(filesOrFolder: List[File]): List[File] = {
    filesOrFolder.filter(_.isFile)

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

object FileOperation extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("LogFilesActorSystem", config.getConfig("configuration"))
  val confStr = "fixed-dispatcher"
  val actor = 3
  val ref = system.actorOf(RoundRobinPool(actor).props(Props[FileOperation] withDispatcher (confStr)), "FileOperation")

  import system.dispatcher

  val pathObj = new File("src/main/resources/logfiles")

  val filesOrFoldersList = pathObj.listFiles().toList
  implicit val timeout = Timeout(5. seconds)
  // for (filesOrFolders <- filesOrFoldersList){
  //  ref ! filesOrFolders
  system.scheduler.scheduleOnce(5.seconds) {


    filesOrFoldersList.map(filesOrFolders => ref ! filesOrFolders)
  }
}
