package jsonrpc4s.testkit

import jsonrpc4s.InputOutput

object TestPlatform {
  def aliceAndBobIO(): (InputOutput, InputOutput) = {
    val inAlice = new JSPipedInputStream()
    val inBob = new JSPipedInputStream()
    val outAlice = new JSPipedOutputStream(inBob)
    val outBob = new JSPipedOutputStream(inAlice)
    val aliceIO = new InputOutput(inAlice, outAlice)
    val bobIO = new InputOutput(inBob, outBob)
    (aliceIO, bobIO)
  }
}
