package com.srihari.malagi.rop

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Success, Try}

class RailwayOrientedProgrammingTest extends AnyFlatSpec with should.Matchers {

  "A simple input" should "be easily converted into two track output" in {
    val input = "Test Input"

    import TwoTrackOutputConverter.ToTryBlock
    val output = input.toTwoTrackOutput
    val expectedOutput = Success(input)

    output should be (expectedOutput)
  }

  "A two track input" should "be easily piped to multiple blocks to get success results" in {
    import TwoTrackOutputConverter.ToTryBlock
    import TwoTrackPipeLine.TryBlockHandler

    val inputData = "Test Input"
    val input: Try[String] = inputData.toTwoTrackOutput
    val storage: mutable.ListBuffer[String] = mutable.ListBuffer()

    def storeComputation(input: String): Unit = storage += input

    def transformInput(input: String): String = s"Received: [$input]. Expect: Test Output"

    val output = input
      .mapWithTeeTo(storeComputation)
      .map(transformInput)
      .mapWithTeeTo(storeComputation)

    val expectedOutputData = "Received: [Test Input]. Expect: Test Output"
    val expectedOutput = Success(expectedOutputData)

    output should be(expectedOutput)
    storage.size should be(2)
    storage.head should be(inputData)
    storage.last should be(expectedOutputData)
  }

  "A two track input" should "be easily mapped to an async operation" in {
    import TwoTrackOutputConverter.ToTryBlock
    import TwoTrackPipeLine.TryBlockHandler

    import scala.concurrent.ExecutionContext

    implicit val ec: ExecutionContext = ExecutionContext.global

    val inputData = "Test Input"
    val input: Try[String] = inputData.toTwoTrackOutput
    val storage: mutable.ListBuffer[String] = mutable.ListBuffer()

    def storeComputation(input: String): Future[Unit] = Future(storage += input)

    def transformInput(input: String): String = s"Received: [$input]. Expect: Test Output"

    val output = input
      .mapWithTeeTo(storeComputation)
      .map(transformInput)
      .mapWithTeeTo(storeComputation)

    val expectedOutputData = "Received: [Test Input]. Expect: Test Output"
    val expectedOutput = Success(expectedOutputData)

    output should be(expectedOutput)
    storage.size should be(2)
    storage.head should be(inputData)
    storage.last should be(expectedOutputData)
  }
}
