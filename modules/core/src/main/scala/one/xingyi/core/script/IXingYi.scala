/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.script

import javax.script.{Invocable, ScriptEngine}
import one.xingyi.core.crypto.Codec
import one.xingyi.core.http._
import one.xingyi.core.id.HasId
import one.xingyi.core.json._
import one.xingyi.core.optics.Lens
import one.xingyi.core.serverMediaType.{DomainDetails, LensLanguage}

import scala.language.postfixOps


trait IXingYiLoader {
  def apply(s: String): IXingYi
}

trait Domain {
  def mirror: Object
}
object Domain {
  implicit def codec[Dom <: Domain](implicit domainMaker: DomainMaker[Dom]) = new Codec[Dom, Object] {
    override def forwards: Dom => Object = _.mirror
    override def backwards: Object => Dom = domainMaker.create
  }
}

trait DomainMaker[Dom <: Domain] {
  def create(mirror: Object): Dom
}

case class ClientPreferedLanguage(s: String)

trait ServerDomain {
  def lens: List[String]

  val lensString = lens.mkString(",")

  def contentType(implicit language: ClientPreferedLanguage): String = s"application/xingyi.${language.s}.$lensString"

}

trait EntityPrefix[T] {
  def apply(): String
}
case class LinkDetail(verb: String, urlPattern: String)

trait Links[T] extends (T => List[LinkDetail])

case class ServerPayload[SharedE, DomainE](status: Status, domainObject: DomainE, domain: DomainDetails[SharedE, DomainE], lensLanguage: LensLanguage)(implicit val links: Links[DomainE])

trait ToContentType[Req] {
  def apply(req: Req): String
}

trait HasHost[T] {
  def apply(t: T): String
}

object ServerPayload extends JsonWriterLanguage {
  def apply[SharedE, DomainE](status: Status, domainObject: DomainE, langAndDetails: (LensLanguage, DomainDetails[SharedE, DomainE]))(implicit links: Links[DomainE]): ServerPayload[SharedE, DomainE] =
    ServerPayload(status, domainObject, langAndDetails._2, langAndDetails._1)

  implicit def toServerResponse[J, Req, SharedE, DomainE]
  (implicit jsonWriter: JsonWriter[J], entityPrefix: EntityPrefix[DomainE],
   hasId: HasId[Req, String], hasHost: HasHost[Req],
   projection: Projection[SharedE, DomainE], toContentType: ToContentType[Req]): ToServiceResponse[Req, ServerPayload[SharedE, DomainE]] = { req =>
    payload =>
      val host = hasHost(req)
      ServiceResponse(payload.status, Body(
        s"http://$host/${entityPrefix()}/code/" + payload.domain.code(payload.lensLanguage).hash + "\n" +
          jsonWriter(projection.toJson(payload.domainObject) |+| ("_links" ->
            JsonObject(payload.links(payload.domainObject).map {
              case LinkDetail(verb, pattern) => verb -> JsonString(pattern.replace("<id>", hasId(req)).replace("<host>", host))
            }: _*)))),
        List(Header("content-type", toContentType(req)), Header("xingyi", payload.domain.name)))
  }
}

trait IXingYi {
  def parse[T <: Domain](s: String)(implicit codec: Codec[T, Object]): T

  protected def rawRender(name: String, t: Object): String

  def stringLens[T <: Domain](name: String)(implicit codec: Codec[T, Object]): Lens[T, String]

  def objectLens[T1 <: Domain, T2 <: Domain](name: String)(implicit codec1: Codec[T1, Object], codex2: Codec[T2, Object]): Lens[T1, T2]

  def listLens[T1 <: Domain, T2 <: Domain](name: String)(implicit codec1: Codec[T1, Object], codex2: Codec[T2, Object]): Lens[T1, List[T2]]

  def render(name: String, t: Domain): String = rawRender(name, t.mirror)
}

class XingYiExecutionException(msg: String, cause: Throwable) extends RuntimeException(msg, cause)
