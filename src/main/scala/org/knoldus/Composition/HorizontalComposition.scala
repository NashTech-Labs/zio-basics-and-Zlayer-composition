package org.knoldus.Composition

import org.knoldus.Basics.ZLayerbasics.{User, UserEmailer, UserEmailerEnv}
import zio.{Has, Task, ZIO, ZLayer}

object HorizontalComposition extends zio.App {
  type UserDbEnv = Has[UserDb.Service]

  object UserDb {
    // service definition
    trait Service {
      def insert(user: User): Task[Unit]
    }

    // layer - service implementation
    val live: ZLayer[Any, Nothing, UserDbEnv] = ZLayer.succeed {
      new Service {
        override def insert(user: User): Task[Unit] = Task {
          // can replace this with an actual DB SQL string
          println(s"[Database] insert into public.user values ('${user.name}')")
        }
      }
    }

    // accessor
    def insert(u: User): ZIO[UserDbEnv, Throwable, Unit] = ZIO.accessM(_.get.insert(u))
  }

  /**
   * Horizontal Composition with UserDb and UserEmailer service
   */
  val userBackendLayer: ZLayer[Any, Nothing, UserDbEnv with UserEmailerEnv] =
    UserDb.live ++ UserEmailer.live

  def run(args: List[String]) = {
    UserDb.insert(User("Meenakshi", "mee123@gmail.com"))
      .provideLayer(userBackendLayer)
      .exitCode
  }
}

