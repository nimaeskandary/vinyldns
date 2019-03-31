/*
 * Copyright 2018 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vinyldns.client.components

import scalacss.ScalaCssReact._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.{BaseUrl, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^._
import vinyldns.client.css.GlobalStyle
import vinyldns.client.routes._

object LeftNav {
  case class NavItem(name: String, faClassName: String, page: Page)
  case class Props(menus: List[NavItem], selectedPage: Page, router: RouterCtl[Page])

  def apply(props: Props): Unmounted[Props, Unit, Unit] = component(props)

  private val component = ScalaComponent
    .builder[Props]("LeftNav")
    .render_P { P =>
      <.div(
        ^.className := "col-md-3 col-sm-3 col-xs-3 left_col",
        <.div(
          ^.className := "left_col scroll-view",
          GlobalStyle.Styles.width100,
          <.div(
            ^.className := "navbar nav_title vinyldns-nav-title",
            <.a(
              <.img(
                ^.className := "vinyldns-logo",
                ^.src := (BaseUrl.fromWindowOrigin / "public/images/vinyldns-portal.png").value
              )
            )
          ),
          <.div(^.className := "clearfix"),
          <.div(
            ^.className := "main_menu_side hidden-print main_menu",
            ^.id := "sidebar-menu",
            <.div(
              ^.className := "menu_section active",
              <.ul(
                ^.className := "nav side-menu",
                P.menus.toTagMod(
                  item => {
                    val active = isActive(P, item.page)
                    <.li(
                      ^.className := activeClass(active),
                      ^.onMouseEnter ==> mouseEnter,
                      ^.onMouseLeave ==> (e => mouseExit(e, active)),
                      ^.key := item.name,
                      <.a(
                        <.i(^.className := item.faClassName),
                        item.name,
                        <.span(^.className := "fa fa-chevron-right"),
                        P.router.setOnClick(item.page)
                      ),
                      toSubMenu(P, item.page)
                    )
                  }
                ),
                <.li(
                  ^.key := "logout",
                  ^.onMouseEnter ==> mouseEnter,
                  ^.onMouseLeave ==> (e => mouseExit(e, false)),
                  <.a(
                    ^.href := (BaseUrl.fromWindowOrigin / "logout").value,
                    <.i(^.className := "fa fa-sign-out"),
                    "Logout"
                  )
                )
              )
            )
          )
        )
      )
    }
    .build

  def activeClass(isActive: Boolean): String =
    if (isActive) "active"
    else ""

  def toSubMenu(P: Props, parent: Page): TagMod = {
    def fromSubTitle(title: String): TagMod =
      <.ul(
        GlobalStyle.Styles.displayBlock,
        ^.className := "nav child_menu",
        <.li(
          ^.className := "active",
          <.a(
            <.i(^.className := "fa fa-eye"),
            title,
            P.router.setOnClick(P.selectedPage)
          )
        )
      )

    (P.selectedPage, parent) match {
      case (groupView: ToGroupViewPage, _: ToGroupListPage.type) =>
        fromSubTitle(groupView.id)
      case (zoneView: ToZoneViewPage, _: ToZoneListPage.type) =>
        fromSubTitle(zoneView.id)
      case _ => TagMod.empty
    }
  }

  def isActive(P: Props, target: Page): Boolean =
    (P.selectedPage, target) match {
      case _ if target.getClass == P.selectedPage.getClass => true
      case (_: ToGroupViewPage, _: ToGroupListPage.type) => true
      case (_: ToZoneViewPage, _: ToZoneListPage.type) => true
      case _ => false
    }

  def mouseEnter(e: ReactEventFromInput): Callback =
    Callback(e.currentTarget.className = "active")

  def mouseExit(e: ReactEventFromInput, isActive: Boolean): Callback =
    if (!isActive) Callback(e.currentTarget.className = "")
    else Callback.empty
}
