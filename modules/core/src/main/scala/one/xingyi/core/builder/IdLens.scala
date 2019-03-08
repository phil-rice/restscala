package one.xingyi.core.builder
import one.xingyi.core.optics.Lens

case class IdLens[T, ID](get:T => ID, set: (T, ID) => T) extends Lens[T, ID]
