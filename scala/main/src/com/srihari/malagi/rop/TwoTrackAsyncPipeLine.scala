package com.srihari.malagi.rop

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object TwoTrackAsyncPipeLine {

  implicit class FutureBlockHandler[X](input: Future[X]) {

    def mapTo[Y](f: X=>Try[Y])(implicit ec: ExecutionContext): Future[Y] = {
      input.flatMap(x => Future.fromTry(f(x)))
    }

    def mapWithTeeTo[Y](f: X=>Unit)(implicit ec: ExecutionContext): Future[X] = {
      input.map(f).flatMap(_ => input)
    }

    def mapWithTeeTo[Y](f: X=> Try[Unit])(implicit ec: ExecutionContext): Future[X] = {
      input.flatMap(x => Future.fromTry(f(x))).flatMap(_=> input)
    }
  }

}
