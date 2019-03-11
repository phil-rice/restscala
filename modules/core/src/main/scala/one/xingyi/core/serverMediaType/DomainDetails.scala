/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.serverMediaType
import one.xingyi.core.codemaker.{CodeFragment, LensCodeMaker, ScalaCode, ToScalaCode}
import one.xingyi.core.crypto.Digestor

import scala.collection.Set
import scala.reflect.ClassTag
trait DomainDefnToDetails[SharedE, DomainE] extends (DomainDefn[SharedE, DomainE] => DomainDetails[SharedE, DomainE])

object DomainDefnToDetails {
  def apply[SharedE, DomainE: ClassTag](domainDefn: DomainDefn[SharedE, DomainE])(implicit domainDefnToDetails: DomainDefnToDetails[SharedE, DomainE]) = domainDefnToDetails(domainDefn)

  implicit def default[SharedE, DomainE](implicit javascript: LensCodeMaker[Javascript],
                                         scala: ToScalaCode[DomainDefn[SharedE, DomainE]]): DomainDefnToDetails[SharedE, DomainE] = { defn =>
    val scalaDetails = CodeDetails(scala(defn))
    val javascriptDetails = CodeDetails(javascript(defn))
    DomainDetails[SharedE, DomainE](defn.domainName, defn.packageName, defn.accepts, javascriptDetails.hash,
      defn.renderers,
      defn.lens.map(_.name).toSet,
      Map(Javascript -> javascriptDetails, ScalaCode -> scalaDetails))
  }
}

case class CodeDetails(code: String)(implicit digestor: Digestor) {
  val hash = digestor(code)
}

case class DomainDetails[SharedE, DomainE](name: String, packageName: String, accept: String, codeHeader: String, renderers: Seq[String], lensNames: Set[String], code: Map[CodeFragment, CodeDetails]) {
  def normalisedLens = DomainDetails.stringsToString(lensNames)

  def isDefinedAt(lensNames: Set[String]) = lensNames.forall(this.lensNames.contains)
}
object DomainDetails {
  def stringsToString(set: Iterable[String]) = set.toList.sorted.mkString(",")
}
