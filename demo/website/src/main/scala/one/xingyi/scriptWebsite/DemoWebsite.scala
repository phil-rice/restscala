/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.scriptWebsite

import javax.net.ssl.SSLContext
import one.xingyi.cddmustache.{Mustache, NameToMustacheTemplate}
import one.xingyi.core.endpoint.{ChainKleisli, DisplayRecordedKleisli, EndpointKleisli, MatchesServiceRequest}
import one.xingyi.core.http._
import one.xingyi.core.json._
import one.xingyi.core.language.AnyLanguage._
import one.xingyi.core.language.MicroserviceComposers
import one.xingyi.core.logging._
import one.xingyi.core.monad._
import one.xingyi.core.objectify._
import one.xingyi.core.script.{Domain => _, _}
import one.xingyi.core.strings.ToHtml
import one.xingyi.javascript.client.{JavascriptClient, JavascriptXingYiLoader}
import one.xingyi.lensdsl.client.{LensDslClient, LensDslXingYi, LensDslXingYiLoader}
import one.xingyi.scriptExample.createdCode1.{Model1Defn, Person, PersonLine12Ops, TelephoneNumber}
import one.xingyi.simplewebframework.HttpClient
import one.xingyi.simplewebframework.simpleServer.CheapServer
import org.json4s.JValue

import scala.language.higherKinds

case class MustacheToHtml[J: JsonWriter, T](templateName: String, title: String)(implicit toJsonLib: ToJsonLib[T], nameToMustacheTemplate: NameToMustacheTemplate) extends ToHtml[T] {
  val mf = Mustache(title, templateName, "main.template.mustache")

  override def apply(t: T): String = mf.apply(JsonMaps(toJsonLib(t)))
}

class Website[M[_] : Async, Fail: Failer : LogRequestAndResult, J: JsonParser : JsonWriter]
(implicit val monad: MonadCanFailWithException[M, Fail] with MonadWithState[M],
 clientPreferedLanguage: ClientPreferedLanguages,
 xingyiLoader: IXingYiLoader,
 val failer: Failer[Fail],
 val detailedLoggingForSR: DetailedLogging[ServiceResponse],
 val logReqAndResult: LogRequestAndResult[Fail],
 loggingAdapter: LoggingAdapter)
  extends LiftFunctionKleisli[M] with MicroserviceComposers[M] with EndpointKleisli[M] with HttpKlesili[M]
    with XingyiKleisli[M, Fail] with RecordCallsKleisli[M, Fail] with DisplayRecordedKleisli[M] with ChainKleisli[M, Fail] {

  implicit val ssl: Option[SSLContext] = None
  private val domain: Domain = Domain(Protocol("http"), HostName("127.0.0.1"), Port(9001))
  override implicit lazy val httpFactory = (v1: ServiceName) => {
    val service = HttpClient.apply[M](domain);
    { req => service(req.addHeader("host", domain.host + ":" + domain.port)) }
  }

  private val backend: ServiceRequest => M[ServiceResponse] = http(ServiceName("Backend"))
  implicit val template = Mustache("Demo", "personAndRequests.mustache", "main.template.mustache")

  implicit val ToHtmlForIndex = MustacheToHtml[J, IndexPageResponse]("index.mustache", "Xing Yi demo")
  implicit val ToHtmlForRecordedCalls = MustacheToHtml[J, ResultWithRecordedCalls[ServiceResponse]]("personAndRequests.mustache", "Xing Yi demo")


  val keepalive: ServiceRequest => M[Option[ServiceResponse]] = sr => Option(ServiceResponse("Alive")).liftM

  implicit val personDetailsUrl: EntityDetailsUrl[Person] = EntityDetailsUrl(PersonAddressRequest.entityDetails.url)
  implicit val recordedCalls = LocalVariable[RecordedCall]
  val index = function[IndexPageRequest, IndexPageResponse]("index")(_ => IndexPageResponse()) |+| endpoint[IndexPageRequest, IndexPageResponse]("/", MatchesServiceRequest.fixedPath(Method("get")))

  val person = backend |+| recordCalls |+| xingyify[PersonAddressRequest, PersonAddressResponse](Model1Defn) |+| endpoint[PersonAddressRequest, PersonAddressResponse]("/person", MatchesServiceRequest.idAtEnd(Method("get"))) |+| andDisplayRecorded[J]

  val x: EditPersonRequest => M[EditPersonResponse] = backend |+| recordCalls |+| editXingYi[EditPersonRequest, Person, PersonLine12Ops, EditPersonResponse](Model1Defn, {
    (par, line12Ops) => (line12Ops.line1Lens.setFn(par.newLine1) andThen line12Ops.line2Lens.setFn(par.newLine2))
  })
  val editPersonPost = backend |+| recordCalls |+| editXingYi[EditPersonRequest, Person, PersonLine12Ops, EditPersonResponse](Model1Defn, {
    (editPersonRequest, line12Ops) => (line12Ops.line1Lens.setFn(editPersonRequest.newLine1) andThen line12Ops.line2Lens.setFn(editPersonRequest.newLine2))
  }) |+| endpoint[EditPersonRequest, EditPersonResponse]("/person", MatchesServiceRequest.prefixIdCommand(Method("post"), "edit")) |+| andDisplayRecorded[J]


  val editPersonForm = backend |+| recordCalls |+| xingyify[DisplayEditPersonFormRequest, DisplayEditPersonFormResponse](Model1Defn) |+| endpoint[DisplayEditPersonFormRequest, DisplayEditPersonFormResponse]("/person", MatchesServiceRequest.prefixIdCommand(Method("get"), "edit")) |+| andDisplayRecorded[J]
  //  val editPersonPost = backend |+| recordCalls |+| xingyify[EditPersonRequest, EditPersonResponse](Model1Defn) |+| endpoint[EditPersonRequest, EditPersonResponse]("/person", MatchesServiceRequest.prefixIdCommand(Method("post"), "edit")) |+| andDisplayRecorded[J]
  val endpoints: ServiceRequest => M[Option[ServiceResponse]] = chain(index, person, editPersonPost, editPersonForm, keepalive)

}

object Website extends App {

  import one.xingyi.json4s.Json4sParserWriter._
  import LensDslXingYiLoader._

  implicit val logger: LoggingAdapter = PrintlnLoggingAdapter

  import SimpleLogRequestAndResult._

  println("Checking backend")

  implicit val clientPreferedLanguage = ClientPreferedLanguages(List(LensDslClient, JavascriptClient))
  implicit val viewNamesToViewLens = new ViewNamesToViewLens(Map(
    "person" -> Person.forJson.lens,
    "telephonenumber" -> TelephoneNumber.default.lens
  ))


  LensDslXingYiLoader.loader[JValue]
  val website = new Website[IdentityMonad, Throwable, JValue]
  val server = new CheapServer[IdentityMonad, Throwable](9000, website.endpoints)
  println("running")
  server.start
}
