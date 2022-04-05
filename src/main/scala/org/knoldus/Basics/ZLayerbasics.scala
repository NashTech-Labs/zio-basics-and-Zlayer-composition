package org.knoldus.Basics

  import zio.{Has, Task, ZIO, ZLayer}
  object ZLayerbasics extends zio.App {

    case class User(name:String , email:String)
    type UserEmailerEnv = Has[UserEmailer.Service]
    object UserEmailer {
      trait Service {
        def notify(user: User, message: String): Task[Unit]
      }

      val live: ZLayer[Any, Nothing,UserEmailerEnv] = ZLayer.succeed( new Service {
        override def notify(user: User, message: String): Task[Unit] =
          Task {
            println(s"Sending '$message' to ${user.email}")
          }
      }
      )

      def notify(user: User, message: String): ZIO[UserEmailerEnv, Throwable, Unit] =
        ZIO.accessM(hasService => hasService.get.notify(user, message))
    }

    def run(args: List[String]) = {
      UserEmailer
        .notify(User("Meenakshi","meenakshi.goyal@knoldus.com"),"welcome")
        .provideLayer(UserEmailer.live).exitCode
    }
  }


