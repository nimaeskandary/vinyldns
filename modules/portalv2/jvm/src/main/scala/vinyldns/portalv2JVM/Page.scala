package vinyldns.portalv2JVM

import scalatags.Text.all._

object Page {
  val containerId = "root"
  val boot = s"ReactApp.main($containerId)"
  val skeleton =
    html(
      head(
        script(src := "/portalv2-fastopt-bundle.js")
      ),
      body(
        onload := boot,
        div(id := containerId)
      )
    )
}
