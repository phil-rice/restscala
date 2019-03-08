/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
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

