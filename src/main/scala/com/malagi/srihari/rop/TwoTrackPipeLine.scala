package com.malagi.srihari.rop

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * This wraps the functionality to process any functions having two track output,
 * when input is of `scala.util.Try` type.
 */
object TwoTrackPipeLine {

  /**
   * This class is responsible for providing enriched functionality to `scala.util.Try` types,
   * so that we will be able to process different functions; and still maintain two track outputs
   * @param input any input of type: Try[X]
   * @tparam X class-type
   */
  implicit class TryBlockHandler[X](input: Try[X]) {

    private def toFuture(implicit ec: ExecutionContext): Future[X] = Future.fromTry(input)

    /**
     * Process a function providing a Two Track Async output (`scala.concurrent.Future`)
     * @param f function which provides a Two Track Async output (`scala.concurrent.Future`)
     * @param ec The execution context
     * @tparam Y resulting class-type
     * @return Two Track Async Output (`scala.concurrent.Future`) of resulting (Y) class-type.
     */
    def mapAsyncTo[Y](f: X=>Future[Y])(implicit ec: ExecutionContext): Future[Y] = {
      toFuture.flatMap(x => f(x))
    }

    /**
     * Process a function which doesn't provide any output, and redirect the input result to next block
     * @param f function which doesn't provide any output
     * @return Two Track Sync Output (`scala.util.Try`) of input (X) class-type.
     */
    def mapWithTeeTo(f: X=>Unit): Try[X] = input.map(f).flatMap(_ => input)

    /**
     * Process a function providing Two Track Sync Output (`scala.util.Try`) with `Unit` output on success,
     * and therefore redirect the input itself in case of successful execution
     * @param f function which provides a Two Track Sync Output, but `Unit` if successful
     * @return Two Track Sync Output (`scala.util.Try`) of input (X) class-type.
     */
    def flatMapWithTeeTo(f: X=>Try[Unit]): Try[X] = input.flatMap(f).flatMap(_ => input)

    /**
     * Process a function providing Two Track Async Output (`scala.concurrent.Future`) with `Unit` output on success,
     * and therefore redirect the input itself in case of successful execution
     * @param f function which provides a Two Track Async Output, but `Unit` if successful
     * @param ec The execution context
     * @return Two Track Async Output (`scala.concurrent.Future`) of input (X) class-type.
     */
    def mapAsyncWithTeeTo(f: X=>Future[Unit])(implicit ec: ExecutionContext): Future[X] =
      input.mapAsyncTo(f).flatMap(_ => toFuture)
  }

}
