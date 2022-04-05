package org.knoldus.Basics
import zio.ZIO
import zio.console._
object ZioBasics extends zio.App {
  val greetingZio =
    for {
      _    <- putStrLn("Hi! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name, welcome to zio")
    } yield ()

  /**
   * for function
   */
  val zioFun = ZIO.fromFunction((i:Int) => i*i)

  val res = for {
    sq <- zioFun.provide(2)
    _ <- putStr(s"$sq")
  }
  yield ()

  /**
   * for case class
   * @param name
   * @param id
   */
  final case class Employee(name:String, id:Int)

  val emp = for{
    name <- ZIO.access[Employee](_.name)
    id <- ZIO.access[Employee](_.id)
  }yield (s"name : $name , id: $id" )

  val empValue = for{
    str <- emp.provide(Employee("Meenakshi",1472))
    _ <- putStr(s"$str")

  }yield ()

  def run(args: List[String]) =
    greetingZio.exitCode
}


