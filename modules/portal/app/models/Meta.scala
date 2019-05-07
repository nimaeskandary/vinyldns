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

package models
import play.api.Configuration
import play.api.libs.json._


case class Meta(
    version: String,
    sharedDisplayEnabled: Boolean,
    batchChangeLimit: Int,
    defaultTtl: Long,
    customLinksJson: String)
object Meta {
  implicit val customLinkWrites: Writes[CustomLink] = new Writes[CustomLink] {
    def writes(customLink: CustomLink): JsObject = Json.obj(
      "displayOnSidebar" -> customLink.displayOnSidebar,
      "displayOnLoginScreen" -> customLink.displayOnLoginScreen,
      "title" -> customLink.title,
      "href" -> customLink.href,
      "icon" -> customLink.icon
    )
  }

  def apply(config: Configuration): Meta =
    Meta(
      config.getOptional[String]("vinyldns.version").getOrElse("unknown"),
      config.getOptional[Boolean]("shared-display-enabled").getOrElse(false),
      config.getOptional[Int]("batch-change-limit").getOrElse(1000),
      config.getOptional[Long]("default-ttl").getOrElse(7200L),
      Json.toJson(CustomLinks(config).links).toString()
    )
}
