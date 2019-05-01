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

package vinyldns.client.models.batch

import upickle.default._
import vinyldns.client.models.OptionRW

case class BatchChangeCreateInfo(
    changes: List[SingleChangeCreateInfo],
    comments: Option[String],
    ownerGroupId: Option[String]
)

object BatchChangeCreateInfo extends OptionRW {
  implicit val batchChangeCreateInfoRw: ReadWriter[BatchChangeCreateInfo] = macroRW

  def apply(): BatchChangeCreateInfo =
    new BatchChangeCreateInfo(List(SingleChangeCreateInfo()), None, None)
}