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

package vinyldns.client.components

import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import vinyldns.client.SharedTestData
import vinyldns.client.components.ValidatedInputField.Backend

class ValidatedInputFieldSpec extends WordSpec with Matchers with MockFactory with SharedTestData {
  val defaultValidations = InputFieldValidations()
  val required = InputFieldValidations(required = true)
  val noSpaces = InputFieldValidations(noSpaces = true)
  val maxSize = InputFieldValidations(maxSize = Some(5))

  "ValidatedInputField.Backend.validateRequired" should {
    "fail if input is empty" in {
      Backend.validateRequired("", required).isLeft shouldBe true
    }

    "pass if input is not empty" in {
      Backend.validateRequired("value", required).isRight shouldBe true
    }

    "pass if validation is not set" in {
      Backend.validateRequired("", defaultValidations).isRight shouldBe true
      Backend.validateRequired("value", defaultValidations).isRight shouldBe true
    }
  }

  "ValidatedInputField.Backend.validateNoSpaces" should {
    "fail if input has spaces" in {
      Backend.validateNoSpaces("val ue", noSpaces).isLeft shouldBe true
      Backend.validateNoSpaces(" value", noSpaces).isLeft shouldBe true
      Backend.validateNoSpaces("value ", noSpaces).isLeft shouldBe true
    }

    "pass if input has no spaces" in {
      Backend.validateNoSpaces("value", noSpaces).isRight shouldBe true
      Backend.validateNoSpaces("", noSpaces).isRight shouldBe true
    }

    "pass if validation is not set" in {
      Backend.validateNoSpaces("", defaultValidations).isRight shouldBe true
      Backend.validateNoSpaces("value", defaultValidations).isRight shouldBe true
      Backend.validateNoSpaces("val ue", defaultValidations).isRight shouldBe true
    }
  }

  "ValidatedInputField.Backend.validateMaxSize" should {
    "fail if input length is greater than max size" in {
      Backend.validateMaxSize("123456", maxSize).isLeft shouldBe true
    }

    "pass if input length is less than or equal to max size" in {
      Backend.validateMaxSize("12345", maxSize).isRight shouldBe true
      Backend.validateMaxSize("1234", maxSize).isRight shouldBe true
    }

    "pass if validation is not set" in {
      Backend.validateMaxSize("", defaultValidations).isRight shouldBe true
      Backend.validateMaxSize("value", defaultValidations).isRight shouldBe true
      Backend.validateMaxSize("1234567890", defaultValidations).isRight shouldBe true
    }
  }
}