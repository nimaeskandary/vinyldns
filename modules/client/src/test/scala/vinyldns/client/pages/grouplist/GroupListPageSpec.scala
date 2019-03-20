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

package vinyldns.client.pages.grouplist

import org.scalatest._
import japgolly.scalajs.react.test._
import org.scalamock.scalatest.MockFactory
import vinyldns.client.SharedTestData
import vinyldns.client.http.{Http, HttpResponse, ListGroupsRoute}
import vinyldns.client.models.membership.GroupList
import vinyldns.client.routes.AppRouter.{Page, ToGroupListPage}
import vinyldns.client.pages.grouplist.components.GroupModal

import scala.language.existentials

class GroupListPageSpec extends WordSpec with Matchers with MockFactory with SharedTestData {
  val initialGroupList = GroupList(generateGroups(1).toList, 100)
  val mockRouter = MockRouterCtl[Page]()

  trait Fixture {
    val mockHttp = mock[Http]

    (mockHttp.get[GroupList] _)
      .expects(ListGroupsRoute(), *, *)
      .once()
      .onCall { (_, onSuccess, _) =>
        onSuccess.apply(mock[HttpResponse], Some(initialGroupList))
      }
  }

  "GroupListPage" should {
    "show create group modal when clicking create group button" in new Fixture {
      ReactTestUtils.withRenderedIntoDocument(GroupListPage(ToGroupListPage, mockRouter, mockHttp)) {
        c =>
          c.state.showCreateGroup shouldBe false
          ReactTestUtils.scryRenderedComponentsWithType(c, GroupModal.component) shouldBe empty

          val createButton =
            ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-create-group")
          Simulate.click(createButton)

          c.state.showCreateGroup shouldBe true
          ReactTestUtils.findRenderedComponentWithType(c, GroupModal.component)
      }
    }

    "close create group modal after clicking close button" in new Fixture {
      ReactTestUtils.withRenderedIntoDocument(GroupListPage(ToGroupListPage, mockRouter, mockHttp)) {
        c =>
          c.state.showCreateGroup shouldBe false
          val createButton =
            ReactTestUtils.findRenderedDOMComponentWithClass(c, "test-create-group")
          Simulate.click(createButton)
          c.state.showCreateGroup shouldBe true

          val modal = ReactTestUtils.findRenderedComponentWithType(c, GroupModal.component)

          val closeButton =
            ReactTestUtils.findRenderedDOMComponentWithClass(modal, "test-close-create-group")
          Simulate.click(closeButton)

          c.state.showCreateGroup shouldBe false
          ReactTestUtils.scryRenderedComponentsWithType(c, GroupModal.component) shouldBe empty
      }
    }
  }
}
