package one.xingyi.lensdsl.client

import one.xingyi.core.crypto.Codec
import one.xingyi.core.json.{JsonParser, JsonParserWriter}
import one.xingyi.core.optics.{Lens, LensLineParser}
import one.xingyi.core.script.{Domain, IXingYi, IXingYiLoader, ViewNamesToViewLens}

object LensDslXingYiLoader {
  implicit def loader[J](implicit json: JsonParserWriter[J], lensLensParser: LensLineParser, viewNamesToViewLens: ViewNamesToViewLens): IXingYiLoader =
    dsl => new LensDslXingYi[J](ILensStore.apply(dsl))
}

class LensDslXingYi[J](lensStore: ILensStore[J])(implicit json: JsonParserWriter[J]) extends IXingYi {
  override def parse[T <: Domain](s: String)(implicit codec: Codec[T, Object]) = codec.backwards(json(s).asInstanceOf[Object])

  override protected def rawRender(name: String, t: Object): String = name match {
    case "json" => json.toStringForJ(t.asInstanceOf[J])
  }
  override def stringLens[T <: Domain](name: String)(implicit codec: Codec[T, Object]): Lens[T, String] =
    codec.lens andThen Lens.cast[Object, J] andThen lensStore.stringLens(name)

  override def objectLens[T1 <: Domain, T2 <: Domain](name: String)(implicit maker1: Codec[T1, Object], maker2: Codec[T2, Object]): Lens[T1, T2] =
    maker1.lens andThen Lens.cast[Object, J] andThen lensStore.objectlens(name)(codec = maker2.asInstanceOf[Codec[T2, J]])

  override def listLens[T1 <: Domain, T2 <: Domain](name: String)(implicit maker1: Codec[T1, Object], maker2: Codec[T2, Object]): Lens[T1, List[T2]] =
    maker1.lens andThen Lens.cast[Object, J] andThen lensStore.listLens(name)(codec = maker2.asInstanceOf[Codec[T2, J]])
}
