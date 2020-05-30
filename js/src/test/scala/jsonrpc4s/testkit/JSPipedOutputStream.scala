package jsonrpc4s.testkit

class JSPipedOutputStream(input: JSPipedInputStream) extends java.io.OutputStream {
  override def write(i: Int): Unit = input.appendToInput(i.toByte)
}
