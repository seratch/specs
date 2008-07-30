package org.specs.runner
import org.specs._
import org.specs.util._
import org.specs.runner._
import org.specs.Sugar._
import org.specs.specification._

object htmlRunnerUnit extends Specification with DataTables {
  val table = "a"    | "b"  | "result" |
                1    !  1   ! 2        |
                1    !  1   ! 2        |
                3    !  1   ! 5        | { (a: Int, b: Int, c: Int) => 
                    a + b must_== c  }
  def xml = { try { table.execute } catch { case _ => } ; hRunner.xmlFor(table) }
  "the xmlFor function" should { 
    "create an html table for a DataTable" in {
      xml must \\(<table class="nested"></table>) 
    }
    "create a header for the DataTable" in {
      xml  must (\\(<td>a</td>) and \\(<td>b</td>) and \\(<td>result</td>))
    }
    "create a row for each result" in {
      xml must (\\(<td>1</td>) and \\(<td>1</td>) and \\(<td>2</td>))
    }
    "create an icon for a failure" in {
      xml must \\(<td class="noBorder"><img src="images/icon_warning_sml.gif"/></td>)
    }
    "create a row for a failure message" in {
      xml must \\(<tr><td/><td colspan="3" class="failureMessage">'4' is not equal to '5'</td></tr>)
    }
  }
  "the sanitize function" should {
    "remove spaces from a name" in {
      hRunner.sanitize("hello world") must_== "hello+world"
    }
    "remove backslashes from a name" in {
      hRunner.sanitize("hello\\world") must_== "hello%5Cworld"
    }
    "remove # from a name" in {
      hRunner.sanitize("hello#world") must_== "hello%23world"
    }
  }
  "the status icon function" should {
    case class errors { def errors = List(new Exception()); def failures = List(FailureException("")); def skipped = List(SkippedException("")) }
    class failed extends errors { override def errors = Nil }
    class skipped extends failed { override def failures = Nil }

    "return an error icon for a result having errors" in {
      hRunner.statusIcon(errors()) must_== <img src="images/icon_error_sml.gif"/>
    }      
    "return a warning icon for a result having failures" in {
      hRunner.statusIcon(new failed()) must_== <img src="images/icon_warning_sml.gif"/>
    }       
    "return an info icon for a result having skipped" in {
      hRunner.statusIcon(new skipped()) must_== <img src="images/icon_info_sml.gif"/>
    }      
  }
  "the message function for an example" should {
    "return the failure message in a cell if there is a failure" in {
      val failed = new Example("", null) { addFailure(new FailureException("has failed")) }
      hRunner.message(failed, false) must_== <td>has failed</td>
    }
    "return the error message in a cell if there is an error" in {
      val error = new Example("", null) { addError(new Exception("error")) }
      hRunner.message(error, false) must_== <td>error</td>
    }
    "return the skipped message in a cell if the example is skipped" in {
      val skip = new Example("", null) { addSkipped(new SkippedException("skip")) }
      hRunner.message(skip, false) must_== <td>skip</td>
    }
    "return nothing if the whole sut is a success" in {
      val ok = new Example("", null)
      hRunner.message(ok, true) must_== scala.xml.NodeSeq.Empty
    }
  }
}
object hRunner extends HtmlRunner(null)
class htmlRunnerUnitTest extends JUnit4(htmlRunnerUnit)