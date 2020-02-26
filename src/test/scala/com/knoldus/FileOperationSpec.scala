//package com.knoldus
//import java.io.{File, FileNotFoundException}
//
//import scala.concurrent.duration._
//import akka.actor.SupervisorStrategy.{Escalate, Resume, Stop}
//import akka.actor.{ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
//import akka.routing.RoundRobinPool
//import akka.testkit
//import akka.testkit.{ImplicitSender, TestActor, TestActors, TestKit}
//import akka.util.Timeout
//import com.typesafe.config.ConfigFactory
//import org.scalatest.BeforeAndAfterAll
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpecLike
//
//class FileOperationSpec  extends TestKit(ActorSystem("TestKitUsageSpec", ConfigFactory.parseString(TestKitUsageSpec.config)))
//  with ImplicitSender
//  with AnyWordSpecLike
//  with Matchers
//  with BeforeAndAfterAll {
//  val system = ActorSystem("LogFilesActorSystem" /*config.getConfig("configuration")*/)
//  val ref = system.actorOf(TestActors.echoActorProps)
//
//    override def afterAll: Unit = {
//      TestKit.shutdownActorSystem(system)
//    }
//
//    "An  actor" must {
//
//      "send back messages unchanged" in {
//       // val config = ConfigFactory.load()
//
//       // val confStr = "fixed-dispatcher"
//       // val actor = 3
//
//        /*def strategy:SupervisorStrategy={
//          val entries=10
//          OneForOneStrategy (maxNrOfRetries = entries, withinTimeRange = 1.minute) {
//            case _: FileNotFoundException => Resume
//            case _: IllegalArgumentException => Stop
//            case _: Exception => Escalate
//          }
//        }*/
//       // val ref = system.actorOf/*RoundRobinPool(actor,supervisorStrategy = strategy).props*/(TestActors.(new FileOperation)/* withDispatcher (confStr))*/, "FileOperation")
//
//        import system.dispatcher
//
//        val pathObj = new File("src/main/resources/logfiles")
//
//        val filesOrFoldersList = pathObj.listFiles().toList
//
//        // for (filesOrFolders <- filesOrFoldersList){
//        //  ref ! filesOrFolders
//
//      //  val cancellable= filesOrFoldersList.map(filesOrFolders =>system.scheduler.scheduleAtFixedRate(0.seconds,60.seconds,ref,filesOrFolders))
//        filesOrFoldersList.map(filesOrFolders =>ref!filesOrFolders)
//
//        expectMsg("got answer")
//
//      }
//
//    }
//
//}
