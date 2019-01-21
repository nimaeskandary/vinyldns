package vinyldns.portalv2JS.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object HomePage {

  object Style extends StyleSheet.Inline {
    import dsl._
    val content = style(textAlign.center, fontSize(30.px), minHeight(450.px), paddingTop(40.px))
  }

  val component = {
    ScalaComponent.builder
      .static("HomePage")(<.div(Style.content, "Scala js react template"))
      .build
  }

  def apply(): Unmounted[Unit, Unit, Unit] = component()
}
