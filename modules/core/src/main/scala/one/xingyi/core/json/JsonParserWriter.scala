package one.xingyi.core.json
import java.util.Optional

import one.xingyi.core.optics.Lens

trait JsonParserWriter[J] extends JsonParser[J] with JsonWriter[J] {

  def lensToChild(childname: String): Lens[J, J]
//  def lensToObject[T](getter: J =>T, setter: (J,T) => J): Lens[J, T]
  def lensToString: Lens[J, String]
  def lensToDouble: Lens[J, Double]
  def lensToInteger: Lens[J, Int]
  def lensToBoolean: Lens[J, Boolean]
  def lensToList: Lens[J, List[J]]
//  def lensToOptional[J](childName): Lens[J, Optional[T]]

}


class NullJsonparserWriter [J]extends JsonParserWriter[J] {
  override def lensToChild(childname: String): Lens[J, J] = ???
  override def lensToString: Lens[J, String] = ???
  override def lensToDouble: Lens[J, Double] = ???
  override def lensToInteger: Lens[J, Int] = ???
  override def lensToList: Lens[J, List[J]] = ???
  override def toJ(jsonValue: JsonValue): J = ???
  override def toStringForJ: J => String = ???
  override def apply(json: String): J = ???
  override def extractInt(j: J): Int = ???
  override def extractDouble(j: J): Double = ???
  override def extractBoolean(j: J): Boolean = ???
  override def extractString(j: J): String = ???
  override def extractOptString(j: J): Option[String] = ???
  override def asList(j: J): List[J] = ???
  override def \(j: J, s: String): J = ???
  override def asListOf[T](j: J, mirrorFn: J => T): List[T] = ???
  override def lensToBoolean: Lens[J, Boolean] = ???
}