package one.xingyi.core.endpoint
import one.xingyi.core.http.{Method, ServiceRequest}
import one.xingyi.core.strings.Strings

trait MatchesServiceRequest {
  def method: Method

  def apply(endpointName: String)(serviceRequest: ServiceRequest): Boolean
}


object MatchesServiceRequest {
  def fixedPath(method: Method) = FixedPathAndVerb(method)
  def idAtEnd(method: Method) = IdAtEndAndVerb(method)
  def prefixIdCommand(method: Method, command: String) = PrefixThenIdThenCommand(method, command)
}

case class FixedPathAndVerb(method: Method) extends MatchesServiceRequest {
  override def apply(endpointName: String)(serviceRequest: ServiceRequest): Boolean =
    serviceRequest.method == method && serviceRequest.uri.path.asUriString == endpointName
}

case class IdAtEndAndVerb(method: Method) extends MatchesServiceRequest {
  val startFn = Strings.allButlastSection("/") _

  override def apply(endpointName: String)(serviceRequest: ServiceRequest): Boolean = {
    val methodMatch = serviceRequest.method == method
    val startString = startFn(serviceRequest.uri.path.asUriString)
    val start = startString == endpointName
    methodMatch && start
  }
}


case class PrefixThenIdThenCommand(method: Method, command: String) extends MatchesServiceRequest {

  override def apply(endpointName: String)(serviceRequest: ServiceRequest): Boolean = {
    try {
      val path = serviceRequest.path.asUriString
      //    println("path: " + path + ", endpoint name: " + endpointName)
      Strings.startsWithAndSnips(endpointName)(path).map { rest =>
        val id = Strings.allButlastSection("/")(rest)
        val actualCommand = Strings.lastSection("/")(rest)
        //      println("id: " + id + ",  command: " + command)
        val result = id.indexOf("/") == -1 && actualCommand == command && serviceRequest.method == method
        //      println("result: " + result)
        result
      }.getOrElse(false)
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"problem in $this", e)
    }
  }
}

