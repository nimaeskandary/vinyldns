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
import vinyldns.client.ajax.{DeleteGroupRoute, Request}
import vinyldns.client.models.Notification
import vinyldns.client.models.membership.{Group, GroupList}
import vinyldns.client.routes.AppRouter.{Page, ToGroupViewPage}

object GroupsTable {
  case class Props(
      requestHelper: Request,
      groupsList: Option[GroupList],
      setNotification: Option[Notification] => Callback,
      refresh: () => Callback,
      router: RouterCtl[Page])

  private val listGroupsTable = ScalaComponent
    .builder[Props](displayName = "ListGroupsTable")
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, Unit, Backend] = listGroupsTable(props)

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
                gl.groups.map {
                  group =>
                    <.tr(
                      <.td(group.name),
                      <.td(group.email),
                      <.td(group.description),
                      <.td(
                        <.div(
                          ^.className := "table-form-group",
                          <.a(
                            ^.className := "btn btn-info btn-rounded",
                            P.router.setOnClick(ToGroupViewPage(group.id.get)),
                            ^.title := s"View group ${group.name}",
                            VdomAttr("data-toggle") := "tooltip",
                            <.span(^.className := "fa fa-eye")
                          ),
                          <.button(
                            ^.className := "btn btn-danger btn-rounded",
                            ^.`type` := "button",
                            ^.onClick --> deleteGroup(P, group),
                            ^.title := s"Delete group ${group.name}",
                            VdomAttr("data-toggle") := "tooltip",
                            <.span(^.className := "fa fa-trash")
                          )
                        )
                      )
                    )
                }.toTagMod
              )
            )
          case Some(gl) if gl.groups.isEmpty => <.p("You don't have any groups yet")
          case None => TagMod.empty
        }
      )

    def deleteGroup(P: Props, group: Group): Callback =
      P.requestHelper.withConfirmation(
        s"Are you sure you want to delete group ${group.name}?",
        Callback.lazily {
          P.requestHelper
            .delete(DeleteGroupRoute(group.id.getOrElse("")))
            .onComplete { xhr =>
              val alert =
                P.setNotification(
                  P.requestHelper.toNotification(s"deleting group ${group.name}", xhr))
              val refreshGroups =
                if (!P.requestHelper.isError(xhr))
                  P.refresh()
                else Callback.empty
              alert >> refreshGroups
            }
            .asCallback
        }
      )
  }
}
