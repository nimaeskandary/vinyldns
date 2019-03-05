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

package vinyldns.client

import japgolly.scalajs.react.extra.router.BaseUrl
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext.Ajax
import vinyldns.client.ajax.CurrentUserRoute
import vinyldns.client.css.AppCSS
import vinyldns.client.routes.AppRouter
import vinyldns.client.models.user.User

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.Try

@JSExportTopLevel("ReactApp")
object ReactApp {
  final val SUCCESS_ALERT_TIMEOUT_MILLIS = 5000.0
  final val csrf: String = Try(document.getElementById("csrf").getAttribute("content"))
    .getOrElse("csrf-unset")
  final val version: String = Try(document.getElementById("version").getAttribute("content"))
    .getOrElse("version-unset")
  var loggedInUser: User = _

  @JSExport
  def main(containerId: String): Unit = {
    AppCSS.load
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global
    Ajax
      .get(CurrentUserRoute.path)
      .onComplete { response =>
        response.map { xhr =>
          CurrentUserRoute.parse(xhr) match {
            case Some(u) =>
              loggedInUser = u
              AppRouter.router().renderIntoDOM(dom.document.getElementById(containerId))
            case None => dom.window.location.assign((BaseUrl.fromWindowOrigin / "login").value)
          }
        }
      }
  }
}