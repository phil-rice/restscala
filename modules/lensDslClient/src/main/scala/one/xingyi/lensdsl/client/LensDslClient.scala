package one.xingyi.lensdsl.client
import one.xingyi.core.client.ClientLanguage

trait LensDslClient extends ClientLanguage{
  def language = "lensdsl"
}
object LensDslClient extends LensDslClient
