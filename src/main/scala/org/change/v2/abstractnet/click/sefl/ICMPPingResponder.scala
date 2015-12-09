package org.change.v2.abstractnet.click.sefl

import org.change.v2.abstractnet.generic.{ConfigParameter, ElementBuilder, GenericElement, Port}
import org.change.v2.analysis.expression.concrete._
import org.change.v2.analysis.expression.concrete.nonprimitive._
import org.change.v2.analysis.processingmodels.instructions._
import org.change.v2.analysis.processingmodels.{Instruction, LocationId}
import org.change.v2.util.conversion.RepresentationConversion._
import org.change.v2.util.canonicalnames._
import org.change.v2.analysis.memory.TagExp._
import org.change.v2.analysis.memory.Tag

class ICMPPingResponder(name: String,
                   elementType: String,
                   inputPorts: List[Port],
                   outputPorts: List[Port],
                   configParams: List[ConfigParameter])
  extends GenericElement(name,
    elementType,
    inputPorts,
    outputPorts,
    configParams) {

  override def instructions: Map[LocationId, Instruction] = Map(
    inputPortName(0) -> InstructionBlock(
      Constrain(Proto,:==:(ConstantValue(ICMPProto))),

      //assume L4Tag is set..., IPEncapsulation is needed too.
      Constrain(ICMPType,:==:(ConstantValue(8))),
      Constrain(ICMPCode,:==:(ConstantValue(0))),
      Assign(ICMPType, ConstantValue(0)),

      //mirror IP src dst here!
      Allocate("tmp"),
      Assign("tmp",:@(IPSrc)),
      Assign(IPSrc,:@(IPDst)),
      Assign(IPDst,:@("tmp")),
      Deallocate("tmp"),      
      Forward(outputPortName(0))
    )
  )
}

class ICMPPingResponderElementBuilder(name: String, elementType: String)
  extends ElementBuilder(name, elementType) {

  addInputPort(Port())
  addOutputPort(Port())

  override def buildElement: GenericElement = {
    new ICMPPingResponder(name, elementType, getInputPorts, getOutputPorts, getConfigParameters)
  }
}

object ICMPPingResponder {
  private var unnamedCount = 0

  private val genericElementName = "ICMPPingResponder"

  private def increment {
    unnamedCount += 1
  }

  def getBuilder(name: String): ICMPPingResponderElementBuilder = {
    increment ; new ICMPPingResponderElementBuilder(name, "ICMPPingResponder")
  }

  def getBuilder: ICMPPingResponderElementBuilder =
    getBuilder(s"$genericElementName-$unnamedCount")
}
