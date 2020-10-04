package com.malagi.srihari.rop

import org.scalatest.concurrent.ScalaFutures

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class TwoTrackPipeLineTest extends UnitSpec with ScalaFutures {

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

    def storeComputation(input: String): Future[String] = Future(storage += input)
      .map(_ => "Successfully stored input")

    val output = input.mapAsyncTo(storeComputation)

    val expectedOutputData = "Successfully stored input"

    whenReady(output) { result =>
      result shouldBe expectedOutputData
      storage.size shouldBe 1
      storage.head shouldBe inputData
    }
  }

  "A two track input" should "be easily mapped to an async tee operation" in {

    import TwoTrackOutputConverter.ToTryBlock
    import TwoTrackPipeLine.TryBlockHandler

    import scala.concurrent.ExecutionContext

    implicit val ec: ExecutionContext = ExecutionContext.global

    val inputData = "Test Input"
    val input: Try[String] = inputData.toTwoTrackOutput
    val storage: mutable.ListBuffer[String] = mutable.ListBuffer()

    def storeComputation(input: String): Future[Unit] = Future(storage += input)

    val output = input.mapAsyncWithTeeTo(storeComputation)

    whenReady(output) { result =>
      result shouldBe inputData
      storage.size shouldBe 1
      storage.head shouldBe inputData
    }
  }

  "A two track input" should "be pass tee exception on failure" in {

    import TwoTrackOutputConverter.ToTryBlock
    import TwoTrackPipeLine.TryBlockHandler

    val inputData = "Test Input"
    val input: Try[String] = inputData.toTwoTrackOutput

    def storeComputation(input: String): Try[Unit] = throw new Exception("Test Exception")

    val output = input.mapWithTeeTo(storeComputation)

    inside(output) {
      case Failure(exception) => exception.getMessage shouldBe "Test exception"
    }
  }
}
