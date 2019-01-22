package vinyldns.portalv2JS.css

import scalacss.internal.mutable.GlobalRegistry
import vinyldns.portalv2JS.components.{LeftNav, TopNav}
import vinyldns.portalv2JS.pages.{HomePage, OtherPage}

object AppCSS {

  val CssSettings = scalacss.devOrProdDefaults
  import CssSettings._

  def load: Any = {
    GlobalRegistry.register(
      GlobalStyle.styleSheet,
      TopNav.Style,
      LeftNav.Style,
      OtherPage.Style,
      HomePage.Style)
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
