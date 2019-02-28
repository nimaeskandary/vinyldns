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

package vinyldns.v2client.components

import cats.implicits._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

object InputField {
  case class Props(
      label: String,
      labelClass: String = "control-label",
      labelSize: String = "col-md-3 col-sm-3 col-xs-12",
      inputClass: String = "form-control",
      inputSize: String = "col-md-6 col-sm-6 col-xs-12",
      placeholder: Option[String] = None,
      initialValue: Option[String] = None,
      isNumber: Boolean = false,
      maxSize: Option[Int] = None,
      required: Boolean = false)
  case class State(
      value: Option[String],
      isValid: Boolean = true,
      errorMessage: Option[String] = None)

  class Backend(bs: BackendScope[Props, State]) {
    def toInputType(P: Props): String = if (P.isNumber) "number" else "text"

    def onChange(e: ReactEventFromInput, P: Props): Callback = {
      val target = e.target
      val value = target.value
      val validatedValue = validate(value, P)
      validatedValue match {
        case Right(_) =>
          bs.modState { S =>
            target.setCustomValidity("")
            S.copy(value = Some(value), isValid = true)
          }
        case Left(error) => {
          bs.modState { S =>
            target.setCustomValidity(error)
            S.copy(value = Some(value), isValid = false, errorMessage = Some(error))
          }
        }
      }
    }

    def validate(value: String, P: Props): Either[String, Unit] =
      for {
        _ <- validateRequired(value, P)
        _ <- validateMaxSize(value, P)
      } yield ()

    def validateRequired(value: String, P: Props): Either[String, Unit] =
      if (P.required)
        Either.cond(
          value.length > 0,
          (),
          "Required"
        )
      else ().asRight

    def validateMaxSize(value: String, P: Props): Either[String, Unit] =
      P.maxSize match {
        case Some(max) =>
          Either.cond(
            value.length < max,
            (),
            s"Must be less than $max characters"
          )
        case None => ().asRight
      }

    def generateInputClass(P: Props, S: State): String =
      if (S.isValid) P.inputClass
      else s"${P.inputClass} parsley-error"

    def errors(S: State): VdomNode =
      if (S.isValid) <.span
      else
        <.ul(
          ^.className := "parsley-errors-list filled",
          <.li(
            ^.className := "parley-required",
            S.errorMessage.getOrElse[String]("Invalid")
          )
        )

    def render(P: Props, S: State): VdomElement =
      <.div(
        ^.className := "form-group",
        <.label(
          ^.className := s"${P.labelClass} ${P.labelSize}",
          P.label,
        ),
        <.div(
          ^.className := P.inputSize,
          <.input(
            ^.className := generateInputClass(P, S),
            ^.`type` := toInputType(P),
            ^.value := S.value.getOrElse(""),
            ^.placeholder := P.placeholder.getOrElse(""),
            ^.onChange ==> (e => onChange(e, P)),
            ^.required := P.required
          ),
          errors(S)
        )
      )
  }

  val component = ScalaComponent
    .builder[Props]("Input")
    .initialStateFromProps { P =>
      State(P.initialValue)
    }
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, State, Backend] = component(props)
}
