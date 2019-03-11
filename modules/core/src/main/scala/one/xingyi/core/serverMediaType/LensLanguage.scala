package one.xingyi.core.serverMediaType
import one.xingyi.core.codemaker.{LensCodeMaker, MediaType}
import one.xingyi.core.strings.Strings

import scala.annotation.implicitNotFound
import scala.language.implicitConversions
import scala.reflect.ClassTag


/** For example Javascript or the LensDsl */
trait LensLanguage {
  /** This will be part of the accept header. Examples are 'javascript' and 'lensdel' */
  def name: String;
  /** This is the mediatype of the returned code. Examples are 'application/javascript' and 'application/lensdsl' */
  def mediaType: MediaType
  override def toString: String = Strings.removeOptional$(getClass.getSimpleName)
}

@implicitNotFound("""There must be a LensCodeMaker[$L] in scope""")
case class LensLanguageAndCodeMaker[L <: LensLanguage](lensLanguage: L, lensCodeMaker: LensCodeMaker[L]) {
  def matches(name: String) = lensLanguage.name.equalsIgnoreCase(name)
  def lensL: LensLanguage = lensLanguage
  def name: String = lensLanguage.name
  def languageAndDetails[SharedE, DomainE: ClassTag](defn: DomainDefn[SharedE, DomainE]): (LensLanguage, CodeDetails) = {
    (lensLanguage, CodeDetails(lensCodeMaker.apply(defn)));
  }
}

object LensLanguageAndCodeMaker {
  implicit def fromLensLanguage[L <: LensLanguage](lensLanguage: L)(implicit lensCodeMaker: LensCodeMaker[L]) = LensLanguageAndCodeMaker(lensLanguage, lensCodeMaker)
}
@implicitNotFound("""There must be a LensLanguages in scope on the server so that the server can work out how to code up the lens.""")
case class LensLanguages(list: List[LensLanguageAndCodeMaker[_]] = List()) {
//  def withLanguage[L <: LensLanguage](l: L)(implicit lensCodeMaker: LensCodeMaker[L]) = LensLanguages(list :+ LensLanguageAndCodeMaker.fromLensLanguage(l))
  val legalValues = list.map(_.name)

  def find(name: String): Option[LensLanguageAndCodeMaker[_]] = list.find(_.matches(name))

  def toMap[SharedE, DomainE: ClassTag](defn: DomainDefn[SharedE, DomainE]): Map[LensLanguage, CodeDetails] = list.map(_.languageAndDetails(defn)).toMap

}
