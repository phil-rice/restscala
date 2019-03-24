package one.xingyi.core.optics
import one.xingyi.core.json.JsonParserWriter


case class LensLine(name: String, defns: List[LensDefn[_, _]]) {

  def toLens[J: JsonParserWriter, To]: Lens[J, To] = recurse[J](defns).asInstanceOf[Lens[J, To]]


  def joinIgnoringTypesInAnEvilWay[A, B, C, D](lens1: Lens[A, B], lens2: Lens[C, D]) = lens1 andThen lens2.asInstanceOf[Lens[B, D]]
  private def recurse[J: JsonParserWriter](defns: List[LensDefn[_, _]]): Lens[_, _] = {
    val head = defns.headOption.getOrElse(throw new RuntimeException("Must have at least one lens in a lens defn for name '$name'"))
    val headLens = head match {
      case j: JsonLensDefn[_, _] => j.asInstanceOf[JsonLensDefn[J, _]].lens
      case s: SimpleLensDefn[_, _] => s.lens
      case v: ViewLensDefn[_, _] => v.lens
    }
    defns.tail match {
      case Nil => headLens
      case tail => joinIgnoringTypesInAnEvilWay(headLens, recurse(tail))
    }
  }

}


