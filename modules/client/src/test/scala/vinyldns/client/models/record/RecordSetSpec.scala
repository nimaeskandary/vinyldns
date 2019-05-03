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

package vinyldns.client.models.record

import org.scalatest._
import japgolly.scalajs.react.test._
import vinyldns.client.SharedTestData
import vinyldns.core.domain.record.RecordType

class RecordSetSpec extends WordSpec with Matchers with SharedTestData {
  val baseRecord = generateRecordSets(1, "zoneId").head

  "RecordSet.recordDataDisplay" should {
    "display an A record" in {
      val records = List(
        RecordData(address = Some("1.1.1.1")),
        RecordData(address = Some("2.2.2.2")),
        RecordData(address = Some("3.3.3.3"))
      )

      val withRecords = baseRecord.copy(records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """<ul class="table-cell-list"><li>1.1.1.1</li><li>2.2.2.2</li><li>3.3.3.3</li></ul>"""
    }

    "display an AAAA record" in {
      val records = List(
        RecordData(address = Some("1::1")),
        RecordData(address = Some("2::2")),
        RecordData(address = Some("3::3"))
      )

      val withRecords = baseRecord.copy(records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """<ul class="table-cell-list"><li>1::1</li><li>2::2</li><li>3::3</li></ul>"""
    }

    "display a CNAME record" in {
      val record = List(RecordData(cname = Some("cname.")))

      val withRecords = baseRecord.copy(`type` = RecordType.CNAME, records = record)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        "<p>cname.</p>"
    }

    "display a DS record" in {
      val records = List(
        RecordData(
          keytag = Some(1),
          algorithm = Some(3),
          digesttype = Some(1),
          digest = Some("ds1")),
        RecordData(
          keytag = Some(1),
          algorithm = Some(3),
          digesttype = Some(2),
          digest = Some("ds2"))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.DS, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """
          |<ul class="table-cell-list">
          |<li>
          |KeyTag: 1 |
          | Algorithm: 3 |
          | DigestType: 1 |
          | Digest: ds1
          |</li>
          |<li>
          |KeyTag: 1 |
          | Algorithm: 3 |
          | DigestType: 2 |
          | Digest: ds2
          |</li>
          |</ul>""".stripMargin.replaceAll("\n", "")
    }

    "display a MX record" in {
      val records = List(
        RecordData(preference = Some(1), exchange = Some("e1")),
        RecordData(preference = Some(2), exchange = Some("e2"))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.MX, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """
          |<ul class="table-cell-list">
          |<li>
          |Preference: 1 |
          | Exchange: e1
          |</li>
          |<li>
          |Preference: 2 |
          | Exchange: e2
          |</li>
          |</ul>""".stripMargin.replaceAll("\n", "")
    }

    "display a NS record" in {
      val records = List(
        RecordData(nsdname = Some("ns1.")),
        RecordData(nsdname = Some("ns2."))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.NS, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """<ul class="table-cell-list"><li>ns1.</li><li>ns2.</li></ul>"""
    }

    "display a PTR record" in {
      val records = List(
        RecordData(ptrdname = Some("ptr1.")),
        RecordData(ptrdname = Some("ptr2."))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.PTR, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """<ul class="table-cell-list"><li>ptr1.</li><li>ptr2.</li></ul>"""
    }

    "display a SOA record" in {
      val records = List(
        RecordData(
          mname = Some("mname"),
          rname = Some("rname"),
          serial = Some(1),
          refresh = Some(2),
          retry = Some(3),
          expire = Some(4),
          minimum = Some(5))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.SOA, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """
          |<table><tbody>
          |<tr><td>Mname:</td><td>mname</td></tr>
          |<tr><td>Rname:</td><td>rname</td></tr>
          |<tr><td>Serial:</td><td>1</td></tr>
          |<tr><td>Refresh:</td><td>2</td></tr>
          |<tr><td>Retry:</td><td>3</td></tr>
          |<tr><td>Expire:</td><td>4</td></tr>
          |<tr><td class="GlobalStyle_Styles-keepWhitespace">Minimum:   </td><td>5</td></tr>
          |</tbody></table>""".stripMargin.replaceAll("\n", "")
    }

    "display a SPF record" in {
      val records = List(
        RecordData(text = Some("spf1")),
        RecordData(text = Some("spf2"))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.SPF, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """<ul class="table-cell-list"><li>spf1</li><li>spf2</li></ul>"""
    }

    "display a TXT record" in {
      val records = List(
        RecordData(text = Some("txt1")),
        RecordData(text = Some("txt2"))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.TXT, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """<ul class="table-cell-list"><li>txt1</li><li>txt2</li></ul>"""
    }

    "display a SRV record" in {
      val records = List(
        RecordData(priority = Some(1), weight = Some(2), port = Some(3), target = Some("t1")),
        RecordData(priority = Some(4), weight = Some(5), port = Some(6), target = Some("t2"))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.SRV, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """
          |<ul class="table-cell-list">
          |<li>
          |Priority: 1 |
          | Weight: 2 |
          | Port: 3 |
          | Target: t1
          |</li>
          |<li>
          |Priority: 4 |
          | Weight: 5 |
          | Port: 6 |
          | Target: t2
          |</li>
          |</ul>""".stripMargin.replaceAll("\n", "")
    }

    "display a SSHFP record" in {
      val records = List(
        RecordData(algorithm = Some(1), `type` = Some(1), fingerprint = Some("f1")),
        RecordData(algorithm = Some(2), `type` = Some(2), fingerprint = Some("f2"))
      )

      val withRecords = baseRecord.copy(`type` = RecordType.SSHFP, records = records)

      ReactTestUtils
        .renderIntoDocument(withRecords.recordDataDisplay)
        .outerHtmlScrubbed() shouldBe
        """
          |<ul class="table-cell-list">
          |<li>
          |Algorithm: 1 |
          | Type: 1 |
          | Fingerprint: f1
          |</li>
          |<li>
          |Algorithm: 2 |
          | Type: 2 |
          | Fingerprint: f2
          |</li>
          |</ul>""".stripMargin.replaceAll("\n", "")
    }
  }
}