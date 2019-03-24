package one.xingyi.lensdsl.client

import java.util

import one.xingyi.core.UtilsSpec
import one.xingyi.core.json.NullJsonparserWriter
import one.xingyi.core.optics._
import one.xingyi.core.script.ViewNamesToViewLens

class LensLanguageTest extends UtilsSpec {

  implicit val parserWriter = new NullJsonparserWriter[Any]
  private val addressLens: Lens[Any, MirroredObjectForTest[Any]] = Lens[Any, MirroredObjectForTest[Any]](MirroredObjectForTest.apply, (m, f) => f.mirror)
  implicit val viewNamesToViewLens = new ViewNamesToViewLens(Map("address" -> addressLens))

  val lensParser = implicitly[LensValueParser]
  val lineParser = implicitly[LensLineParser]

  behavior of "LensvalueParser"


  it should "parser simple items" in {
    lensParser("line1,{string}") shouldBe List(ChildLensDefn("line1"), new StringLensDefn())
    lensParser("line1,{integer}") shouldBe List(ChildLensDefn("line1"), new IntegerLensDefn())
    lensParser("line1,{double}") shouldBe List(ChildLensDefn("line1"), new DoubleLensDefn())
    lensParser("a,!address") shouldBe List(ChildLensDefn("a"), ViewLensDefn("address", addressLens))
    lensParser("a,*,!address") shouldBe List(ChildLensDefn("a"), new ListLensDefn(), ViewLensDefn("address", addressLens))
    lensParser("a,*,#0,!address") shouldBe List(ChildLensDefn("a"), new ListLensDefn(), ItemInListDefn(0), ViewLensDefn("address", addressLens))
    lensParser("a,*,#1,!address") shouldBe List(ChildLensDefn("a"), new ListLensDefn(), ItemInListDefn(1), ViewLensDefn("address", addressLens))
    lensParser("a,*,#last") shouldBe List(ChildLensDefn("a"), new ListLensDefn(), new LastItemInListDefn())
    lensParser("a,*,#last,!address") shouldBe List(ChildLensDefn("a"), new ListLensDefn(), new LastItemInListDefn(), ViewLensDefn("address", addressLens))
    lensParser("{identity}") shouldBe List(new IdentityDefn())
    lensParser("{itemAsList}") shouldBe List(new ItemAsListDefn())
  }

  behavior of "LensLineParser"

  it should "parser lines" in {
    lineParser.apply("lens1=child,*,#0,line1,{string}") shouldBe
    LensLine("lens1", List(ChildLensDefn("child"), new ListLensDefn(), ItemInListDefn(0), ChildLensDefn("line1"), new StringLensDefn()))
  }

  //  @Test public void testLensStoreParser () {
  //    List < String > lines = List.of("line1=child/childClass,line1/double", "line1=child/childClass,line1/string", "line3=child1/childClass,line1/integer");
  //    assertEquals(Lists.map(lines, s -> lineParser.apply(s)), storeParser.apply(Lists.join(lines, "\n")).defns);
  //  }

}
