package one.xingyi.core.json
import java.util.Optional

import one.xingyi.core.optics.Lens

trait JsonParserWriter [J] extends JsonParser [J] with JsonWriter[J] {

  def lensToChild(childname: String): Lens[J, J]
  def lensToString(name: String): Lens[J, String]
  def lensToDouble(name: String): Lens[J, Double]
  def lensToInteger(name: String): Lens[J, Int]
  def lensToList[T](name: String, primitiveClassName: String): Lens[J, List[T]]
  def lensToOptional[T](name: String, primitiveClassName: String): Lens[J, Optional[T]]

}
