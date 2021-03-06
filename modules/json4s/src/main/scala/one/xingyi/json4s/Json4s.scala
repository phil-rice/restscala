/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.json4s

import java.util.Optional

import one.xingyi.core.json._
import one.xingyi.core.language.AnyLanguage._
import one.xingyi.core.language.FunctionLanguage._
import one.xingyi.core.optics.Lens
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, JValue}

import scala.language.implicitConversions

case class FromJson4sException(msg: String, cause: Throwable) extends Exception(msg, cause)

object Json4sParserWriter extends Json4sParserWriter
trait Json4sParserWriter {
  protected implicit val formats = DefaultFormats
  implicit object JsonParserWriterForJson4s extends JsonParserWriter[JValue] {
    protected implicit val formats = DefaultFormats
    override def extractInt(j: JValue): Int = j.extract[Int]
    override def extractDouble(j: JValue): Double = j.extract[Double]
    override def extractBoolean(j: JValue): Boolean = j.extract[Boolean]
    override def extractString(j: JValue): String = j.extract[String]
    override def extractOptString(j: JValue): Option[String] = j.extractOpt[String]
    override def asList(j: JValue): List[JValue] = j.extract[List[JValue]]
    override def \(j: JValue, s: String): JValue = j \ s
    override def apply(s: String): JValue = JsonMethods.parse(s).ifError(e => throw new FromJson4sException(s"String is [$s]", e))
    override def asListOf[T](j: JValue, mirrorFn: JValue => T): List[T] = asList(j).map(mirrorFn)
    override def toJ(jsonValue: JsonValue): JValue = jsonValue match {
      case JsonString(s) => s
      case JsonInt(i) => i
      case JsonDouble(d) => d
      case JsonBoolean(b) => b
      case j: JsonObject => JObject(j.nameAndValues.map { case (k, v) => (k, toJ(v)) }: _*)
      case JsonList(list) => JArray(list.map(toJ).toList)
    }
    override def toStringForJ = JsonMethods.render _ ~> JsonMethods.pretty

    def addChild(childName: String)(j: JValue, childValue: JValue) = j match {
      case o@JObject(obj) => JObject(obj.filterNot(_._1 == childName) :+ (childName -> childValue))
      case _ => throw new RuntimeException(s"Cannot extract '$childName' from '$j' as it isn't an object")
    }

    override def lensToChild(childname: String): Lens[JValue, JValue] = Lens(_ \ childname, addChild(childname), name = Some("child " + childname))
    override val lensToString: Lens[JValue, String] = Lens[JValue, String](_.extract[String], (j, s) => JString(s), name = Some("{string}"))
    override val lensToDouble: Lens[JValue, Double] = Lens[JValue, Double](_.extract[Double], (j, s) => JDouble(s), name = Some("{double}"))
    override val lensToInteger: Lens[JValue, Int] = Lens[JValue, Int](_.extract[Int], (j, s) => JInt(s), name = Some("{integer}"))
    override def lensToList: Lens[JValue, List[JValue]] = Lens[JValue, List[JValue]](asList, (j, listt) => JArray(listt), name = Some("*"))
    override def lensToBoolean: Lens[JValue, Boolean] = Lens[JValue, Boolean](_.extract[Boolean], (j,b) => JBool(b), Some("{boolean}"))
  }
}

