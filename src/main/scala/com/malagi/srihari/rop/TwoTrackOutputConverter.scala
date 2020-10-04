package com.malagi.srihari.rop

import scala.util.Try

/**
 * This wraps the functionality to convert any standard input types into two track output.
 * Essentially wraps the input into a try block.
 */
object TwoTrackOutputConverter {

  /**
   * This class is responsible for providing enriched functionality to any input types,
   * so that they can be converted into two track ouputs
   * @param x any input of type: X
   * @tparam X class-type
   */
  implicit class ToTryBlock[X](x: X) {

    /**
     * Wraps a given input into a try block, so that railway oriented programming (rop),
     * pattern can be applied.
     * @return `scala.util.Try[X]`
     */
    def toTwoTrackOutput: Try[X] = Try(x)
  }
}
