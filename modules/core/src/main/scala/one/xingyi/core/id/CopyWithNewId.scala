package one.xingyi.core.id

trait CopyWithNewId[T, ID] extends ((ID, T) => T)
object CopyWithNewId {
  implicit def fromLens[T, Id](implicit lens: IdLens[T, Id]): CopyWithNewId[T, Id] = { (id, t) => lens.set(t, id) }
}