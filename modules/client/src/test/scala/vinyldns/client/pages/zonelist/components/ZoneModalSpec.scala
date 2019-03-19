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

package vinyldns.client.pages.zonelist.components

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.test._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import vinyldns.client.SharedTestData
import vinyldns.client.http.{CreateZoneRoute, Http}
import vinyldns.client.models.membership.GroupList
import vinyldns.client.models.zone.{Zone, ZoneConnection, ZoneCreateInfo}
import upickle.default.write

import scala.language.existentials

class ZoneModalSpec extends WordSpec with Matchers with MockFactory with SharedTestData {

  trait Fixture {
    val mockHttp = mock[Http]
    val groupList = GroupList(generateGroups(2).toList, Some(100))
    val props =
      ZoneModal.Props(mockHttp, generateNoOpHandler[Unit], generateNoOpHandler[Unit], groupList)
  }

  "ZoneModal" should {
    "not call withConfirmation if submitted without required fields" in new Fixture {
      (mockHttp.withConfirmation _).expects(*, *).never()
      (mockHttp.post _).expects(*, *, *, *).never()

      ReactTestUtils.withRenderedIntoDocument(ZoneModal(props)) { c =>
        val form = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-zone-form")
        Simulate.submit(form)
      }
    }

    "call withConfirmation if submitting with required fields" in new Fixture {
      (mockHttp.withConfirmation _).expects(*, *).once().returns(Callback.empty)
      (mockHttp.post _).expects(*, *, *, *).never()

      ReactTestUtils.withRenderedIntoDocument(ZoneModal(props)) { c =>
        val nameField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-name")
        val emailField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-email")
        val adminField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-group-admin")

        Simulate.change(nameField, SimEvent.Change("test-zone."))
        Simulate.change(emailField, SimEvent.Change("test@email.com"))
        Simulate.change(adminField, SimEvent.Change(groupList.groups.head.id))

        val form = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-zone-form")
        Simulate.submit(form)
      }
    }

    "call http.post if submitting with required fields and confirming" in new Fixture {
      val expectedZone = ZoneCreateInfo("test-zone.", "test@email.com", groupList.groups.head.id)

      (mockHttp.withConfirmation _).expects(*, *).once().onCall((_, cb) => cb)
      (mockHttp.post[Zone] _)
        .expects(CreateZoneRoute, write(expectedZone), *, *)
        .never()
        .once()
        .returns(Callback.empty)

      ReactTestUtils.withRenderedIntoDocument(ZoneModal(props)) { c =>
        val nameField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-name")
        val emailField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-email")
        val adminField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-group-admin")

        Simulate.change(nameField, SimEvent.Change("test-zone."))
        Simulate.change(emailField, SimEvent.Change("test@email.com"))
        Simulate.change(adminField, SimEvent.Change(groupList.groups.head.id))

        val form = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-zone-form")
        Simulate.submit(form)
      }
    }

    "not allow user to submit unless picking a valid groupId" in new Fixture {
      (mockHttp.withConfirmation _).expects(*, *).never()
      (mockHttp.post _).expects(*, *, *, *).never()

      ReactTestUtils.withRenderedIntoDocument(ZoneModal(props)) { c =>
        val nameField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-name")
        val emailField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-email")
        val adminField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-group-admin")

        Simulate.change(nameField, SimEvent.Change("test-zone."))
        Simulate.change(emailField, SimEvent.Change("test@email.com"))
        Simulate.change(adminField, SimEvent.Change("no-existo"))

        val form = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-zone-form")
        Simulate.submit(form)
      }
    }

    "make request with customServer toggled on" in new Fixture {
      val expectedZone =
        ZoneCreateInfo(
          "test-zone.",
          "test@email.com",
          groupList.groups.head.id,
          Some(ZoneConnection("name", "name", "key", "1.1.1.1")))

      (mockHttp.withConfirmation _).expects(*, *).once().onCall((_, cb) => cb)
      (mockHttp.post[Zone] _)
        .expects(CreateZoneRoute, write(expectedZone), *, *)
        .never()
        .once()
        .returns(Callback.empty)

      ReactTestUtils.withRenderedIntoDocument(ZoneModal(props)) { c =>
        val customServerToggle =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-custom-server")
        Simulate.change(customServerToggle, SimEvent.Change())

        val nameField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-name")
        val emailField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-email")
        val adminField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-group-admin")
        val customKeyName =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-connection-key-name")
        val customKey = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-connection-key")
        val customServer =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-connection-server")

        Simulate.change(nameField, SimEvent.Change("test-zone."))
        Simulate.change(emailField, SimEvent.Change("test@email.com"))
        Simulate.change(adminField, SimEvent.Change(groupList.groups.head.id))
        Simulate.change(customKeyName, SimEvent.Change("name"))
        Simulate.change(customKey, SimEvent.Change("key"))
        Simulate.change(customServer, SimEvent.Change("1.1.1.1"))

        val form = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-zone-form")
        Simulate.submit(form)
      }
    }

    "make request with customTransfer toggled on" in new Fixture {
      val expectedZone =
        ZoneCreateInfo(
          "test-zone.",
          "test@email.com",
          groupList.groups.head.id,
          transferConnection = Some(ZoneConnection("name", "name", "key", "1.1.1.1")))

      (mockHttp.withConfirmation _).expects(*, *).once().onCall((_, cb) => cb)
      (mockHttp.post[Zone] _)
        .expects(CreateZoneRoute, write(expectedZone), *, *)
        .never()
        .once()
        .returns(Callback.empty)

      ReactTestUtils.withRenderedIntoDocument(ZoneModal(props)) { c =>
        val customServerToggle =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-custom-transfer")
        Simulate.change(customServerToggle, SimEvent.Change())

        val nameField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-name")
        val emailField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-email")
        val adminField =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-group-admin")
        val customKeyName =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-transfer-key-name")
        val customKey = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-transfer-key")
        val customServer =
          ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-transfer-server")

        Simulate.change(nameField, SimEvent.Change("test-zone."))
        Simulate.change(emailField, SimEvent.Change("test@email.com"))
        Simulate.change(adminField, SimEvent.Change(groupList.groups.head.id))
        Simulate.change(customKeyName, SimEvent.Change("name"))
        Simulate.change(customKey, SimEvent.Change("key"))
        Simulate.change(customServer, SimEvent.Change("1.1.1.1"))

        val form = ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-zone-form")
        Simulate.submit(form)
      }
    }
  }
}
