/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.script
import one.xingyi.core.UtilsSpec
import one.xingyi.core.exceptions.CannotRespondToQuery
import one.xingyi.core.serverMediaType.{IXingYiHeaderToLensNames, LensLanguage, XingYiHeaderDetails}
import org.mockito.Mockito._
abstract class DomainListTest[L <: LensLanguage] extends UtilsSpec with ScriptFixture[L] {


  //TODO Let's have a second lens language for test

  behavior of "DomainList"

  it should "aggregate the passed in domains" in {
    domainList.domains shouldBe List(details1, details2)
  }

  behavior of "DomainList selecting the right details"

  it should "select the first if no lens are specified" in {
    implicit val toLensNames = mock[IXingYiHeaderToLensNames]
    when(toLensNames.accept("someAcceptHeader")) thenReturn Some(XingYiHeaderDetails(defaultLensLanguage, Set()))
    domainList.accept(Some("someAcceptHeader"), defaultLensLanguage) shouldBe(defaultLensLanguage, details1)
  }

  it should "select the first if it matches" in {
    implicit val toLensNames = mock[IXingYiHeaderToLensNames]
    when(toLensNames.accept("someAcceptHeader")) thenReturn Some(XingYiHeaderDetails(defaultLensLanguage, details1.lensNames))
    domainList.accept(Some("someAcceptHeader"), defaultLensLanguage) shouldBe(defaultLensLanguage, details1)
  }

  it should "select the second if the first doesn't match but the second does" in {
    implicit val toLensNames = mock[IXingYiHeaderToLensNames]
    when(toLensNames.accept("someAcceptHeader")) thenReturn Some(XingYiHeaderDetails(defaultLensLanguage, details2.lensNames))
    domainList.accept(Some("someAcceptHeader"), defaultLensLanguage) shouldBe(defaultLensLanguage, details2)
  }

  it should "blow up if it can't match" in {
    implicit val toLensNames = mock[IXingYiHeaderToLensNames]
    when(toLensNames.accept("someAcceptHeader")) thenReturn Some(XingYiHeaderDetails(defaultLensLanguage, Set("some", "rubbish", "names")))
    intercept[CannotRespondToQuery](domainList.accept(Some("someAcceptHeader"), defaultLensLanguage)).getMessage.noWhiteSpace shouldBe
    s"""Header[Some(someAcceptHeader)]
       | normalised[names,rubbish,some],
       | language: ${defaultLensLanguage.name}
       | headerAsSet: Set(some, rubbish, names)
       | failures:
       | Domain ParentDomainForTest1
       |   Allowed: lens_house_postcode_string,lens_parent_house_house,lens_parent_children_childlist,lens_house_houseno_string,lens_parent_name_string,lens_child_name_string
       |   Failed: some,rubbish,names
       |;Domain ParentDomainForTest2
       |   Allowed: lens_house_postcode_string,lens_parent_house_house,lens_person_postcode_string,lens_parent_children_childlist,lens_house_houseno_string,lens_parent_name_string,lens_child_name_string
       |   Failed: some,rubbish,names
     """.stripMargin.noWhiteSpace
  }

}
