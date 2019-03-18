package one.xingyi.lensdsl.client

import one.xingyi.core.json.{JsonParser, JsonParserWriter}
import one.xingyi.core.optics.Lens
import one.xingyi.core.simpleList.ISimpleList

trait ILensStoreOps[Mirror] {
  def stringLens(lensName: String): Lens[Mirror, String]
  def integerLens(lensName: String): Lens[Mirror, Int]
  def doubleLens(lensName: String): Lens[Mirror, Double]
  def booleanLens(lensName: String): Lens[Mirror, Boolean]
  def listLens(lensName: String): Lens[Mirror, List[Mirror]]
  def simpleListLens[T](lensName: String): Lens[Mirror, ISimpleList[T]]

}
trait ILensStore[Mirror] extends ILensStoreOps[Mirror]

object ILensStore {
  def apply[Mirror:JsonParserWriter](s: String)(implicit lensLensParser: LensLineParser): ILensStore[Mirror] =
    apply(s.split("\n").map(lensLensParser.apply).toList)

  def apply[Mirror: JsonParserWriter](list: List[LensLine]): ILensStore[Mirror] =
    SimpleLensStore(list.foldLeft(Map[String, Lens[Mirror, _]]())((acc, ll) => acc + (ll.name -> ll.toLens)))
}

case class SimpleLensStore[Mirror](lines: Map[String, Lens[Mirror, _]])(implicit jsonParserWriter: JsonParserWriter[Mirror]) extends ILensStore[Mirror] {

  override def stringLens(lensName: String): Lens[Mirror, String] = ???
  override def integerLens(lensName: String): Lens[Mirror, Int] = ???
  override def doubleLens(lensName: String): Lens[Mirror, Double] = ???
  override def booleanLens(lensName: String): Lens[Mirror, Boolean] = ???
  override def listLens(lensName: String): Lens[Mirror, List[Mirror]] = ???
  override def simpleListLens[T](lensName: String): Lens[Mirror, ISimpleList[T]] = ???
}
