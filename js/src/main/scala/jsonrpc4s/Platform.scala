package jsonrpc4s

import java.io.OutputStream
import java.nio.channels.WritableByteChannel

import monix.reactive.Observer
import scribe.LoggerSupport

import scala.collection.mutable

object Platform {
  // A regular Map is thread-safe in JS since JS is single-threaded
  type ThreadSafeMutableMap[K, V] = mutable.Map[K, V]
  def threadSafeMutableMapEmpty[K, V]: ThreadSafeMutableMap[K, V] = mutable.Map.empty

  def messagesToOutput(
      out: Either[OutputStream, WritableByteChannel],
      logger: LoggerSupport
  ): Observer.Sync[Message] = {
    throw new UnsupportedOperationException("This operation is not supported in Scala.js")
  }
}
