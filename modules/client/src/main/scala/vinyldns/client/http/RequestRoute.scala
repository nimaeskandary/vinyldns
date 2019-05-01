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

package vinyldns.client.http

import upickle.default._
import vinyldns.client.models.membership.{Group, GroupList, MemberList, User}
import vinyldns.client.models.record.{RecordSetChange, RecordSetChangeList, RecordSetList}
import vinyldns.client.models.zone.{GetZone, Zone, ZoneList}
import vinyldns.client.components.JsNative.logError
import vinyldns.client.models.batch.{BatchChangeCreateInfo, BatchChangeList, SingleChangeCreateInfo}

import scala.scalajs.js.URIUtils
import scala.util.{Failure, Success, Try}

sealed trait RequestRoute[T] {
  implicit val rw: ReadWriter[T]

  def path: String

  def parse(httpResponse: HttpResponse): Option[T] =
    Try {
      read[T](httpResponse.responseText)
    } match {
      case Success(p) => Some(p)
      case Failure(e) =>
        logError(e.getMessage)
        None
    }

  def toQueryString(map: Map[String, String]): String =
    if (map.isEmpty) ""
    else
      map.foldLeft("") {
        case (acc, (name, value)) if acc.isEmpty => s"?$name=${URIUtils.encodeURIComponent(value)}"
        case (acc, (name, value)) => s"$acc&$name=${URIUtils.encodeURIComponent(value)}"
      }
}

object CurrentUserRoute extends RequestRoute[User] {
  implicit val rw: ReadWriter[User] = User.rw
  def path: String = "/api/users/currentuser"
}

object RegenerateCredentialsRoute extends RequestRoute[Unit] {
  implicit val rw: ReadWriter[Unit] =
    ReadWriter.join(UnitReader, UnitWriter)

  def path: String = "/regenerate-creds"
}

final case class ListGroupsRoute(
    maxItems: Int = 100,
    nameFilter: Option[String] = None,
    startFrom: Option[String] = None)
    extends RequestRoute[GroupList] {
  implicit val rw: ReadWriter[GroupList] = GroupList.rw

  val queryStrings: Map[String, String] =
    Map("maxItems" -> maxItems.toString) ++
      nameFilter.map(f => "groupNameFilter" -> f) ++
      startFrom.map(s => "startFrom" -> s)

  def path: String = s"/api/groups${toQueryString(queryStrings)}"
}

object CreateGroupRoute extends RequestRoute[Group] {
  implicit val rw: ReadWriter[Group] = Group.rw

  def path: String = "/api/groups"
}

final case class GetGroupRoute(id: String) extends RequestRoute[Group] {
  implicit val rw: ReadWriter[Group] = Group.rw

  def path: String = s"/api/groups/$id"
}

final case class DeleteGroupRoute(id: String) extends RequestRoute[Group] {
  implicit val rw: ReadWriter[Group] = Group.rw

  def path: String = s"/api/groups/$id"
}

final case class UpdateGroupRoute(id: String) extends RequestRoute[Group] {
  implicit val rw: ReadWriter[Group] = Group.rw

  def path: String = s"/api/groups/$id"
}

final case class GetGroupMembersRoute(id: String) extends RequestRoute[MemberList] {
  implicit val rw: ReadWriter[MemberList] = MemberList.rw

  def path: String = s"/api/groups/$id/members"
}

final case class LookupUserRoute(username: String) extends RequestRoute[User] {
  implicit val rw: ReadWriter[User] = User.rw

  def path: String = s"/api/users/lookupuser/$username"
}

object CreateZoneRoute extends RequestRoute[Zone] {
  implicit val rw: ReadWriter[Zone] = Zone.rw

  def path: String = "/api/zones"

  // route returns an object {zone: ...}
  override def parse(httpResponse: HttpResponse): Option[Zone] =
    Try(read[GetZone](httpResponse.responseText)) match {
      case Success(p) => Some(p.zone)
      case Failure(e) =>
        logError(e.getMessage)
        None
    }
}

final case class UpdateZoneRoute(id: String) extends RequestRoute[Zone] {
  implicit val rw: ReadWriter[Zone] = Zone.rw

  def path: String = s"/api/zones/$id"

  // route returns an object {zone: ...}
  override def parse(httpResponse: HttpResponse): Option[Zone] =
    Try(read[GetZone](httpResponse.responseText)) match {
      case Success(p) => Some(p.zone)
      case Failure(e) =>
        logError(e.getMessage)
        None
    }
}

