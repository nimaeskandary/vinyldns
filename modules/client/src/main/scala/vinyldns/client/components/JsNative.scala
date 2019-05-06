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

import japgolly.scalajs.react.Callback

import scala.scalajs.js.timers.setTimeout
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.Date
import scala.util.Try

object JsNative {
  final val ONE_SECOND_IN_MILLIS = 1000.0

  final val HALF_SECOND_IN_MILLIS = ONE_SECOND_IN_MILLIS / 2
  final val TWO_SECONDS_IN_MILLIS = ONE_SECOND_IN_MILLIS * 2
  final val FIVE_SECONDS_IN_MILLIS = ONE_SECOND_IN_MILLIS * 5

  // hook to javascript timeout function
  def withDelay(millis: Double, cb: Callback): Callback =
    Callback(setTimeout(millis)(cb.runNow()))

  def logError(message: String): Unit = {
    Try(global.console.error(message))
    ()
  }

  def toReadableTimestamp(t: String): String =
    Try {
      val datetime = new Date(Date.parse(t))
      val date = datetime.toDateString()
      val time = datetime.toLocaleTimeString()
      s"$date $time"
    }.getOrElse(t)

  def toReadableTimestamp(tO: Option[String]): String =
    tO match {
      case Some(t) => toReadableTimestamp(t)
      case None => ""
    }
}
