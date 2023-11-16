package util

import cats.Monad
import com.twitter.util.Future

object FutureMonad {
  implicit val twitterFutureMonad: Monad[Future] = new Monad[Future] {
    def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)

    def tailRecM[A, B](a: A)(f: A => Future[Either[A, B]]): Future[B] =
      f(a).flatMap {
        case Right(b) => Future.value(b)
        case Left(nextA) => tailRecM(nextA)(f)
      }

    def pure[A](x: A): Future[A] = Future.value(x)

    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
  }
}
