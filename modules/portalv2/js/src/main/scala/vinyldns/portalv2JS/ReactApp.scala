package vinyldns.portalv2JS

import org.scalajs.dom
import vinyldns.portalv2JS.css.AppCSS
import vinyldns.portalv2JS.routes.AppRouter

import scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ReactApp")
object ReactApp {
  @JSExport
  def main(containerId: String): Unit = {
    AppCSS.load
    AppRouter.router().renderIntoDOM(dom.document.getElementById(containerId))
  }
}
