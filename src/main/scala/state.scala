package test.background

import cats.effect.*

trait State[T]:
  def start(t: T => IO[Unit]): IO[Unit]
  def stop: IO[Unit]
  def set(t: T): IO[Unit]

object State:
  private case class Internal[T](
      config: T,
      interrupt: IO[Unit]
  )

  def ref[T](init: T): IO[State[T]] = IO
    .ref(Internal(init, IO.println("stopping, something")))
    .map { r =>
      new State[T]:
        def start(work: T => IO[Unit]) = r.get.flatMap {
          case Internal(conf, _) =>
            val action = work(conf)
            action.start.flatMap { fib =>
              r.set(Internal(conf, fib.cancel))
            }
        }

        def stop      = r.get.flatMap(_.interrupt)
        def set(t: T) = r.update(_.copy(config = t))
    }
end State
