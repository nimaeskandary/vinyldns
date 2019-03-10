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

import cats.implicits._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import vinyldns.client.components.InputFieldType.InputFieldType

import scala.util.Try

case class InputFieldValidations(
    maxSize: Option[Int] = None,
    canContainSpaces: Boolean = true,
    required: Boolean = false)

object InputFieldType extends Enumeration {
  type InputFieldType = Value
  val Text, Email, Number = Value
}

object ValidatedInputField {
  case class Props(
      parentOnChange: String => Callback,
      labelClass: String = "control-label",
      labelSize: String = "col-md-3 col-sm-3 col-xs-12",
      inputClass: String = "form-control",
      inputSize: String = "col-md-6 col-sm-6 col-xs-12",
      label: Option[String] = None,
      placeholder: Option[String] = None,
      helpText: Option[String] = None,
      initialValue: Option[String] = None,
      typ: InputFieldType = InputFieldType.Text,
      validations: Option[InputFieldValidations] = None)
  case class State(
      value: Option[String],
      isValid: Boolean = true,
      errorMessage: Option[String] = None)

  val component = ScalaComponent
    .builder[Props]("ValidatedInput")
    .initialStateFromProps { P =>
      State(P.initialValue)
    }
    .renderBackend[Backend]
    .componentDidMount { e =>
      e.backend.onChange(e.state.value.getOrElse(""), e.props)
    }
    .build

  def apply(props: Props): Unmounted[Props, State, Backend] = component(props)

  class Backend(bs: BackendScope[Props, State]) {
    def render(P: Props, S: State): VdomElement =
      <.div(
        ^.className := "form-group",
        P.label.map { l =>
          <.label(
            ^.className := s"${P.labelClass} ${P.labelSize}",
            l
          )
        },
        <.div(
          ^.className := P.inputSize,
          <.input(
            ^.className := generateInputClass(P, S),
            ^.`type` := toInputType(P),
            ^.value := S.value.getOrElse(""),
            ^.placeholder := P.placeholder.getOrElse(""),
            ^.onChange ==> ((e: ReactEventFromInput) => onChange(e.target.value, P)),
            ^.required := Try(P.validations.get.required).getOrElse(false)
          ),
          helpText(P.helpText),
          errors(S)
        )
      )

    // make use of simple html5 validations
    def toInputType(P: Props): String =
      P.typ match {
        case InputFieldType.Text => "text"
        case InputFieldType.Number => "number"
        case InputFieldType.Email => "email"
      }

    def onChange(value: String, P: Props): Callback = {
      val validatedValue = validate(value, P)
      val localOnChange = validatedValue match {
        case Right(_) =>
          bs.modState { S =>
            S.copy(value = Some(value), isValid = true)
          }
        case Left(error) =>
          bs.modState { S =>
            S.copy(value = Some(value), isValid = false, errorMessage = Some(error))
          }
      }
      localOnChange >> P.parentOnChange(value)
    }

    def validate(value: String, P: Props): Either[String, Unit] =
      P.validations match {
        case Some(checks) =>
          for {
            _ <- validateRequired(value, checks)
            _ <- validateMaxSize(value, checks)
            _ <- validateNoSpaces(value, checks)
          } yield ()
        case None => ().asRight
      }

    def validateRequired(value: String, checks: InputFieldValidations): Either[String, Unit] =
      if (checks.required)
        Either.cond(
          value.length > 0,
          (),
          "Required"
        )
      else ().asRight

    def validateMaxSize(value: String, checks: InputFieldValidations): Either[String, Unit] =
      checks.maxSize match {
        case Some(max) =>
          Either.cond(
            value.length < max,
            (),
            s"Must be less than $max characters"
          )
        case None => ().asRight
      }

    def validateNoSpaces(value: String, checks: InputFieldValidations): Either[String, Unit] =
      if (!checks.canContainSpaces)
        Either.cond(
          !value.contains(" "),
          (),
          "Cannot contain spaces"
        )
      else ().asRight

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

    def helpText(text: Option[String]): VdomNode =
      text match {
        case Some(t) => <.div(^.className := "help-block", t)
        case None => <.div
      }
  }
}
