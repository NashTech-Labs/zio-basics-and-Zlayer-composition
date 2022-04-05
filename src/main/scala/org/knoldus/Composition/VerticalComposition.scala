package org.knoldus.Composition

import org.knoldus.Basics.ZLayerbasics.{User,UserEmailer, UserEmailerEnv}
import org.knoldus.Composition.HorizontalComposition.{UserDb, UserDbEnv, userBackendLayer}
import zio.{ExitCode, Has, Task, ZIO, ZLayer}
object VerticalComposition extends zio.App {

  // type alias
  type UserSubscriptionEnv = Has[UserSubscription.Service]

  object UserSubscription {
    // service definition as a class
    class Service(notifier: UserEmailer.Service, userModel: UserDb.Service) {
      def subscribe(u: User): Task[User] = {
        for {
          _ <- userModel.insert(u)
          _ <- notifier.notify(u, s"Welcome, ${u.name}! Here are zio concepts")
        } yield u
      }
    }
  // layer with service implementation via dependency injection
  val live: ZLayer[UserEmailerEnv with UserDbEnv, Nothing, UserSubscriptionEnv] =
    ZLayer.fromServices[UserEmailer.Service, UserDb.Service, UserSubscription.Service] { (emailer, db) =>
      new Service(emailer, db)
    }

  // accessor
  def subscribe(u: User): ZIO[UserSubscriptionEnv, Throwable, User] = ZIO.accessM(_.get.subscribe(u))
}
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val userRegistrationLayer = userBackendLayer >>> UserSubscription.live

    UserSubscription.subscribe(User("Meenakshi", "meenakshi@gmail.com"))
      .provideLayer(userRegistrationLayer)
      .catchAll(t => ZIO.succeed(t.printStackTrace()).map(_ => ExitCode.failure))
      .map { u =>
        println(s"Registered user: $u")
        ExitCode.success
      }
  }
}
