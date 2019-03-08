package one.xingyi.core.id
import one.xingyi.core.optics.Lens

trait HasId[T, ID] extends (T => ID)
object HasId {
  implicit def fromLens[T, Id](implicit lens: IdLens[T, Id]): HasId[T, Id] = { t => lens.get(t) }
}

case class IdLens[T, ID](get: T => ID, set: (T, ID) => T) extends Lens[T, ID]


trait CopyWithNewId[T, ID] extends ((ID, T) => T)
object CopyWithNewId {
  implicit def fromLens[T, Id](implicit lens: IdLens[T, Id]): CopyWithNewId[T, Id] = { (id, t) => lens.set(t, id) }
}