/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.scriptBackend2

import one.xingyi.core.script.{AbstractEntityServiceFinderRequestTest, EditEntityRequestTest, EntityRequestTest}
import one.xingyi.json4s.Json4sParser._
import one.xingyi.json4s.Json4sWriter._
import one.xingyi.scriptModel2.IPerson
import org.json4s.JsonAST.JValue


class EditEntityRequestModel2Test extends EditEntityRequestTest[JValue, IPerson, Person] {
  override def dom: Person = Person(id1, Address("someLine1", "someLine2", "pc"), Telephone("someTelNo"))
}

class EntityRequestModel2Test extends EntityRequestTest[JValue, IPerson, Person]

class EntityServiceFinderRequestModel2Test extends AbstractEntityServiceFinderRequestTest[JValue]
