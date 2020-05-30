package jsonrpc4s.testkit

import java.io.{PipedInputStream, PipedOutputStream}

import jsonrpc4s.InputOutput

object TestPlatform {
  def aliceAndBobIO(): (InputOutput, InputOutput) = {
    val inAlice = new PipedInputStream()
    val inBob = new PipedInputStream()
    val outAlice = new PipedOutputStream(inBob)
    val outBob = new PipedOutputStream(inAlice)
    val aliceIO = new InputOutput(inAlice, outAlice)
    val bobIO = new InputOutput(inBob, outBob)
    (aliceIO, bobIO)
  }
}
