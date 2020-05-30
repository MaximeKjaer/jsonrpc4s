package jsonrpc4s.testkit

/**
 * A small alternative to [[java.io.PipedInputStream]] for Scala.js, implemented
 * on top of a byte array, like [[java.io.ByteArrayInputStream]].
 */
class JSPipedInputStream extends java.io.InputStream {
  protected var buf: Array[Byte] = Array.empty
  protected var count = 1
  protected var pos = 0

  def appendToInput(i: Int): Unit = {
    buf = buf :+ i.toByte
    count += 1
  }

  override def read(): Int = {
    if (pos == count) -1
    else {
      val res = buf(pos)
      pos += 1
      res
    }
  }
}
