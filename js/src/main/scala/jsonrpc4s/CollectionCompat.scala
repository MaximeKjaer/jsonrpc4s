package jsonrpc4s

import scala.collection.mutable

object CollectionCompat extends MapFactory {
  // A regular Map is thread-safe in JS since JS is single-threaded
  type ThreadSafeMutableMap[K, V] = mutable.Map[K, V]
  def threadSafeMutableMapEmpty[K, V]: ThreadSafeMutableMap[K, V] = mutable.Map.empty
}
