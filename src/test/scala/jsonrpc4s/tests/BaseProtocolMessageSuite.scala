package jsonrpc4s.tests

import java.nio.ByteBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import monix.eval.Task
import monix.execution.schedulers.TestScheduler
import monix.reactive.Observable
import scribe.Logger
import minitest.SimpleTestSuite
import jsonrpc4s.Request
import jsonrpc4s.RawJson
import jsonrpc4s.BaseMessageCodecs.stringCodec
import jsonrpc4s.RequestId
import jsonrpc4s.BaseProtocolMessage
import jsonrpc4s.MessageWriter

object BaseProtocolMessageSuite extends SimpleTestSuite {
  private val request = Request("method", Some(RawJson.toJson("params")), RequestId(1))
  private val message = BaseProtocolMessage.fromMsg(request)
  private val byteArray = MessageWriter.write(message).array()
  private val byteArrayDouble = byteArray ++ byteArray
  private def bytes = ByteBuffer.wrap(byteArray)

  test("toString") {
    assertEquals(
      message.toString.replaceAll("\r\n", "\n"),
      """|Content-Length: 60
         |
         |{"method":"method","params":"params","id":1,"jsonrpc":"2.0"}""".stripMargin
        .replaceAll("\r\n", "\n")
    )
  }

  private val s = TestScheduler()
  def await[T](f: Task[T]): T = {
    val a = f.runToFuture(s)
    while (s.tickOne()) ()
    Await.result(a, Duration("5s"))
  }

  // Emulates a sequence of chunks and returns the parsed protocol messages.
  def parse(buffers: List[ByteBuffer]): List[BaseProtocolMessage] = {
    val buf = List.newBuilder[BaseProtocolMessage]
    val t = BaseProtocolMessage
      .fromByteBuffers(Observable(buffers: _*), Logger.root)
      // NOTE(olafur) toListL will not work as expected here, it will send onComplete
      // for the first onNext, even when a single ByteBuffer can contain multiple
      // messages
      .foreachL(buf += _)
    await(t)
    buf.result()
  }

  0.to(4).foreach { i =>
    test(s"parse-$i") {
      val (buffers, messages) = 1.to(i).toList.map(_ => bytes -> message).unzip
      assertEquals(parse(buffers), messages)
    }
  }

  def checkTwoMessages(name: String, buffers: List[ByteBuffer]): Unit = {
    test(name) {
      val obtained = parse(buffers)
      val expected = List(message, message)
      assertEquals(obtained, expected)
    }
  }

  def array: ByteBuffer = ByteBuffer.wrap(byteArray)
  def take(n: Int): ByteBuffer = ByteBuffer.wrap(byteArray.take(n))
  def drop(n: Int): ByteBuffer = ByteBuffer.wrap(byteArray.drop(n))

  checkTwoMessages(
    "combined",
    ByteBuffer.wrap(byteArrayDouble) ::
      Nil
  )

  checkTwoMessages(
    "chunked",
    take(10) ::
      drop(10) ::
      array ::
      Nil
  )

  checkTwoMessages(
    "chunked2",
    take(10) ::
      ByteBuffer.wrap(drop(10).array() ++ take(10).array()) ::
      drop(10) ::
      Nil
  )

  test("chunked at every possible offset") {
    0.to(byteArrayDouble.length).foreach { i =>
      // Split the message at offset `i` and emit two chunks
      val buffers =
        ByteBuffer.wrap(byteArrayDouble.take(i)) ::
          ByteBuffer.wrap(byteArrayDouble.drop(i)) ::
          Nil
      val obtained = parse(buffers)
      val expected = List(message, message)
      assertEquals(obtained, expected)
    }
  }
}
