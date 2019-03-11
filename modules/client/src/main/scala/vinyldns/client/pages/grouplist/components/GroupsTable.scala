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

package vinyldns.client.pages.grouplist.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import vinyldns.client.http.{DeleteGroupRoute, Http, HttpResponse}
import vinyldns.client.models.Notification
import vinyldns.client.models.membership.{Group, GroupList}
import vinyldns.client.routes.AppRouter.{Page, ToGroupViewPage}

object GroupsTable {
  case class Props(
      http: Http,
      groupsList: Option[GroupList],
      setNotification: Option[Notification] => Callback,
      refresh: Unit => Callback,
      router: RouterCtl[Page])

  val component = ScalaComponent
    .builder[Props](displayName = "ListGroupsTable")
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, Unit, Backend] = component(props)

  class Backend {
    def render(P: Props): VdomElement =
      <.div(
        P.groupsList match {
          case Some(gl) if gl.groups.nonEmpty =>
            <.table(
              ^.className := "table",
              <.thead(
                <.tr(
                  <.th("Name"),
                  <.th("Email"),
                  <.th("Description"),
                  <.th("Actions")
                )
              ),
              <.tbody(
                gl.groups.map(toTableRow(P, _)).toTagMod
              )
            )
          case Some(gl) if gl.groups.isEmpty => <.p("You don't have any groups yet")
          case None => <.p("Loading your groups...")
        }
      )

    def toTableRow(P: Props, group: Group): TagMod =
      <.tr(
        <.td(group.name),
        <.td(group.email),
        <.td(group.description),
        <.td(
          <.div(
            ^.className := "table-form-group",
            <.a(
              ^.className := "btn btn-info btn-rounded test-view",
              P.router.setOnClick(ToGroupViewPage(group.id)),
              ^.title := s"View group ${group.name}",
              VdomAttr("data-toggle") := "tooltip",
              <.span(^.className := "fa fa-eye")
            ),
            <.button(
              ^.className := "btn btn-danger btn-rounded test-delete",
              ^.`type` := "button",
              ^.onClick --> deleteGroup(P, group),
              ^.title := s"Delete group ${group.name}",
              VdomAttr("data-toggle") := "tooltip",
              <.span(^.className := "fa fa-trash")
            )
          )
        )
      )

    def deleteGroup(P: Props, group: Group): Callback =
      P.http.withConfirmation(
        s"Are you sure you want to delete group ${group.name}?",
        Callback
          .lazily {
            val onSuccess = { (httpResponse: HttpResponse, _: Option[Group]) =>
              P.setNotification(
                P.http.toNotification(s"deleting group ${group.name}", httpResponse)) >>
                P.refresh(())
            }
            val onFailure = { httpResponse: HttpResponse =>
              P.setNotification(
                P.http.toNotification(s"deleting group ${group.name}", httpResponse))
            }
            P.http.delete(DeleteGroupRoute(group.id), onSuccess, onFailure)
          }
      )
  }
}
