package com.knoldus.controller


  import java.io.File

  import akka.actor.{ActorSystem, Props}
  import akka.testkit.{ImplicitSender, TestKit}
  import com.knoldus.modules.Path
  import org.scalatest.wordspec.AnyWordSpecLike
  import org.scalatest.BeforeAndAfterAll

  import scala.concurrent.duration._
  import scala.language.postfixOps

  class FileOperationsSpec extends TestKit(ActorSystem("logFilesActorSystem")) with ImplicitSender with AnyWordSpecLike with BeforeAndAfterAll {
    override def afterAll(): Unit = {
      TestKit.shutdownActorSystem(system)
    }

    "A  actor " should {
      "send back" in{
        within(30 second){
          val testActor = system.actorOf(Props[MainActor])
          val path = new File("./src/main/resources/logfiles")
          testActor ! Path(path)
          val expectedMessage= 1454
          expectMsg(expectedMessage)
        }}
    }


}
