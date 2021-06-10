package test
package fibers

import scala.concurrent.duration.*
import cats.syntax.all.*
import cats.effect.*
import scala.util.Random
import scalafx.application.Platform
import test.ui.Config

case class GeneratorConfig(minimum: FiniteDuration, maximum: FiniteDuration)

object FiberProgram:
  type HandleUpdate = (Int, Int, String) => Unit

  def create(
      config: Config,
      handler: HandleUpdate,
      genConfig: GeneratorConfig
  ) =
    def action(
        conf: GeneratorConfig,
        local: IOLocal[Int],
        worker: (Int, Int)
    ): IO[Unit] =
      def randomDelay =
        IO {
          val diff = conf.maximum.toMillis - conf.minimum.toMillis
          Random.nextLong(diff).millis + conf.minimum
        }

      def go: IO[Unit] =
        for
          sleepy <- randomDelay
          _      <- local.update(_ + 1).delayBy(sleepy)
          cur    <- local.get
          _ <- IO(
            Platform.runLater(handler(worker._1, worker._2, cur.toString))
          )
          _ <- go
        yield ()

      go
    end action

    (0 until config.workers).toList.parTraverse { num =>
      val col = num / config.columns
      val row = num % config.columns

      IOLocal(0).flatMap(action(genConfig, _, col -> row))
    }.void
  end create
end FiberProgram
