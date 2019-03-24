/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.serverMediaType

import one.xingyi.core.codemaker._
import one.xingyi.core.exceptions.CannotRespondToQuery
import one.xingyi.core.json._
import one.xingyi.core.optics.{LensLine, LensLineParser}
import one.xingyi.core.reflection.{ClassTags, Reflect}

import scala.language.implicitConversions
import scala.reflect.ClassTag

object DomainDefn {
  val xingyiHeaderPrefix = "application/xingyi."
  val xingyiCodeSummaryMediaType = "application/json"
  def accepts(lensNames: List[String]) = DomainDefn.xingyiHeaderPrefix + DomainDetails.stringsToString(lensNames)

  implicit def domainDefnToScala[SharedE, DomainE: ClassTag](implicit domainDefnToCodeDom: DomainDefnToCodeDom, domainCdToScala: ToScalaCode[DomainCD]): ToScalaCode[DomainDefn[SharedE, DomainE]] = { defn => domainCdToScala(domainDefnToCodeDom(defn)) }
}

class DomainDefn[SharedE, DomainE: ClassTag](val sharedPackageName: String, val renderers: List[String],
                                             val interfacesToProjections: List[InterfaceAndProjection[_, _]] = List(),
                                             val manual: List[IXingYiSharedOps[XingYiManualPath, _]] = List())
                                            (implicit objectProjection: ObjectProjection[SharedE, DomainE], projectionToLensDefns: ProjectionToLensDefns) {
  def rootName: String = ClassTags.nameOf[DomainE]
  def packageName: String = getClass.getPackage.getName
  def domainName: String = getClass.getSimpleName

  val projectionLens: Map[IXingYiLens[_, _], LensDefnFromProjection[_, _]] = interfacesToProjections.flatMap(x => projectionToLensDefns(x.projection)).distinct.toMap
  val manualLens: List[LensDefnFromProjection[_, _]] = manual.flatMap(Reflect(_).zeroParamMethodsNameAndValue[XingYiManualPath[_, _]].map { case (name, path) => path.makeManualLens(name) }.toList)
  val lens = (projectionLens.values ++ manualLens).toList.sortBy(_.name)

  def accepts: String = DomainDefn.accepts(lens.map(_.name))

  override def toString: String =
    s"""${getClass.getSimpleName}(
       |renderers = ${renderers.mkString(",")}
       |projections =
       |${interfacesToProjections.mkString("\n")}
       |manual = ${manual.mkString(",")}
       |""".stripMargin
}

case class XingYiManualPath[A, B](lensDefn: String)(implicit val classTag: ClassTag[A], val childClassTag: ClassTag[B], lensLineParser: LensLineParser) {
  val lensLine = lensLineParser(lensDefn)
  def makeManualLens(name: String) = ManualLensDefnFromProjection[A, B](lensLine.name, lensLine.defns)

}

case class InterfaceAndProjection[Shared, Domain](projection: ObjectProjection[Shared, Domain], sharedOps: IXingYiSharedOps[IXingYiLens, Shared])
object InterfaceAndProjection {
  implicit def tupleTo[Shared, Domain](tuple: (IXingYiSharedOps[IXingYiLens, Shared], ObjectProjection[Shared, Domain])) =
    InterfaceAndProjection(tuple._2, tuple._1)
}

case class DomainAndMethods[SharedE, DomainE](methodDatas: List[MethodData[DomainE]], defn: DomainDefn[SharedE, DomainE])

object DomainList {
  def stringToSet(s: String) = s.split(",").filterNot(_.isEmpty).toSet
}

case class DomainList[SharedE, DomainE](firstDomain: DomainDetails[SharedE, DomainE], restDomains: DomainDetails[SharedE, DomainE]*) {
  val domains = firstDomain :: restDomains.toList

  def accept(xingyiHeader: Option[String], defaultLanguage: LensLanguage)(implicit xingYiHeaderToLensNames: IXingYiHeaderToLensNames): (LensLanguage, DomainDetails[SharedE, DomainE]) =
    xingyiHeader.flatMap(xingYiHeaderToLensNames.accept).fold((defaultLanguage, firstDomain))(details => domains.find(_.isDefinedAt(details.lensNames)).map(d => (details.lensDlsName, d)).getOrElse(throw cannotRespondException(xingyiHeader, details)))

  def cannotRespondException(xingyiHeader: Option[String], details: XingYiHeaderDetails) =
    new CannotRespondToQuery(xingyiHeader, details, DomainDetails.stringsToString(details.lensNames), domains.map(d => (d.name, d.lensNames, details.lensNames -- d.lensNames)))

}
