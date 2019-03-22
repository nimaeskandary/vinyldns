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

package vinyldns.client.models

case class Pagination[KeyType](
    startFroms: List[Option[KeyType]] = List(),
    pageNumber: Int = 1,
    popped: Option[KeyType] = None) {

  def next(currentStartFrom: Option[KeyType]): Pagination[KeyType] = {
    val newStartFroms = currentStartFrom :: this.startFroms
    val newPageNumber = this.pageNumber + 1
    this.copy(startFroms = newStartFroms, pageNumber = newPageNumber)
  }

  def previous(): Pagination[KeyType] = {
    val newPageNumber = math.max(1, this.pageNumber - 1)

    this.startFroms match {
      case atLeastTwo if atLeastTwo.length >= 2 =>
        val popped :: rest = this.startFroms
        this.copy(startFroms = rest, pageNumber = newPageNumber, popped = popped)
      case one if one.length == 1 =>
        val popped = one.head
        val rest = List()
        this.copy(startFroms = rest, pageNumber = newPageNumber, popped = popped)
      case _ =>
        this.copy(startFroms = List(), pageNumber = newPageNumber, popped = None)
    }
  }
}