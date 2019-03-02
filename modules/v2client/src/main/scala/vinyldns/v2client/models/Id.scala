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

package vinyldns.v2client.models

import japgolly.scalajs.react.extra.Reusability
import upickle.default.{macroRW, ReadWriter => RW}

// this is because sometimes requests want a {id: String} json object
case class Id(id: String)

object Id {
  implicit val rw: RW[Id] = macroRW
  implicit val idReuse: Reusability[Id] = Reusability.derive[Id]
  implicit val idSeqReuse: Reusability[Seq[Id]] = Reusability.byIterator[Seq, Id](idReuse)
}
