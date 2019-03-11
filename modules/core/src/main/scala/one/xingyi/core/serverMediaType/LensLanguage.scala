package one.xingyi.core.serverMediaType
import one.xingyi.core.codemaker.{CodeFragment, LensCodeMaker}
import one.xingyi.core.crypto.Digestor
import scala.language.implicitConversions
import scala.annotation.implicitNotFound


/** For example Javascript or the LensDsl */
trait LensLanguage {
  /** This will be part of the accept header. Examples are 'javascript' and 'lensdel' */
  def name: String;
  /** This is the mediatype of the returned code. Examples are 'application/javascript' and 'application/lensdsl' */
  def mediaType: String = "application/" + name
}

@implicitNotFound("""There must be a LensCodeMaker[$L] in scope""")
case class LensLanguageAndCodeMaker[L <: CodeFragment with LensLanguage](lensLanguage: L, lensCodeMaker: LensCodeMaker[L]) {
  def matches(name: String) = lensLanguage.name.equalsIgnoreCase(name)
}

object LensLanguageAndCodeMaker {
  implicit def fromLensLanguage[L <: CodeFragment with LensLanguage](lensLanguage: L)(implicit lensCodeMaker: LensCodeMaker[L]) = LensLanguageAndCodeMaker(lensLanguage, lensCodeMaker)
}
@implicitNotFound("""There must be a LensLanguages in scope on the server so that the server can work out how to code up the lens.""")
class LensLanguages(list: List[LensLanguageAndCodeMaker[_]]) {
  def find(name: String): Option[LensLanguageAndCodeMaker[_]] = list.find(_.matches(name))
}
