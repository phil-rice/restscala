package one.xingyi.core.json
import java.util.Optional

import one.xingyi.core.optics.Lens

trait JsonParserWriter[J] extends JsonParser[J] with JsonWriter[J] {

  def lensToChild(childname: String): Lens[J, J]
  def lensToString: Lens[J, String]
  def lensToDouble: Lens[J, Double]
  def lensToInteger: Lens[J, Int]
  def lensToList[T](primitiveClassName: String, tMaker: J => T, jMaker: T => J): Lens[J, List[T]]
  def lensToOptional[J](childName): Lens[J, Optional[T]]

}
