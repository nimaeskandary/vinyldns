package vinyldns.portalv2JS

import scalatags.JsDom.all._

import org.scalajs.dom
import dom.html
import org.scalajs.dom.raw.Node

import scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Client")
object Client extends {
  @JSExport
  def main(container: html.Div): Node =
    container.appendChild(
      div(
        h1("hello world")
      ).render
    )
}
