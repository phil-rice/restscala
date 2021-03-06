/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.scriptExample.createdCode1

import one.xingyi.core.crypto.Codec
import one.xingyi.core.json.{IXingYiHeaderFor, JsonParserWriter}
import one.xingyi.core.optics.Lens
import one.xingyi.core.script.{Domain, IXingYi, ServerDomain}
import one.xingyi.scriptModel1._

object Model1Defn extends ServerDomain {
  def lens = List("lens_person_name_string", "lens_person_telephonenumber_telephonenumber", "lens_person_line1_string", "lens_person_line2_string", "lens_telephonenumber_number_string")
}

case class TelephoneNumber(mirror: Object) extends Domain with one.xingyi.scriptModel1.ITelephoneNumber
object TelephoneNumber {
  implicit def default[J]: Codec[TelephoneNumber, J] = new Codec[TelephoneNumber, J] {
    override def forwards: TelephoneNumber => J = _.mirror.asInstanceOf[J]
    override def backwards: J => TelephoneNumber = j => TelephoneNumber(j.asInstanceOf[Object])
  }
}

object TelephoneNumberOps {
  implicit def hasHeader[J]: IXingYiHeaderFor[TelephoneNumberOps] = () => List("lens_telephonenumber_number_string")
}
class TelephoneNumberOps(implicit val xingYi: IXingYi) extends ITelephoneNumberOps[Lens, TelephoneNumber] {
  def numberLens = xingYi.stringLens[TelephoneNumber]("lens_telephonenumber_number_string")
}

case class Person(mirror: Object) extends Domain with one.xingyi.scriptModel1.IPerson

object Person {
  implicit def forJson[J]: Codec[Person, J] = new Codec[Person, J] {
    override def forwards: Person => J = _.mirror.asInstanceOf[J]
    override def backwards: J => Person = j => Person(j.asInstanceOf[Object])
  }
}


object PersonNameOps {
  implicit def hasHeader[J]: IXingYiHeaderFor[PersonNameOps[J]] = () => List("lens_person_name_string")
}
class PersonNameOps[J](implicit val xingYi: IXingYi) extends IPersonNameOps[Lens, Person] {
  def nameLens = xingYi.stringLens[Person]("lens_person_name_string")
}

object PersonTelephoneOps {
  implicit def hasHeader[J]: IXingYiHeaderFor[PersonTelephoneOps] = () => List("lens_person_telephonenumber_telephonenumber")
}
class PersonTelephoneOps(implicit val xingYi: IXingYi) extends IPersonTelephoneOps[Lens, Person, TelephoneNumber] {
  def telephoneNumberLens = xingYi.objectLens[Person, TelephoneNumber]("lens_person_telephonenumber_telephonenumber")
}

object PersonLine12Ops {
  implicit def hasHeader: IXingYiHeaderFor[PersonLine12Ops] = () => List("lens_person_line1_string", "lens_person_line2_string")
}
class PersonLine12Ops(implicit val xingYi: IXingYi) extends IPersonLine12Ops[Lens, Person] {
  def line1Lens = xingYi.stringLens[Person]("lens_person_line1_string")
  def line2Lens = xingYi.stringLens[Person]("lens_person_line2_string")
}

