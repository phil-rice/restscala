/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.json

import one.xingyi.core.parser.Parser

import scala.language.implicitConversions
import one.xingyi.core.language.AnyLanguage._
import one.xingyi.core.simpleList.ISimpleList

trait FromJson[T] extends Parser[T]

object FromJson {
}

trait JsonParser[J] {
  def apply(json: String): J
  def extractInt(j: J): Int
  def extractDouble(j: J): Double
  def extractBoolean(j: J): Boolean
  def extractString(j: J): String
  def extractOptString(j: J): Option[String]
  def asList(j: J): List[J]
  def \(j: J, s: String): J
  def asListOf[T](j: J, mirrorFn: J => T): List[T]
}

trait FromJsonLib[J, T] {
  def apply(j: J): T
}

object FromJsonLib {
  implicit def fromString[J](implicit jsonParser: JsonParser[J]): FromJsonLib[J, String] = jsonParser.extractString(_)
}
object JsonParserLanguage extends JsonParserLanguage
trait JsonParserLanguage {
  implicit def jsonToString[J](j: J)(implicit parser: JsonParser[J]) = parser.extractString(j)
  implicit def toInt[J](j: J)(implicit parser: JsonParser[J]) = parser.extractInt(j)
  implicit def toDouble[J](j: J)(implicit parser: JsonParser[J]) = parser.extractDouble(j)
  implicit def toBoolean[J](j: J)(implicit parser: JsonParser[J]) = parser.extractBoolean(j)
  implicit def toOptString[J](j: J)(implicit parser: JsonParser[J]) = parser.extractOptString(j)

  implicit class JsonParserOps[J](j: J)(implicit jsonParser: JsonParser[J]) {
    def map[T1](fn: J => T1): List[T1] = jsonParser.asList(j).map(fn)
    def asList[T1](implicit fromJson: FromJsonLib[J, T1]): List[T1] = map[T1](fromJson.apply)
    def asListP[Shared, Domain](implicit projection: Projection[Shared, Domain]): List[Domain] = map[Domain](projection.fromJson)
    def as[T1](implicit fromJson: FromJsonLib[J, T1]): T1 = fromJson(j)
    def \(s: String): J = jsonParser.\(j, s)
  }

}
