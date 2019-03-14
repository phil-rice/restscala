package one.xingyi.lensdsl.client

import java.util

import one.xingyi.core.UtilsSpec

class LensLanguageTest extends UtilsSpec {
   val lensParser = implicitly[LensValueParser]
      val lineParser = implicitly[LensLineParser]

  behavior of "LensvalueParser"


  it should "parser single items" in {
    lensParser("line1/string") shouldBe List(StringLensDefn("line1"))
    lensParser("line1/integer") shouldBe List(IntegerLensDefn("line1"))
    lensParser("line1/double") shouldBe List(DoubleLensDefn("line1"))
    lensParser("a/address") shouldBe List(ViewLensDefn("a", "address"))
    lensParser("a/*address") shouldBe List(ListLensDefn("a", "address"))
    lensParser("{firstItem}") shouldBe List(FirstItemInListDefn())
    lensParser("{identity}") shouldBe List(IdentityDefn())
    lensParser("{itemAsList}") shouldBe List(ItemAsListDefn())
  }

  it should "item and child" in {
    lensParser.apply("child/childClass,line1/string") shouldBe List(ViewLensDefn("child", "childClass"), StringLensDefn("line1"))
    lensParser.apply("child/childClass,line1/integer") shouldBe List(ViewLensDefn("child", "childClass"), IntegerLensDefn("line1"))
    lensParser.apply("child/childClass,line1/double") shouldBe List(ViewLensDefn("child", "childClass"), DoubleLensDefn("line1"))
    lensParser.apply("child/childClass,a/address") shouldBe List(ViewLensDefn("child", "childClass"), ViewLensDefn("a", "address"))
    lensParser.apply("child/childClass,a/*address") shouldBe List(ViewLensDefn("child", "childClass"), ListLensDefn("a", "address"))

    lensParser.apply("child/childClass,a/*string") shouldBe List(ViewLensDefn("child", "childClass"), ListLensDefn("a", "string"))
    lensParser.apply("child/childClass,a/*double") shouldBe List(ViewLensDefn("child", "childClass"), ListLensDefn("a", "double"))
    lensParser.apply("child/childClass,a/*integer") shouldBe List(ViewLensDefn("child", "childClass"), ListLensDefn("a", "integer"))
    lensParser.apply("child/childClass,a/*boolean") shouldBe List(ViewLensDefn("child", "childClass"), ListLensDefn("a", "boolean"))

    lensParser.apply("child/childClass,{firstItem},a/*address") shouldBe List(ViewLensDefn("child", "childClass"), FirstItemInListDefn(), ListLensDefn("a", "address"))
  }

  behavior of "LensLineParser"

  it should "parser lines" in {
    lineParser.apply("lens1=child/childClass,line1/string") shouldBe
      LensLine("lens1", List(ViewLensDefn("child", "childClass"), StringLensDefn("line1")))
  }

  //  @Test public void testLensStoreParser () {
  //    List < String > lines = List.of("line1=child/childClass,line1/double", "line1=child/childClass,line1/string", "line3=child1/childClass,line1/integer");
  //    assertEquals(Lists.map(lines, s -> lineParser.apply(s)), storeParser.apply(Lists.join(lines, "\n")).defns);
  //  }

}
