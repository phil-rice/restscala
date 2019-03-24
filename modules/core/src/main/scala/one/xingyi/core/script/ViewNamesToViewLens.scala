package one.xingyi.core.script
import one.xingyi.core.optics.Lens

/** This is the (on the client) registered list of names to the lens that will create the client side object
  * The Lens are from the client side object to the mirror that represents the data from the server
  */
class ViewNamesToViewLens(map: Map[String, Lens[_, _]]) {
  def legalValues = map.keySet.toList.sorted
  def contains(name: String) = map.contains(name)
  def apply(name: String) = map(name)

}
