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

package vinyldns.client.pages.zone.list

import scalacss.ScalaCssReact._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import vinyldns.client.components.AlertBox.addNotification
import vinyldns.client.css.GlobalStyle
import vinyldns.client.http.{GetBackendIdsRoute, Http, HttpResponse, ListGroupsRoute}
import vinyldns.client.models.membership.GroupListResponse
import vinyldns.client.pages.zone.list.components.{ZoneModal, ZonesTable}
import vinyldns.client.router.AppRouter.PropsFromAppRouter
import vinyldns.client.router.Page

object ZoneListPage extends PropsFromAppRouter {
  case class State(
      groupList: Option[GroupListResponse] = None,
      backendIds: Option[List[String]] = None,
      showCreateZone: Boolean = false)

  val component = ScalaComponent
    .builder[Props]("ZoneListPage")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillMount(e => e.backend.listGroups(e.props) >> e.backend.getBackendIds(e.props))
    .build

  def apply(page: Page, router: RouterCtl[Page], http: Http): Unmounted[Props, State, Backend] =
    component(Props(page, router, http))

  class Backend(bs: BackendScope[Props, State]) {
    val refToTable = Ref.toScalaComponent(ZonesTable.component)

    def render(P: Props, S: State): VdomElement =
      (S.groupList, S.backendIds) match {
        case (Some(groupList), Some(backendIds)) =>
          <.div(
            GlobalStyle.Styles.height100,
            ^.className := "right_col",
            ^.role := "main",
            <.div(
              ^.className := "page-title",
              <.div(
                ^.className := "title_left",
                <.h3(<.span(^.className := "fa fa-table"), "  Zones"))),
            <.div(^.className := "clearfix"),
            <.div(
              ^.className := "page-content-wrap",
              <.div(
                ^.className := "row",
                <.div(
                  ^.className := "col-md-12 col-sm-12 col-xs-12",
                  <.div(
                    ^.className := "panel panel-default",
                    <.div(
                      ^.className := "panel-heading",
                      // connect to zone button
                      <.div(
                        ^.className := "btn-group",
                        <.button(
                          ^.className := "btn btn-default test-create-zone",
                          ^.`type` := "button",
                          ^.onClick --> makeCreateFormVisible,
                          <.span(^.className := "fa fa-plus-square"),
                          "  Connect to Zone"
                        ),
                        // refresh button
                        <.button(
                          ^.className := "btn btn-default test-refresh-zones",
                          ^.onClick --> { resetPageInfo >> refreshZonesTable },
                          ^.`type` := "button",
                          <.span(^.className := "fa fa-refresh"),
                          "  Refresh"
                        )
                      ),
                      // search bar
                      <.form(
                        ^.className := "pull-right input-group test-search-form",
                        ^.onSubmit ==> { e: ReactEventFromInput =>
                          e.preventDefaultCB >> resetPageInfo >> refreshZonesTable
                        },
                        <.div(
                          ^.className := "input-group",
                          <.span(
                            ^.className := "input-group-btn",
                            <.button(
                              ^.className := "btn btn-primary btn-left-round",
                              ^.`type` := "submit",
                              <.span(^.className := "fa fa-search")
                            )
                          ),
                          <.input(
                            ^.className := "form-control test-nameFilter",
                            ^.placeholder := "Zone Name",
                            ^.onChange ==> { e: ReactEventFromInput =>
                              updateNameFilter(e.target.value)
                            }
                          )
                        )
                      )
                    ),
                    // table
                    refToTable.component(ZonesTable.Props(P.http, P.router, groupList))
                  )
                )
              )
            ),
            createZoneModal(P, S, groupList, backendIds)
          )
        case _ =>
          // show loading message
          <.div(
            GlobalStyle.Styles.height100,
            ^.className := "right_col",
            ^.role := "main",
            <.div(
              ^.className := "page-title",
              <.div(
                ^.className := "title_left",
                <.h3(<.span(^.className := "fa fa-table"), "  Zones"))),
            <.div(^.className := "clearfix"),
            <.div(
              ^.className := "page-content-wrap",
              <.div(
                ^.className := "row",
                <.div(
                  ^.className := "col-md-12 col-sm-12 col-xs-12",
                  <.div(
                    ^.className := "panel panel-default",
                    <.div(
                      ^.className := "panel-heading",
                      "Loading..."
                    )
                  )
                )
              )
            )
          )
      }

    def createZoneModal(
        P: Props,
        S: State,
        groupList: GroupListResponse,
        backendIds: List[String]): TagMod =
      if (S.showCreateZone)
        ZoneModal(
          ZoneModal
            .Props(
              P.http,
              _ => makeCreateFormInvisible,
              _ => refreshZonesTable,
              groupList,
              backendIds))
      else TagMod.empty

    def refreshZonesTable: Callback =
      refToTable.get
        .map { mounted =>
          mounted.backend.listZones(mounted.props, mounted.state)
        }
        .getOrElse(Callback.empty)
        .runNow()

    def resetPageInfo: Callback =
      refToTable.get
        .map { mounted =>
          mounted.backend.resetPageInfo
        }
        .getOrElse(Callback.empty)
        .runNow()

    def updateNameFilter(value: String): Callback =
      refToTable.get
        .map { mounted =>
          mounted.backend.updateNameFilter(value)
        }
        .getOrElse(Callback.empty)
        .runNow()

    def makeCreateFormVisible: Callback =
      bs.modState(_.copy(showCreateZone = true))

    def makeCreateFormInvisible: Callback =
      bs.modState(_.copy(showCreateZone = false))

    def listGroups(P: Props): Callback = {
      val onSuccess = { (_: HttpResponse, parsed: Option[GroupListResponse]) =>
        bs.modState(_.copy(groupList = parsed))
      }
      val onFailure = { httpResponse: HttpResponse =>
        addNotification(P.http.toNotification("listing groups", httpResponse, onlyOnError = true))
      }
      P.http.get(ListGroupsRoute(1000), onSuccess, onFailure)
    }

    def getBackendIds(P: Props): Callback = {
      val onSuccess = { (_: HttpResponse, parsed: Option[List[String]]) =>
        bs.modState(_.copy(backendIds = parsed))
      }
      val onFailure = { httpResponse: HttpResponse =>
        addNotification(
          P.http.toNotification("listing backend ids", httpResponse, onlyOnError = true))
      }
      P.http.get(GetBackendIdsRoute, onSuccess, onFailure)
    }
  }
}
