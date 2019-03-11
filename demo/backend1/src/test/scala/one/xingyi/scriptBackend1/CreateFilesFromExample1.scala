/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.scriptBackend1

import java.io.File

import one.xingyi.core.UtilsSpec
import one.xingyi.core.codemaker.{DomainCD, DomainDefnToCodeDom, LensCodeMaker, ToScalaCode}
import one.xingyi.core.script._
import one.xingyi.core.serverMediaType.Javascript
import one.xingyi.core.strings.Files
import one.xingyi.scriptModel1.IPerson

class CreateFilesFromExample1 extends UtilsSpec {

  val scalaFile = new File("modules/website/src/main/scala/one/xingyi/scriptWebsite/createdCode1/Example1.scala").getAbsoluteFile
  val javascriptFile = new File("modules/website/src/main/resources/example1.js").getAbsoluteFile
  behavior of "Create The Example Domain Classes"


  val isCorrectDirectory = scalaFile.getPath.replace('/', '\\').endsWith("restscala\\modules\\website\\src\\main\\scala\\one\\xingyi\\scriptWebsite\\createdCode1\\Example1.scala")
  it should "be talking to the correct directory" in {
    checkCorrectDirectory
  }

  def checkCorrectDirectory = {
    withClue(s"directory is $scalaFile")(isCorrectDirectory shouldBe true)
  }

  it should "make the javascript" in {
    checkCorrectDirectory

    val codeMaker = implicitly[LensCodeMaker[Javascript]]
    val javascript = codeMaker.apply(new Model1Defn)
    Files.printToFile(javascriptFile)(pw => pw.print(javascript))
  }

  //  it should "make the scala" ignore {
  //    ToScalaCode.makeScalaCode[IPerson, Person]
  //    val codeMaker = implicitly[ToScalaCode[DomainDefn[IPerson, Person]]]
  //    val scala = codeMaker(new Model1Defn)
  //    Files.printToFile(scalaFile)(pw => pw.print(scala))
  //  }
  it should "make the scala new way" in {
    val domainCd = implicitly[DomainDefnToCodeDom].apply(new Model1Defn)
    val scala = implicitly[ToScalaCode[DomainCD]] apply domainCd
    Files.printToFile(scalaFile)(pw => pw.print(scala))
  }

}
