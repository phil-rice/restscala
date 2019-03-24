package one.xingyi.lensdsl.client

import one.xingyi.core.crypto.Codec
import one.xingyi.core.json.{JsonParser, JsonParserWriter, JsonParsingException}
import one.xingyi.core.optics.Lens
import one.xingyi.core.simpleList.ISimpleList


trait ILensStoreOps[Mirror] {
  def stringLens(lensName: String): Lens[Mirror, String]
  def integerLens(lensName: String): Lens[Mirror, Int]
  def doubleLens(lensName: String): Lens[Mirror, Double]
  def booleanLens(lensName: String): Lens[Mirror, Boolean]
  def listLens[T](lensName: String)(implicit codec: Codec[T, Mirror]): Lens[Mirror, List[T]]
  def objectlens[T](lensName: String)(implicit codec: Codec[T, Mirror]): Lens[Mirror, T]
}
trait ILensStore[Mirror] extends ILensStoreOps[Mirror]

class LensDslParsingException(msg: String, cause: Throwable) extends RuntimeException(msg, cause)
object ILensStore {

  def wrap[X](msg: String, block: => X): X = try {
    block
  } catch {
    case e: Exception => throw new LensDslParsingException(msg, e)
  }

  def apply[Mirror: JsonParserWriter](s: String)(implicit lensLensParser: LensLineParser): ILensStore[Mirror] =
    wrap(s, apply(s.split("\n").map(_.trim).map(lensLensParser.apply).toList))
  def apply[Mirror: JsonParserWriter](list: List[LensLine]): ILensStore[Mirror] =
    SimpleLensStore(list.foldLeft(Map[String, Lens[Mirror, _]]())((acc, ll) => acc + (ll.name -> ll.toLens)))
}


case class SimpleLensStore[Mirror](lines: Map[String, Lens[Mirror, _]])(implicit jsonParserWriter: JsonParserWriter[Mirror]) extends ILensStore[Mirror] {

  override def stringLens(lensName: String): Lens[Mirror, String] = lines(lensName).asInstanceOf[Lens[Mirror, String]]
  override def integerLens(lensName: String): Lens[Mirror, Int] = lines(lensName).asInstanceOf[Lens[Mirror, Int]]
  override def doubleLens(lensName: String): Lens[Mirror, Double] = lines(lensName).asInstanceOf[Lens[Mirror, Double]]
  override def booleanLens(lensName: String): Lens[Mirror, Boolean] = lines(lensName).asInstanceOf[Lens[Mirror, Boolean]]
  //  override def listLens[T](lensName: String, maker: Mirror => T, getter: T => Mirror): Lens[Mirror, List[T]] =
  override def listLens[T](lensName: String)(implicit codec: Codec[T, Mirror]): Lens[Mirror, List[T]] =
    Lens.asListLens(lines(lensName).asInstanceOf[Lens[Mirror, List[Mirror]]])
  override def objectlens[T](lensName: String)(implicit codec: Codec[T, Mirror]): Lens[Mirror, T] =
    lines(lensName).asInstanceOf[Lens[Mirror, Mirror]] andThen codec.backwardsLens
}
