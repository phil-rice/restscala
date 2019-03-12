/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.script

import one.xingyi.core.script.{DomainListDDTest, DomainListTest}
import one.xingyi.javascript.server.Javascript
import one.xingyi.json4s.Json4sWriter._
import one.xingyi.lensdsl.server.{LensDsl, LensDslScriptFixture}
import one.xongyi.javascript.server.JavascriptScriptFixture
import org.json4s.JValue

class DomainListDDJ4SJAvascriptTest extends DomainListDDTest[JValue, Javascript] with JavascriptScriptFixture {

}
class DomainListDDJ4SLensDslTest extends DomainListDDTest[JValue, LensDsl] with LensDslScriptFixture {

}

class DomainListLensDslTest extends DomainListTest[LensDsl] with LensDslScriptFixture {

}
class DomainListJavascriptTest extends DomainListTest[Javascript] with JavascriptScriptFixture {

}