final case class ListZonesRoute(
    maxItems: Int = 100,
    nameFilter: Option[String] = None,
    startFrom: Option[String] = None)
    extends RequestRoute[ZoneList] {
  implicit val rw: ReadWriter[ZoneList] = ZoneList.rw

  val queryStrings: Map[String, String] =
    Map("maxItems" -> maxItems.toString) ++
      nameFilter.map(f => "nameFilter" -> f) ++
      startFrom.map(s => "startFrom" -> s.toString)

  def path: String = s"/api/zones${toQueryString(queryStrings)}"
}

final case class DeleteZoneRoute(id: String) extends RequestRoute[Zone] {
  implicit val rw: ReadWriter[Zone] = Zone.rw

  def path: String = s"/api/zones/$id"
}

final case class GetZoneRoute(id: String) extends RequestRoute[Zone] {
  implicit val rw: ReadWriter[Zone] = Zone.rw

  def path: String = s"/api/zones/$id"

  // route returns an object {zone: ...}
  override def parse(httpResponse: HttpResponse): Option[Zone] =
    Try(read[GetZone](httpResponse.responseText)) match {
      case Success(p) => Some(p.zone)
      case Failure(e) =>
        logError(e.getMessage)
        None
    }
}

final case class ListRecordSetsRoute(
    zoneId: String,
    maxItems: Int = 100,
    nameFilter: Option[String] = None,
    startFrom: Option[String] = None)
    extends RequestRoute[RecordSetList] {
  implicit val rw: ReadWriter[RecordSetList] = RecordSetList.rw

  val queryStrings: Map[String, String] =
    Map("maxItems" -> maxItems.toString) ++
      nameFilter.map(f => "recordNameFilter" -> f) ++
      startFrom.map(s => "startFrom" -> s)

  def path: String = s"/api/zones/$zoneId/recordsets${toQueryString(queryStrings)}"
}

final case class CreateRecordSetRoute(zoneId: String) extends RequestRoute[RecordSetChange] {
  implicit val rw: ReadWriter[RecordSetChange] = RecordSetChange.rw

  def path: String = s"/api/zones/$zoneId/recordsets"
}

final case class DeleteRecordSetRoute(zoneId: String, recordId: String)
    extends RequestRoute[RecordSetChange] {
  implicit val rw: ReadWriter[RecordSetChange] = RecordSetChange.rw

  def path: String = s"/api/zones/$zoneId/recordsets/$recordId"
}

final case class UpdateRecordSetRoute(zoneId: String, recordId: String)
    extends RequestRoute[RecordSetChange] {
  implicit val rw: ReadWriter[RecordSetChange] = RecordSetChange.rw

  def path: String = s"/api/zones/$zoneId/recordsets/$recordId"
}

final case class ListRecordSetChangesRoute(
    zoneId: String,
    maxItems: Int = 100,
    startFrom: Option[String] = None)
    extends RequestRoute[RecordSetChangeList] {
  implicit val rw: ReadWriter[RecordSetChangeList] = RecordSetChangeList.rw

  val queryStrings: Map[String, String] = Map.empty[String, String] ++
    Map("maxItems" -> maxItems.toString) ++
    startFrom.map(s => "startFrom" -> s)

  def path: String = s"/api/zones/$zoneId/recordsetchanges${toQueryString(queryStrings)}"
}

final case class ListBatchChangesRoute(maxItems: Int = 100, startFrom: Option[String] = None)
    extends RequestRoute[BatchChangeList] {
  implicit val rw: ReadWriter[BatchChangeList] = BatchChangeList.batchChangeListRw

  val queryStrings: Map[String, String] = Map.empty[String, String] ++
    Map("maxItems" -> maxItems.toString) ++
    startFrom.map(s => "startFrom" -> s)

  def path: String = s"/api/batchchanges${toQueryString(queryStrings)}"
}

object CreateBatchChangeRoute extends RequestRoute[BatchChangeCreateInfo] {
  implicit val rw: ReadWriter[BatchChangeCreateInfo] = BatchChangeCreateInfo.batchChangeCreateInfoRw

  def path: String = "/api/batchchanges"

  override def parse(httpResponse: HttpResponse): Option[BatchChangeCreateInfo] =
    Try {
      read[List[SingleChangeCreateInfo]](httpResponse.responseText)
    } match {
      case Success(p) => Some(BatchChangeCreateInfo(p, None, None, None))
      case Failure(e) =>
        logError(e.getMessage)
        None
    }
}
