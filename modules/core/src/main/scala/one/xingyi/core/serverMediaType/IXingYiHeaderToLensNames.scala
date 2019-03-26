/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.serverMediaType
import one.xingyi.core.strings.Strings
import one.xingyi.core.strings.Strings._

import scala.collection.Set

case class XingYiHeaderDetails(lensDlsName: LensLanguage, lensNames: Set[String])


trait IXingYiHeaderToLensNames extends {
  def accept(xingyiHeader: String): Option[XingYiHeaderDetails]
}
object IXingYiHeaderToLensNames {
  implicit def headerToLensNames(implicit lensLanguages: LensLanguages): IXingYiHeaderToLensNames = new DefaultXingYiHeaderToLensNames
}

class DefaultXingYiHeaderToLensNames(implicit lensLanguages: LensLanguages) extends IXingYiHeaderToLensNames {
  override def accept(header: String): Option[XingYiHeaderDetails] =
    if (header.contains(DomainDefn.xingyiHeaderPrefix)) {
      if (!header.startsWith(DomainDefn.xingyiHeaderPrefix)) throw new RuntimeException(s"Must start with ${DomainDefn.xingyiHeaderPrefix} actually is $header")
      val withoutPrefix = header.substring(DomainDefn.xingyiHeaderPrefix.length)
      val (languageName, lens) = partition(".")(withoutPrefix).fold(throw new RuntimeException(s"There must be a language name after the '.'. The header was $header"))(x => x)
      val lensLanguage = lensLanguages.find(languageName).getOrElse(throw new RuntimeException(s"Language: ${languageName} not known. Legal values ${lensLanguages.legalValues}. Header is $header")).lensL
      Some(XingYiHeaderDetails(lensLanguage, DomainList.stringToSet(lens)))
    }
    else
      None

}