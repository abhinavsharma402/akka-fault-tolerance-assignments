package com.knoldus.modules

import java.io.File

case class LogStatus(file: String, error: Int, warnings: Int, info: Int)
case class Path(path:File)
trait FileHandlings {
  def getLogs(file: File): LogStatus
  def readFile(file: File): (List[String])
  def readWarnings(filesContent: List[String]): Int
  def readError(filesContent: List[String]): Int
  def readInfo(filesContent: List[String]): Int

}
