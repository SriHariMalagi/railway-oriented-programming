package com.malagi.srihari.rop

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * This wraps the functionality to process any functions having two track output,
 * when input is of `scala.concurrent.Future` type.
 */
object TwoTrackAsyncPipeLine {

  /**
   * This class is responsible for providing enriched functionality to `scala.concurrent.Future` types,
   * so that we will be able to process different functions; and still maintain two track outputs
   * @param input any input of type: Future[X]
   * @tparam X class-type
   */
  implicit class FutureBlockHandler[X](input: Future[X]) {

    /**
     * Process a function providing a Two Track Sync output (`scala.util.Try`)
     * @param f function which provides a Two Track Sync output (`scala.util.Try`)
     * @param ec The execution context
     * @tparam Y resulting class-type
     * @return Two Track Async Output (`scala.concurrent.Future`) of resulting (Y) class-type.
     */
    def mapTo[Y](f: X=>Try[Y])(implicit ec: ExecutionContext): Future[Y] = {
      input.flatMap(x => Future.fromTry(f(x)))
    }

    /**
     * Process a function which doesn't provide any output, and redirect the input result to next block
     * @param f function which doesn't provide any output
     * @param ec The execution context
     * @return Two Track Async Output (`scala.concurrent.Future`) of input (X) class-type.
     */
    def mapWithTeeTo(f: X=>Unit)(implicit ec: ExecutionContext): Future[X] = {
      input.map(f).flatMap(_ => input)
    }

    /**
     * Process a function providing Two Track Sync Output (`scala.util.Try`) with `Unit` output on success,
     * and therefore redirect the input itself in case of successful execution
     * @param f function which provides a Two Track Sync Output, but `Unit` if successful
     * @param ec The execution context
     * @return Two Track Async Output (`scala.concurrent.Future`) of input (X) class-type.
     */
    def flatMapWithTeeTo(f: X=> Try[Unit])(implicit ec: ExecutionContext): Future[X] = {
      input.flatMap(x => Future.fromTry(f(x))).flatMap(_=> input)
    }
  }

}
