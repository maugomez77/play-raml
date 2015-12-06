package bavadim

import java.io.File

import org.apache.commons.io.FileUtils
import org.scalatest.{Assertions, FunSuite}
import play.routes.compiler._

/**
 * @author vadim
 * @since 03.11.15
 */
class RAMLRouteParserTest extends FunSuite with Assertions {
  val parser = new RAMLRouteParser

  val ethalon = List(
    Route(HttpVerb("GET"),
      PathPattern(Seq(StaticPart("users/"), DynamicPart("id", "[^/]+", encode = true))),
      HandlerCall("controllers", "Clients", instantiate = false, "show", Some(Seq(Parameter("id", "Long", None, None))))),

    Route(HttpVerb("PUT"),
      PathPattern(Seq(StaticPart("users/"), DynamicPart("id", "[^/]+", true))),
      HandlerCall("controllers", "Clients", false, "show", Some(Seq(Parameter("id", "Long", None, None))))),

    Route(HttpVerb("GET"),
      PathPattern(Seq(StaticPart("users/"), DynamicPart("id", "[^/]+", true), StaticPart("/another/"),
        DynamicPart("id1", "[^/]+", true))),
      HandlerCall("controllers", "Clients", false, "show",
        Some(Seq(Parameter("id", "Long", None, None), Parameter("id1", "Long", None, None)))))

  )

  test("Parser must parse example 1") {
    val ethalon = List(
      Route(HttpVerb("GET"),
        PathPattern(Seq(StaticPart("rootMethod"))),
        HandlerCall("controllers", "Clients", false, "show3", None)),

      Route(HttpVerb("GET"),
        PathPattern(Seq(StaticPart("testService/"), StaticPart("testMethod"))),
        HandlerCall("controllers", "Clients", false, "show1", None), List(Comment("Retrieve a test JSON"))),

      Route(HttpVerb("GET"),
        PathPattern(Seq(StaticPart("testService/"), StaticPart("testMethodWithParam/"), DynamicPart("param", "[^/]+", true))),
        HandlerCall("controllers", "Clients", false, "show2", Some(Seq(Parameter("id", "Long", None, None))))))

    parser.parse(file("1.raml")) match {
      case Right(l) =>
        assert(l == ethalon)
      case Left(e) => fail(e.toString())
    }
  }

  test("Parser must parse example 2") {
    val ethalon = List(
      Route(HttpVerb("GET"),
        PathPattern(Seq(StaticPart("user/"), DynamicPart("userId", "[^/]+", true))),
        HandlerCall("controllers", "UserController", true, "orgsAndBranches", Some(Seq(Parameter("userId", "String", None, None)))))
    )

    parser.parse(file("2.raml")) match {
      case Right(l) =>
        assert(l == ethalon)
      case Left(e) => fail(e.toString())
    }
  }

  test("Parser must parse example 3") {
    parser.parse(file("3.raml")) match {
      case Right(l) => fail(l.toString())
      case _ => succeed
    }
  }

  private def file(fileName: String): File = {
    val classLoader = getClass.getClassLoader
    new File(classLoader.getResource(fileName).getFile)
  }

  private def fileAsStr(fileName: String): String = FileUtils.readFileToString(file(fileName))
}