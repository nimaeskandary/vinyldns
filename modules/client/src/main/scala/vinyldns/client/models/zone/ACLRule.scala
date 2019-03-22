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

package vinyldns.client.models.zone

import upickle.default.{ReadWriter, macroRW}
import vinyldns.client.models.OptionRW

case class Rules(rules: List[ACLRule])

object Rules {
  implicit val rw: ReadWriter[Rules] = macroRW
}

case class ACLRule(
    accessLevel: String,
    recordTypes: Seq[String],
    description: Option[String] = None,
    userId: Option[String] = None,
    groupId: Option[String] = None,
    recordMask: Option[String] = None)

object ACLRule extends OptionRW {
  implicit val rw: ReadWriter[ACLRule] = macroRW
}