package vinyldns.portalv2JVM

import scalatags.Text.all._

object Page {
  val containerId = "root"
  val boot = s"ReactApp.main('$containerId')"
  val skeleton =
    html(
      head(
        script(src := "/portalv2-fastopt-bundle.js"),
        link(
          rel := "stylesheet",
          href := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
        ),
        script(href := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js")
      ),
      body(
        onload := boot,
        div(id := containerId)
      )
    )
}
