package com.srihari.malagi.rop

import com.srihari.malagi.rop.exception.TeeException

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object TwoTrackPipeLine {

  implicit class TryBlockHandler[X](input: Try[X]) {

    private def toFuture(implicit ec: ExecutionContext): Future[X] = Future.fromTry(input)

    def mapAsyncTo[Y](f: X=>Future[Y])(implicit ec: ExecutionContext): Future[Y] = {
      toFuture.flatMap(x => f(x))
    }

    def mapWithTeeTo(f: X=>Unit): Try[X] = input.map(f) match {
      case Success(_) => input
      case Failure(e) => Failure(TeeException(e))
    }

    def mapAsyncWithTeeTo(f: X=>Future[Unit])(implicit ec: ExecutionContext): Future[X] =
      input.mapAsyncTo(f).flatMap(_ => toFuture)
  }

}
