package jsonrpc4s

trait MapFactory {
  type ThreadSafeMutableMap[K, V] <: AnyRef
  def threadSafeMutableMapEmpty[K, V]: ThreadSafeMutableMap[K, V]
}
