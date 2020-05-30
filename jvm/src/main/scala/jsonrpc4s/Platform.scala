package jsonrpc4s

import java.io.OutputStream
import java.nio.channels.{Channels, WritableByteChannel}

import monix.execution.Ack
import monix.reactive.Observer
import scribe.LoggerSupport

import scala.collection.concurrent.TrieMap

object Platform {
  // On the JVM, TrieMaps are a thread-safe and mutable Map implementation
  type ThreadSafeMutableMap[K, V] = TrieMap[K, V]
  def threadSafeMutableMapEmpty[K, V]: ThreadSafeMutableMap[K, V] = TrieMap.empty

  def messagesToOutput(
      out: Either[OutputStream, WritableByteChannel],
      logger: LoggerSupport
  ): Observer.Sync[Message] = {
    new Observer.Sync[Message] {
      private[this] val lock = new Object()
      private[this] var isClosed: Boolean = false
      private[this] val (channel, underlying) = out match {
        case Left(out) => Channels.newChannel(out) -> Some(out)
        case Right(channel) => channel -> None
      }

      private[this] val writer = new LowLevelChannelMessageWriter(channel, logger)
      override def onNext(elem: Message): Ack = lock.synchronized {
        if (isClosed) Ack.Stop
        else {
          try {
            writer.write(elem) match {
              case Ack.Continue => Ack.Continue
              case Ack.Stop => Ack.Stop
              case ack => Ack.Continue
            }
          } catch {
            case err: java.io.IOException =>
              logger.trace(s"Found error when writing ${elem}, closing channel!", err)
              isClosed = true
              Ack.Stop
          }
        }
      }

      override def onError(err: Throwable): Unit = {
        logger.trace("Caught error, stopped writing JSON-RPC messages to output stream!", err)
        onComplete()
      }

      override def onComplete(): Unit = {
        lock.synchronized {
          channel.close()
          underlying.foreach(_.close())
          isClosed = true
        }
      }
    }
  }
}
