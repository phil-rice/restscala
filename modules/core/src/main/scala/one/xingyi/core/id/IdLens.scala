package one.xingyi.core.id
import one.xingyi.core.optics.Lens

case class IdLens[T, ID](get:T => ID, set: (T, ID) => T) extends Lens[T, ID]
