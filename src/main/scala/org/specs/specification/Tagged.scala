package org.specs.specification
import scala.collection.mutable.Queue

/**
 * The Tagged trait allows to add tags to a Specification or a System under test.<p/>
 * The tags will be propagated to the specification or the sut components using the tagWith method. With this rule, 
 * a Tagged component should always have its parent tags (and possibly more).<p/>
 * 
 * Tags can be used when executing a Specification to restrict the executed examples. In that case, the examples are marked as skipped:<pre>
 * object mySpec extends Specification
 * mySpec.accept("unit").reject("functional") // this means that only the examples tagged as "unit" and not "functional" will be executed. 
 * </pre>
 * 
 * Any Tagged object can be tagged using the "tag" method, accepting any number of strings:<pre>
 * "this example is tagged" in {
 *   // assert something
 * } tag("unit", "sample")
 * </pre>
 */
trait Tagged {
  /** list of all tags */
  val tags: Queue[Tag] = new Queue[Tag]

  /** list of tags which are accepted for this element */
  val accepted: Queue[Tag] = new Queue[Tag]

  /** list of tags which are rejected for this element */
  val rejected: Queue[Tag] = new Queue[Tag]

  /** Add one or several tags to this element */
  def tag(t: String*): this.type = addTags(t:_*)

  /** Add one tag to this element */
  def addTag(t: String): this.type = { 
    tags.enqueue(Tag(t))
    propagateTagsToComponents
    this 
  }

  /** Add one or several tags to this element */
  def addTags(t: String*): this.type = { t.foreach(addTag(_)); this }
  
  /** 
   * Declare that this element should be accepted only if it has one of the accepted tags.
   *  This method declares the same thing for the components of this element.
   */
  def accept(t: Tag*): this.type = { 
    accepted.enqueue(t:_*)
    propagateTagsToComponents
    this
  }

  /** 
   * Declare that this element should be rejected if it has one of the rejected tags.
   *  This method declares the same thing for the components of this element.
   */
  def reject(t: Tag*): this.type = { 
    rejected.enqueue(t:_*)
    propagateTagsToComponents
    this 
  }

  /** 
   * Return true if this Tagged element:<ul>
   * <li>doesn't have any tags and no tags are marked as accepted
   * <li>or doesn't have any accepted tags
   * <li>or has at least one of the accepted tags and doesn't have any of the rejected tags
   * </ul>
   * 
   * @return true if the element can be accepted, considering the tags it owns and the accepted/rejected tags
   */
  def isAccepted = {
    tags.isEmpty && accepted.isEmpty ||
    (accepted.isEmpty ||
    !accepted.isEmpty && tags.exists(t => accepted.exists(a => t matches a))) && 
    !tags.exists(t => rejected.exists(a => t.matches(a)))
  }

  /** transforms a string to a Tag object */
  implicit def stringToTag(s: String) = Tag(s) 

  /** @return a description of the Tagged element showing the owned tags, the accepted and rejected tags */
  def tagSpec = "tags: " + tags.mkString(", ") + "  accepted: " + accepted.mkString(", ") + "  rejected: " + rejected.mkString(", ")

  /** add the tags specification from another tagged element. This is used when propagating the tags from a specification to a sut for example */
  def tagWith(other: Tagged): this.type = {
    this.addTags(other.tags.map(_.name):_*).
      accept(other.accepted.map(_.name):_*).
      reject(other.rejected.map(_.name):_*)
  }
  /** this method should be overriden if the Tagged element has Tagged components which should be tagged when this element is tagged */
  def taggedComponents: Seq[Tagged] = List()

  /** add tags, accepted and rejected to the tagged components if there are some */
  private def propagateTagsToComponents = taggedComponents.foreach(_.tagWith(this))
}

/** This class encapsulate tag strings as an object. The tag name is interpreted as a regex */
case class Tag(name: String) {
  /** @return trueThis class encapsulate tag strings as an object. The tag name is interpreted as a regex */
  def matches(pattern: Tag) = {
    try {(name matches pattern.name) || (pattern.name matches name)}
    catch {
      case e: java.util.regex.PatternSyntaxException => false
      case e: NullPointerException => false
    }
  }
}
