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

package vinyldns.v2client.css

object GlobalStyle {

  val CssSettings = scalacss.devOrProdDefaults
  import CssSettings._

  object styleSheet extends StyleSheet.Inline {
    import dsl._

    val height100 = style(height :=! "100%")
    val width100 = style(width :=! "100%")
    val overrideDisplay = style(display.block.important)
    val cursorPointer = style(cursor.pointer)
    val maxHeight100 = style(maxHeight :=! "100%")
    val maxWidth100 = style(maxWidth :=! "100%")
    val backgroundWhite = style(backgroundColor :=! "white")
    val notifyOuter = style(
      width :=! "300px",
      right :=! "36px",
      top :=! "36px",
      cursor.auto,
      position.absolute,
      wordWrap.breakWord,
      zIndex :=! "2000"
    )
    val notifyInner = style(minHeight :=! "16px")
  }
}