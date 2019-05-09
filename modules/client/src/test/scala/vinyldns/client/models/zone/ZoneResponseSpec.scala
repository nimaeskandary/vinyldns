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

import org.scalatest._
import vinyldns.client.SharedTestData
import vinyldns.client.models.membership.Id

class ZoneResponseSpec extends WordSpec with Matchers with SharedTestData {
  "ZoneResponse.canEdit" should {
    val group = generateGroupResponses(1).head

    "return true if the user is super" in {
      ZoneResponse.canEdit(testUser.copy(isSuper = true, isSupport = false), None) shouldBe true
    }

    "return true if the user is support" in {
      ZoneResponse.canEdit(testUser.copy(isSuper = false, isSupport = true), None) shouldBe true
    }

    "return true if the user is in the admin group" in {
      ZoneResponse.canEdit(
        testUser.copy(isSuper = false, isSupport = false),
        Some(group.copy(admins = Seq(Id(testUser.id))))) shouldBe true
    }

    "return false if not super, support, or group admin" in {
      ZoneResponse.canEdit(testUser.copy(isSuper = false, isSupport = false), None) shouldBe false
    }
  }
}
