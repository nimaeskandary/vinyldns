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

package vinyldns.client.pages.zone.view.components.recordmodal.recordinput

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput}
import vinyldns.client.models.record.{RecordSetCreateInfo, RecordSetResponse}
import vinyldns.client.pages.zone.view.components.recordmodal.recordinput.DsInput.DsField.DsField
import vinyldns.client.pages.zone.view.components.recordmodal._

import scala.util.Try

object DsInput extends RecordDataInput {
  def toTagMod(
      S: RecordSetModal.State,
      bs: BackendScope[RecordSetModal.Props, RecordSetModal.State]): TagMod =
    <.div(
      ^.className := "form-group",
      <.label(
        ^.className := "col-md-3 col xs-12 control-label",
        "Record Data"
      ),
      <.div(
        ^.className := "col-md-6 col-xs-12",
        <.table(
          ^.className := "table table-condensed",
          <.thead(
            <.tr(
              <.th("Key Tag"),
              <.th("Algorithm"),
              <.th("Digest Type"),
              <.th("Digest"),
              <.th
            )
          ),
          <.tbody(
            S.recordSet.records.zipWithIndex.map {
              case (rd, index) =>
                <.tr(
                  ^.key := index,
                  <.td(
                    <.input(
                      ^.className := s"form-control test-keytag",
                      ^.`type` := "number",
                      ^.value := rd.keytagToString,
                      ^.onChange ==> { e: ReactEventFromInput =>
                        changeDsField(bs, e.target.value, index, DsField.KeyTag)
                      },
                      ^.required := true
                    )
                  ),
                  <.td(
                    <.select(
                      ^.className := s"form-control test-algorithm",
                      ^.value := rd.algorithmToString,
                      ^.onChange ==> { e: ReactEventFromInput =>
                        changeDsField(bs, e.target.value, index, DsField.Algorithm)
                      },
                      List(
                        "" -> "",
                        "3" -> "DSA",
                        "5" -> "RSASHA1",
                        "6" -> "DSA NSEC3 SHA1",
                        "7" -> "RSASHA1 NSEC3 SHA1",
                        "8" -> "RSASHA256",
                        "10" -> "RSASHA512",
                        "12" -> "ECC GOST",
                        "13" -> "ECDSAP256SHA256",
                        "14" -> "ECDSAP384SHA384",
                        "15" -> "ED25519",
                        "16" -> "ED448",
                        "253" -> "PRIVATEDNS",
                        "254" -> "PRIVATEOID"
                      ).map {
                        case (value, display) =>
                          <.option(^.key := value, ^.value := value, s"($value) $display")
                      }.toTagMod,
                      ^.required := true
                    )
                  ),
                  <.td(
                    <.select(
                      ^.className := s"form-control test-digesttype",
                      ^.value := rd.digesttypeToString,
                      ^.onChange ==> { e: ReactEventFromInput =>
                        changeDsField(bs, e.target.value, index, DsField.DigestType)
                      },
                      List(
                        "" -> "",
                        "1" -> "SHA1",
                        "2" -> "SHA256",
                        "3" -> "GOSTR341194",
                        "4" -> "SHA384"
                      ).map {
                        case (value, display) =>
                          <.option(^.key := value, ^.value := value, s"($value) $display")
                      }.toTagMod,
                      ^.required := true
                    )
                  ),
                  <.td(
                    <.input(
                      ^.className := s"form-control test-digest",
                      ^.value := rd.digestToString,
                      ^.onChange ==> { e: ReactEventFromInput =>
                        changeDsField(bs, e.target.value, index, DsField.Digest)
                      },
                      ^.required := true
                    )
                  ),
                  <.td(
                    <.button(
                      ^.className := "btn btn-sm btn-danger fa fa-times",
                      ^.`type` := "button",
                      ^.onClick --> removeRow(bs, index)
                    )
                  )
                )
            }.toTagMod,
            <.tr(
              <.td,
              <.td,
              <.td,
              <.td,
              <.td(
                <.button(
                  ^.className := "btn btn-sm btn-info fa fa-plus test-add",
                  ^.`type` := "button",
                  ^.onClick --> addRow(bs)
                )
              )
            )
          )
        )
      )
    )

  object DsField extends Enumeration {
    type DsField = Value
    val KeyTag, Algorithm, DigestType, Digest = Value
  }

  def changeDsField(
      bs: BackendScope[RecordSetModal.Props, RecordSetModal.State],
      value: String,
      index: Int,
      field: DsField): Callback =
    bs.modState { s =>
      val newRow = field match {
        case DsField.KeyTag => s.recordSet.records(index).copy(keytag = Try(value.toInt).toOption)
        case DsField.Algorithm =>
          s.recordSet.records(index).copy(algorithm = Try(value.toInt).toOption)
        case DsField.DigestType =>
          s.recordSet.records(index).copy(digesttype = Try(value.toInt).toOption)
        case DsField.Digest => s.recordSet.records(index).copy(digest = Some(value))
      }
      val newRecordData = s.recordSet.records.updated(index, newRow)

      if (s.isUpdate) {
        val record = s.recordSet.asInstanceOf[RecordSetResponse]
        s.copy(recordSet = record.copy(records = newRecordData))
      } else {
        val record = s.recordSet.asInstanceOf[RecordSetCreateInfo]
        s.copy(recordSet = record.copy(records = newRecordData))
      }
    }
}
