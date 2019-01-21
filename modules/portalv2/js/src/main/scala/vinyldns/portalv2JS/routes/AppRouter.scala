package vinyldns.portalv2JS.routes

import japgolly.scalajs.react.extra.router.{Resolution, RouterConfigDsl, RouterCtl, _}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html.Div
import vinyldns.portalv2JS.components.{Footer, LeftNav, TopNav}
import vinyldns.portalv2JS.models.Menu
import vinyldns.portalv2JS.pages.HomePage
import vinyldns.portalv2JS.pages.OtherPage

object AppRouter {

  sealed trait AppPage

  case object Home extends AppPage
  case object Other extends AppPage

  val menu = Vector(
    Menu("Home", Home),
    Menu("Other", Other)
  )

  val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._
    (staticRoute("Home", Home) ~> render(HomePage())
      | staticRoute("Other", Other) ~> render(OtherPage()))
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
  }

  def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]): VdomTagOf[Div] =
    <.div(
      TopNav(TopNav.Props(menu, r.page, c)),
      LeftNav(LeftNav.Props(menu, r.page, c)),
      r.render(),
      Footer()
    )

  val baseUrl = BaseUrl.fromWindowOrigin / ""

  val router = Router(baseUrl, config)
}
