package jsonrpc4s

import scala.collection.concurrent.TrieMap

object CollectionCompat extends MapFactory {
  type ThreadSafeMutableMap[K, V] = TrieMap[K, V]
  def threadSafeMutableMapEmpty[K, V]: ThreadSafeMutableMap[K, V] = TrieMap.empty
}
