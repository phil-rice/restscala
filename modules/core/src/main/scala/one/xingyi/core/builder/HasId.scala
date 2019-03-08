package one.xingyi.core.builder

trait HasId[T, ID] extends (T => ID)
object HasId {
  implicit def fromLens[T, Id](implicit lens: IdLens[T, Id]): HasId[T, Id] = { t => lens.get(t) }
}