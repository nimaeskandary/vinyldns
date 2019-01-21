package vinyldns.portalv2JS.css

object GlobalStyle {

  val CssSettings = scalacss.devOrProdDefaults
  import CssSettings._

  object styleSheet extends StyleSheet.Inline {
    import dsl._

    style(
      unsafeRoot("body")(
        margin.`0`,
        padding.`0`,
        fontSize(14.px)
      )
    )
  }
}
