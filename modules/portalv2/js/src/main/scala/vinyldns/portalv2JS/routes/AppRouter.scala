package vinyldns.portalv2JS.routes

import japgolly.scalajs.react.extra.router.{Resolution, RouterConfigDsl, RouterCtl, _}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html.Div
import vinyldns.portalv2JS.components.{Footer, TopNav}
import vinyldns.portalv2JS.models.Menu
import vinyldns.portalv2JS.pages.HomePage

object AppRouter {

  sealed trait AppPage

  case object Home extends AppPage
  final case class Items(p: Item) extends AppPage

  val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._
    val itemRoutes: Rule =
      Item.routes.prefixPath_/("#items").pmap[AppPage](Items) {
        case Items(p) => p
      }
    (trimSlashes
      | staticRoute(root, Home) ~> render(HomePage())
      | itemRoutes)
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
  }

  val mainMenu = Vector(
    Menu("Home", Home),
    Menu("Items", Items(Item.Info))
  )

  def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]): VdomTagOf[Div] =
    <.div(
      TopNav(TopNav.Props(mainMenu, r.page, c)),
      r.render(),
      Footer()
    )

  val baseUrl = BaseUrl.fromWindowOrigin / "scalajs-react-template/"

  val router = Router(baseUrl, config)
}
