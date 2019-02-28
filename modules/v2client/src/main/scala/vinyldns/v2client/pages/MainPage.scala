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

package vinyldns.v2client.pages

import scalacss.ScalaCssReact._
import vinyldns.v2client.models.{Notification, User}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import upickle.default.read
import vinyldns.v2client.ajax.{CurrentUserRoute, Request}
import vinyldns.v2client.components.Notify
import vinyldns.v2client.css.GlobalStyle
import vinyldns.v2client.pages.MainPage.PropsFromMainPage

import scala.util.Try

// AppPages are pages that can be nested in MainPage
// Things nested in MainPage need to have access to the shared Alerter, hence propsFromMainPage
trait AppPage {
  def apply(propsFromMainPage: PropsFromMainPage): Unmounted[PropsFromMainPage, _, _]
}

object MainPage {
  case class State(notification: Option[Notification] = None, loggedInUser: Option[User] = None)
  case class Alerter(set: Option[Notification] => Callback)
  case class Props(childPage: AppPage)
  case class PropsFromMainPage(alerter: Alerter, loggedInUser: User)

  class Backend(bs: BackendScope[Props, State]) {
    def clearNotification: Callback =
      bs.modState(_.copy(notification = None))
    def setNotification(notification: Option[Notification]): Callback =
      bs.modState(_.copy(notification = notification))

    def getLoggedInUser: Callback =
      Request
        .get(CurrentUserRoute())
        .onComplete { xhr =>
          setNotification(Request.toNotification("getting logged in user", xhr, onlyOnError = true))
          if (!Request.isError(xhr.status)) {
            val user = Try(read[User](xhr.responseText)).toOption
            bs.modState(_.copy(loggedInUser = user))
          } else Callback(())
        }
        .asCallback

    def renderAppPage(P: Props, S: State): VdomNode =
      S.loggedInUser match {
        case Some(user) => P.childPage(PropsFromMainPage(Alerter(setNotification), user))
        case None =>
          <.p(
            "Trouble retrieving user info. Please re-login. " +
              "If needed contact your VinylDNS Administrators.")
      }

    def render(P: Props, S: State): VdomElement =
      <.div(
        GlobalStyle.styleSheet.fullViewHeight,
        ^.className := "right_col",
        ^.role := "main",
        S.notification match {
          case Some(n) => Notify(Notify.Props(n, () => clearNotification))
          case None => <.div
        },
        renderAppPage(P, S)
      )
  }

  private val component = ScalaComponent
    .builder[Props]("MainPage")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillMount(e => e.backend.getLoggedInUser)
    .build

  def apply(childPage: AppPage): Unmounted[Props, State, Backend] = component(Props(childPage))
}
