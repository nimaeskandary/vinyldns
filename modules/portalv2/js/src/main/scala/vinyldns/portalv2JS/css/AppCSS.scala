package vinyldns.portalv2JS.css

import scalacss.ProdDefaults._
import scalacss.internal.mutable.GlobalRegistry
import vinyldns.portalv2JS.components.{LeftNav, TopNav}
import vinyldns.portalv2JS.pages.{HomePage, ItemsPage}

object AppCSS {

  def load: Any = {
    GlobalRegistry.register(
      GlobalStyle,
      TopNav.Style,
      LeftNav.Style,
      ItemsPage.Style,
      HomePage.Style)
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
