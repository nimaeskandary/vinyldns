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

package vinyldns.client.pages.zone.list.components

import scalacss.ScalaCssReact._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import vinyldns.client.components.AlertBox.addNotification
import vinyldns.client.css.GlobalStyle
import vinyldns.client.http.{Http, HttpResponse, ListZonesRoute}
import vinyldns.client.models.zone.{ZoneListResponse, ZoneResponse}
import vinyldns.client.models.Pagination
import vinyldns.client.models.membership.GroupListResponse
import vinyldns.client.router.{Page, ToGroupViewPage, ToZoneViewRecordsTab}

import scala.util.Try

object ZonesTable {
  case class Props(http: Http, router: RouterCtl[Page], groupList: GroupListResponse)
  case class State(
      zonesList: Option[ZoneListResponse] = None,
      nameFilter: Option[String] = None,
      pagination: Pagination = Pagination(),
      maxItems: Int = 100)

  val component = ScalaComponent
    .builder[Props]("ListZonesTable")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillMount(e => e.backend.listZones(e.props, e.state))
    .build

  def apply(props: Props): Unmounted[Props, State, Backend] = component(props)

  class Backend(bs: BackendScope[Props, State]) {
    def render(P: Props, S: State): VdomElement =
      <.div(
        S.zonesList match {
          case Some(zl)
              if zl.zones.nonEmpty || zl.nameFilter.isDefined || S.pagination.pageNumber != 1 =>
            <.div(
              <.div(
                ^.className := "panel-heading",
                <.span(
                  itemsPerPage(P, S),
                  paginationButtons(P, S, zl)
                )
              ),
              <.div(^.className := "clearfix"),
              <.div(
                ^.className := "panel-body",
                <.table(
                  ^.className := "table",
                  <.thead(
                    <.tr(
                      <.th("Name"),
                      <.th("Email"),
                      <.th(
                        "Admin Group  ",
                        <.span(
                          GlobalStyle.Styles.cursorPointer,
                          ^.className := "fa fa-info-circle",
                          VdomAttr("data-toggle") := "tooltip",
                          ^.title := "All members of the group have full admin access of the Zone and its DNS records"
                        )
                      ),
                      <.th(
                        "Type  ",
                        <.span(
                          GlobalStyle.Styles.cursorPointer,
                          ^.className := "fa fa-info-circle",
                          VdomAttr("data-toggle") := "tooltip",
                          ^.title :=
                            """
                              |Private Zones are restricted to the Admin Group and ACL Rules.
                              | Shared Zones allow other Vinyl users to manage DNS records
                            """.stripMargin
                        )
                      ),
                      <.th("Actions")
                    )
                  ),
                  <.tbody(
                    zl.zones.map(toTableRow(P, _)).toTagMod
                  )
                )
              )
            )
          case Some(zl) if zl.zones.isEmpty => <.div(<.br, <.p("You don't have any zones yet"))
          case None => <.div(<.br, <.p("Loading your zones..."))
        }
      )

    def itemsPerPage(P: Props, S: State): TagMod =
      <.span(
        <.label(
          GlobalStyle.Styles.keepWhitespace,
          ^.className := "control-label",
          "Items per page:  "
        ),
        <.select(
          ^.value := S.maxItems,
          ^.onChange ==> { e: ReactEventFromInput =>
            val maxItems = Try(e.target.value.toInt).getOrElse(100)
            bs.modState(
              _.copy(maxItems = maxItems),
              resetPageInfo >>
                bs.state >>= { s =>
                listZones(P, s)
              })
          },
          List(100, 50, 25, 5, 1).map { o =>
            <.option(^.key := o, o)
          }.toTagMod
        )
      )

    def paginationButtons(P: Props, S: State, zl: ZoneListResponse): TagMod =
      <.span(
        ^.className := "btn-group pull-right",
        // paginate
        <.button(
          ^.className := "btn btn-round btn-default test-previous-page",
          ^.onClick --> previousPage(P),
          ^.`type` := "button",
          ^.disabled := S.pagination.pageNumber <= 1,
          <.span(
            ^.className := "fa fa-arrow-left"
          ),
          if (S.pagination.pageNumber > 1) s"  Page ${S.pagination.pageNumber - 1}"
          else TagMod.empty
        ),
        <.button(
          ^.className := "btn btn-round btn-default test-next-page",
          ^.onClick --> nextPage(P, S),
          ^.`type` := "button",
          ^.disabled := zl.nextId.isEmpty,
          if (zl.nextId.isDefined) s"Page ${S.pagination.pageNumber + 1}  "
          else TagMod.empty,
          <.span(
            ^.className := "fa fa-arrow-right"
          )
        )
      )

    def listZones(P: Props, S: State, startFrom: Option[String] = None): Callback = {
      val onSuccess = { (_: HttpResponse, parsed: Option[ZoneListResponse]) =>
        bs.modState(_.copy(zonesList = parsed))
      }
      val onFailure = { httpResponse: HttpResponse =>
        addNotification(P.http.toNotification("list zones", httpResponse, onlyOnError = true))
      }
      P.http.get(ListZonesRoute(S.maxItems, S.nameFilter, startFrom), onSuccess, onFailure)
    }

    def toTableRow(P: Props, zone: ZoneResponse): TagMod =
      <.tr(
        ^.key := zone.id,
        <.td(zone.name),
        <.td(zone.email),
        <.td(
          <.a(
            GlobalStyle.Styles.cursorPointer,
            zone.adminGroupName,
            P.router.setOnClick(ToGroupViewPage(zone.adminGroupId))
          )
        ),
        <.td(if (zone.shared) "Shared" else "Private"),
        <.td(
          <.div(
            ^.className := "btn-group",
            <.a(
              ^.className := "btn btn-info btn-rounded test-view",
              P.router.setOnClick(ToZoneViewRecordsTab(zone.id)),
              ^.title := s"View zone ${zone.name}",
              VdomAttr("data-toggle") := "tooltip",
              <.span(^.className := "fa fa-eye"),
              " View"
            )
          )
        )
      )

    def updateNameFilter(value: String): Callback =
      if (value.isEmpty) bs.modState(_.copy(nameFilter = None))
      else bs.modState(_.copy(nameFilter = Some(value)))

    def resetPageInfo: Callback =
      bs.modState(s => s.copy(pagination = Pagination()))

    def nextPage(P: Props, S: State): Callback =
      S.zonesList
        .map { zl =>
          bs.modState({ s =>
            s.copy(pagination = s.pagination.next(zl.startFrom))
          }, bs.state >>= { s =>
            listZones(P, s, zl.nextId)
          })
        }
        .getOrElse(Callback.empty)

    def previousPage(P: Props): Callback =
      bs.modState(
        { s =>
          s.copy(pagination = s.pagination.previous())
        },
        bs.state >>= { s =>
          listZones(P, s, s.pagination.popped)
        }
      )
  }
}
