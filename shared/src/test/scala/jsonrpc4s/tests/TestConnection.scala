package jsonrpc4s.testkit

import monix.execution.Cancelable
import monix.execution.Scheduler
import jsonrpc4s.Connection
import jsonrpc4s.InputOutput
import jsonrpc4s.RpcClient
import jsonrpc4s.Services

/**
 * A bi-directional connection between two running JSON-RPC entities named Alice and Bob.
 *
 * @param alice the running instance for Alice.
 * @param aliceIO the input/output streams for Alice.
 * @param bob the running instance for Bob.
 * @param bobIO the input/output streams for Bob.
 */
final class TestConnection(
    val alice: Connection,
    val aliceIO: InputOutput,
    val bob: Connection,
    val bobIO: InputOutput
) extends Cancelable {
  override def cancel(): Unit =
    Cancelable.cancelAll(alice :: bob :: bobIO :: aliceIO :: Nil)
}

object TestConnection {

  /**
   * Instantiate bi-directional communication between a client and server.
   *
   * Useful for testing purposes.
   *
   * @param clientServices services implemented by the client.
   * @param serverServices services implemented by the server.
   * @param s the scheduler to run all services.
   */
  def apply(
      clientServices: RpcClient => Services,
      serverServices: RpcClient => Services
  )(implicit s: Scheduler): TestConnection = {
    val (aliceIO, bobIO) = TestPlatform.aliceAndBobIO()
    val alice = Connection.simple(aliceIO, "alice")(clientServices)
    val bob = Connection.simple(bobIO, "bob")(serverServices)
    new TestConnection(alice, aliceIO, bob, bobIO)
  }

}
