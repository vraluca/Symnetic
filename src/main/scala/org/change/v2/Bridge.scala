package org.change.v2

import org.change.v2.abstractnet.generic.{ConfigParameter, ElementBuilder, GenericElement, Port}
import org.change.v2.analysis.expression.concrete._
import org.change.v2.analysis.expression.concrete.nonprimitive._
import org.change.v2.analysis.processingmodels.instructions._
import org.change.v2.analysis.processingmodels.{Instruction, LocationId}
import org.change.v2.util.conversion.RepresentationConversion._

class Bridge(name: String,
                   elementType: String,
                   inputPorts: List[Port],
                   outputPorts: List[Port],
                   configParams: List[ConfigParameter])
  extends GenericElement(name,
    elementType,
    inputPorts,
    outputPorts,
    configParams) {

  /**
   * Every Click element modeled in Symnet has an "instructions" map.
   *
   * This maps every input port (identified by a LocationId) to the instruction performed.
   *
   * Knowing the input port id, one can get its corresponding LocationId by calling "inputPortName()".
   * The same holds true for output port names (used usually in conjuction with the "Forward instruction").
   *
   * Usually the sequence of instructions performed on a given input port ends with a "Forward". This
   * ensures the state gets propagated further, otherwise it becomes stuck (there are no instructions
   * to be executed).
   *
   * One can use the parameters provided in the Click file by issuing "configParams(id).value" where "id"
   * is the 0-based index of the parameter. For instance: "ipToNumber( configParams(0).value )" assumes the
   * first parameter of this click element is an IPv4 address and converts it to a long value.
   * @return
   */
  override def instructions: Map[LocationId, Instruction] = Map(
    inputPortName(0) -> InstructionBlock(
      Assign("IPAddr", ConstantValue( ipToNumber( configParams(0).value ) )),
      Assign("IPAddr2", ConstantValue( ipToNumber( configParams(1).value ) )),
      Assign("zero", ConstantValue(0)),
      Assign("diff", SymbolicValue()), 
      If(Constrain("diff", :>:(:@("zero"))), Forward(outputPortName(0)), Forward(outputPortName(1)))
    )
  )
  override def outputPortName(which: Int = 0): String = s"$name-$which-out"
}

class BridgeElementBuilder(name: String, elementType: String)
  extends ElementBuilder(name, elementType) {

  override def buildElement: GenericElement = {
    new Bridge(name, elementType, getInputPorts, getOutputPorts, getConfigParameters)
  }
}

object Bridge {
  private var unnamedCount = 0

  private val genericElementName = "bridge"

  private def increment {
    unnamedCount += 1
  }

  def getBuilder(name: String): BridgeElementBuilder = {
    increment ; new BridgeElementBuilder(name, "Bridge")
  }

  def getBuilder: BridgeElementBuilder =
    getBuilder(s"$genericElementName-$unnamedCount")
}
