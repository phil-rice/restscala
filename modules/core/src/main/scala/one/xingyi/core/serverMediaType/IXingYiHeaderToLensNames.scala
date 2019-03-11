/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.serverMediaType
import scala.collection.Set

trait IXingYiHeaderToLensNames {
  def apply(xingyiHeader: Option[String]): Set[String]
}
object IXingYiHeaderToLensNames {
  implicit object headerToLensNames extends IXingYiHeaderToLensNames {
    override def apply(xingyiHeader: Option[String]): Set[String] = xingyiHeader match {
      case None => Set()
      case Some(header) if !header.contains(DomainDefn.xingyiHeaderPrefix) => Set()
      case Some(header) =>
        if (!header.startsWith(DomainDefn.xingyiHeaderPrefix)) throw new RuntimeException(s"Must start with ${DomainDefn.xingyiHeaderPrefix} actually is $header")
        val withoutPrefix = header.substring(DomainDefn.xingyiHeaderPrefix.length)
        DomainList.stringToSet(withoutPrefix)
    }
  }
}