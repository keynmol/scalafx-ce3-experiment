package test
package ui

import test.background.*
import test.fibers.*

import scalafx.Includes.*
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.application.JFXApp3
import cats.effect.*
import scalafx.scene.layout.VBox
import scalafx.scene.control.Label
import scalafx.scene.layout.HBox
import scalafx.scene.control.Button
import scalafx.scene.control.ButtonBar
import cats.effect.std.Dispatcher
import scalafx.scene.layout.GridPane
import scalafx.event.EventType
import scalafx.event.Event
import scalafx.geometry.Insets
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scala.concurrent.duration.*
import cats.syntax.all.*
import scalafx.scene.text.Text
import scalafx.scene.Node
import scalafx.application.Platform
import scala.util.Random

case class UIDependencies(
    dispatcher: Dispatcher[IO],
    state: State[GeneratorConfig]
):
  export dispatcher.{unsafeRunAndForget as dispatchAsync}

case class Config(columns: Int, workers: Int)

class UI(
    dependencies: UIDependencies,
    config: Config = Config(4, 16)
) extends JFXApp3:
  import dependencies.*
  import FiberProgram.HandleUpdate

  def lab(init: String) =
    new Text:
      text = init
      style = "-fx-font-size: 25px"

  def grid(count: Int, columns: Int): (Node, HandleUpdate) =
    val gp = new GridPane:
      hgap = 10
      vgap = 10

    val rows =
      if count % columns != 0 then count / columns + 1
      else count / columns

    val buf = Map.newBuilder[(Int, Int), Text]

    for
      c <- 0 until columns
      r <- 0 until rows
    do
      val txt = lab("0")
      gp.add(txt, c, r)
      buf.addOne(c -> r, txt)

    val mp = buf.result

    val handler: HandleUpdate =
      (c, r, upd) =>
        mp.get(c -> r).foreach { txt =>
          txt.text = upd
        }

    gp -> handler
  end grid

  def Processors: (scalafx.scene.Node, HandleUpdate) =
    grid(config.workers, config.columns)

  def startButton(handler: HandleUpdate) =
    new Button("Start"):
      this.setOnAction { evt =>
        dispatchAsync(state.start(FiberProgram.create(config, handler, _)))
      }

  def stopButton =
    new Button("Stop"):
      this.setOnAction { evt =>
        dispatchAsync(state.stop)
      }

  def Buttons =
    val test = Processors
    new Scene:
      root = new VBox:
        padding = Insets(15, 15, 15, 15)
        children = Seq(
          new Label("Demonstration of Cats Effect 3 parallel processing"),
          new Label("""
            | This application launches 16 fibers in parallel, 
            | and each fiber keeps a track of the number of its executions in
            | IOLocal[Int].
            | Each execution is delayed by a random amount of time,
            | in the range between 50 and 500 milliseconds.
            | 
            | Each fiber increments its corresponding number in the grid
            """.stripMargin),
          test._1,
          startButton(test._2),
          stopButton
        )
    end new
  end Buttons

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title.value = "Hello Stage"
      width = 600
      height = 450
      scene = Buttons
end UI
