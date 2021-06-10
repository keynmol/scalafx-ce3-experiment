package test.ui

import test.background.*
import test.fibers.*

import scalafx.Includes.*
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.application.JFXApp3
import cats.effect.*
import cats.effect.std.Dispatcher
import scala.concurrent.duration.*

import cats.effect.unsafe.implicits.global

object HelloStageDemo extends IOApp:
  def run(args: List[String]) =
    def ux(deps: UIDependencies) = IO(UI(deps))
      .flatMap(app => IO.interruptible(false)(app.main(args.toArray)))

    val dependencies =
      for
        dispatcher <- Dispatcher[IO]
        state <- Resource.eval(
          State.ref(GeneratorConfig(50.millis, 500.millis))
        )
      yield UIDependencies(dispatcher, state)

    dependencies
      .use(ux)
      .as(ExitCode.Success)
end HelloStageDemo
