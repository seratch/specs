/**
 * Copyright (c) 2007-2009 Eric Torreborre <etorreborre@yahoo.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software. Neither the name of specs nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS INTHE SOFTWARE.
 */

package org.specs.util

/**
 * This object provides simple functions to instantiate classes.
 */
object Classes {
  
  /**
   * Create an instance of a given class.
   */
  def createObject[T](className: String): Option[T] = createObject[T](className, false)
  
  /**
   * Create an instance of a given class and optionally print message if the class can't be loaded.
   */
  def createObject[T](className: String, printMessage: Boolean): Option[T] = createObject(className, printMessage, false)
  /**
   * Create an instance of a given class and optionally print message and/or the stacktrace if the class can't be loaded.
   */
  def createObject[T](className: String, printMessage: Boolean, printStackTrace: Boolean): Option[T] = {
    try {
     return Some(getClass.getClassLoader.loadClass(className).newInstance.asInstanceOf[T])
    } catch {
      case e => {
        if (printMessage || System.getProperty("debugCreateObject") != null) scala.Console.println("Could not load class " + className)
        if (printStackTrace || System.getProperty("debugCreateObject") != null) e.printStackTrace()
      }
    }
    return None
  }
  /**
   * @return the class name without the package name
   */
  def className(fullName: String): String = {
    val remainingDollarNames = fullName.split("\\.").last.split("\\$")
    if (remainingDollarNames.size > 1) {
      if (remainingDollarNames(remainingDollarNames.size - 1).matches("\\d"))
        remainingDollarNames(remainingDollarNames.size - 2)
      else
        remainingDollarNames(remainingDollarNames.size - 1)
    }
    else remainingDollarNames(0)
  }
  /**
   * @return the class name without the package name
   */
  def className(klass: Class[_]): String = {
    val result = className(klass.getName)
    if (result.contains("anon") && klass.getSuperclass != null)
      className(klass.getSuperclass)
    else
      result
  }
  /**
   * @return the class name without the package name of any object
   */
  def getClassName[T](a: T): String = className(a.asInstanceOf[java.lang.Object].getClass)
}

/**
 * This trait provides utility functions for classes.
 */
trait Classes {
  /**
   * This method is used to determine for example if the JUnit runner is executed from Maven or within Eclipse.
   * In the first the test case names don't need to have the hashcode example.
   * 
   * @return true if the this current piece of code contains name in its stacktrace.
   */
  def isExecutedFrom(name: String) = new Exception().getStackTrace().exists(_.toString contains name)
}