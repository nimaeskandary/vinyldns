package vinyldns.portalv2JVM

import scalatags.Text.all._

object Page {
  val boot =
    "Client.main(document.getElementById('contents'))"
  val skeleton =
    html(
      head(
        script(src := "/portalv2-fastopt-bundle.js")
      ),
      body(
        onload := boot,
        div(id := "contents")
      )
    )
}
