/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.codemaker

import one.xingyi.core.endpoint.{EndpointKleisli, MatchesServiceRequest}
import one.xingyi.core.http._
import one.xingyi.core.id.HasId
import one.xingyi.core.json.{JsonParser, JsonWriter, ObjectProjection, ProofOfBinding}
import one.xingyi.core.language.AnyLanguage._
import one.xingyi.core.language.MicroserviceComposers
import one.xingyi.core.monad.{LiftFunctionKleisli, MonadCanFailWithException}
import one.xingyi.core.script._
import one.xingyi.core.serverMediaType.{DomainList, LensLanguage, LensLanguages}

import scala.language.higherKinds


class EntityCodeMaker[M[_], Fail, SharedE, DomainE](implicit val monad: MonadCanFailWithException[M, Fail], failer: SimpleFailer[Fail],
                                                    domainList: DomainList[SharedE, DomainE],
                                                    entityPrefixFinder: EntityPrefix[DomainE],
                                                    proof: ProofOfBinding[SharedE, DomainE]) extends LiftFunctionKleisli[M] with MicroserviceComposers[M] with EndpointKleisli[M] {

  val entityPrefix = entityPrefixFinder()

  def hashMap = domainList.domains.map(_.code).
    foldLeft(Map[String, CodeDetailsResponse]()) { (acc, map) => map.foldLeft(acc) { case (m, (frag, details)) => m + (details.hash -> CodeDetailsResponse(details.code, frag.mediaType)) } }

  def allCode: CodeRequest => CodeResponse[SharedE, DomainE] = _ => CodeResponse(domainList)

  def codeDetails: CodeDetailsRequest => M[CodeDetailsResponse] = cr =>
    hashMap.get(cr.hash).fold(monad.fail[CodeDetailsResponse](failer.simpleNotFound("Cannot find hash. values are" + hashMap.keys.toList.sorted)))(_.liftM[M])

  def allCodeEndpoint[J: JsonWriter] = function(s"$entityPrefix.allCode")(allCode) |+| endpoint[CodeRequest, CodeResponse[SharedE, DomainE]](s"/$entityPrefix/code", MatchesServiceRequest.fixedPath(Method("get")))
  def codeDetailsEndpoint[J: JsonWriter] = codeDetails |+| endpoint[CodeDetailsRequest, CodeDetailsResponse](s"/$entityPrefix/code", MatchesServiceRequest.idAtEnd(Method("get")))

  def endpoints[J: JsonWriter] = List(allCodeEndpoint, codeDetailsEndpoint)
}


class EntityMaker[M[_], Fail, SharedE, DomainE: Links](methods: List[Method],defaultLangauge: LensLanguage)
                                                      (implicit val monad: MonadCanFailWithException[M, Fail],
                                                       failer: SimpleFailer[Fail],
                                                       editEntityFailer: EditEntityRequestFailer[Fail],
                                                       domainList: DomainList[SharedE, DomainE],
                                                       lensLanguages: LensLanguages,
                                                       entityPrefixFinder: EntityPrefix[DomainE],
                                                       hasId: HasId[DomainE, String],
                                                       proof: ProofOfBinding[SharedE, DomainE]) extends LiftFunctionKleisli[M] with MicroserviceComposers[M] with EndpointKleisli[M] {
  val entityPrefix = entityPrefixFinder()
  import one.xingyi.core.language.MonadLanguage._

  val details = function[EntityServiceFinderRequest, EntityServiceFinderResponse](s"$entityPrefix.persondetails")(
    req => EntityServiceFinderResponse(req.host, s"http://${req.host}/code/<hash>", s"http://${req.host}/$entityPrefix/<id>", methods.map(_.toString), domainList, lensLanguages)
  )
  def detailsEndpoint[J: JsonWriter] =
    details |+| endpoint[EntityServiceFinderRequest, EntityServiceFinderResponse](s"/$entityPrefix", MatchesServiceRequest.fixedPath(Method("get")))

  def makeServerPayload[Req](xingYiHeader: Option[String], dom: DomainE) =
    ServerPayload(Status(200), dom, domainList.accept(xingYiHeader, defaultLangauge))

  def getFn(fn: String => M[DomainE]) = { req: GetEntityRequest => fn(req.id).map(dom => makeServerPayload(req.xingYiHeader, dom)) }
  def getEndpoint[J: JsonWriter](fn: String => M[DomainE])(implicit project: ObjectProjection[SharedE, DomainE]): ServiceRequest => M[Option[ServiceResponse]] =
    getFn(fn) |+| endpoint[GetEntityRequest, ServerPayload[SharedE, DomainE]](s"/$entityPrefix", MatchesServiceRequest.idAtEnd(Method("get")))

  def newFn(fn: String => M[DomainE]) = { req: GetEntityRequest => fn(req.id).map { dom => makeServerPayload(req.xingYiHeader, dom) } }
  def newEntity[J: JsonWriter](fn: String => M[DomainE])(implicit project: ObjectProjection[SharedE, DomainE]) =
    newFn(fn) |+| endpoint[GetEntityRequest, ServerPayload[SharedE, DomainE]](s"/$entityPrefix", MatchesServiceRequest.idAtEnd(Method("post")))

  def editFn(fn: (String, DomainE) => M[DomainE]) = { req: EditEntityRequest[DomainE] => fn(hasId(req.entity), req.entity).map(newDom => makeServerPayload(req.acceptHeader, newDom)) }
  def editEndpoint[J: JsonWriter : JsonParser](fn: (String, DomainE) => M[DomainE])(implicit project: ObjectProjection[SharedE, DomainE], links: Links[DomainE]) =
    editFn(fn) |+| endpoint[EditEntityRequest[DomainE], ServerPayload[SharedE, DomainE]](s"/$entityPrefix", MatchesServiceRequest.idAtEnd(Method("put")))
}

