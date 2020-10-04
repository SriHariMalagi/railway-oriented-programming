package com.malagi.srihari.rop

import scala.util.Success

class TwoTrackOutputConverterTest extends UnitSpec {

  "A simple input" should "be easily converted into two track output" in {
    val input = "Test Input"

    import TwoTrackOutputConverter.ToTryBlock
    val output = input.toTwoTrackOutput
    val expectedOutput = Success(input)

    output should be (expectedOutput)
  }
}
