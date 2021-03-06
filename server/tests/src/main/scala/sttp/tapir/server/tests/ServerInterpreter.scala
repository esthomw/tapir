package sttp.tapir.server.tests

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Resource}
import sttp.tapir.Endpoint
import sttp.tapir.server.{DecodeFailureHandler, ServerEndpoint}
import sttp.tapir.tests.Port

import scala.reflect.ClassTag

trait ServerInterpreter[F[_], +R, ROUTE] {
  implicit lazy val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  def route[I, E, O](e: ServerEndpoint[I, E, O, R, F], decodeFailureHandler: Option[DecodeFailureHandler] = None): ROUTE
  def routeRecoverErrors[I, E <: Throwable, O](e: Endpoint[I, E, O, R], fn: I => F[O])(implicit eClassTag: ClassTag[E]): ROUTE
  def server(routes: NonEmptyList[ROUTE], port: Port): Resource[IO, Unit]
}
