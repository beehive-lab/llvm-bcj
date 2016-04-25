/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.ac.man.cs.llvm.ir.disassembler;

import java.io.PrintStream;
import uk.ac.man.cs.llvm.ir.model.InstructionVisitor;
import uk.ac.man.cs.llvm.ir.model.Symbol;
import uk.ac.man.cs.llvm.ir.model.constants.IntegerConstant;
import uk.ac.man.cs.llvm.ir.model.elements.*;
import uk.ac.man.cs.llvm.ir.types.PointerType;
import uk.ac.man.cs.llvm.ir.types.Type;

public final class LLVMDisassemblerBlockVisitor implements InstructionVisitor {

    private final PrintStream out;

    public LLVMDisassemblerBlockVisitor(PrintStream out) {
        this.out = out;
    }

    private String toArgumentsString(Call call) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < call.getArgumentCount(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(toTypedValueString(call.getArgument(i)));
        }
        return sb.toString();
    }

    private String toAlignmentString(int align) {
        if (align == 0) {
            return "";
        } else {
            return ", align " + (1 << (align - 1));
        }
    }

    private String toAllocateCountString(AllocateInstruction allocate) {
        Symbol symbol = allocate.getCount();
        if (symbol instanceof IntegerConstant && ((IntegerConstant) symbol).getValue() == 1) {
            return "";
        }
        return String.format(", %s", toTypedValueString(allocate.getCount()));
    }

    private String toTypedValueString(Symbol value) {
        return String.format("%s %s", value.getType(), toValueString(value));
    }

    private String toValueString(Symbol symbol) {
        return LLVMDisassemblerHelper.toString(symbol);
    }

    @Override
    public void visit(AllocateInstruction allocate) {
        out.printf("  %s = alloca %s%s%s%n",
                allocate.getName(),
                allocate.getPointeeType(),
                toAllocateCountString(allocate),
                toAlignmentString(allocate.getAlign()));
    }

    @Override
    public void visit(BinaryOperationInstruction operation) {
        out.printf("  %s = %s %s %s, %s%n",
                operation.getName(),
                LLVMDisassemblerHelper.toString(operation.getOperator(), operation.getFlags()),
                operation.getType(),
                toValueString(operation.getLHS()),
                toValueString(operation.getRHS()));
    }

    @Override
    public void visit(BranchInstruction branch) {
        out.printf("  br label %%%s%n",
                branch.getSuccessor().getName());
    }

    @Override
    public void visit(CallInstruction call) {
        out.printf("  %s = call %s %s(%s)%n",
                call.getName(),
                call.getCallType().getReturnType(),
                toValueString(call.getCallTarget()),
                toArgumentsString(call));
    }

    @Override
    public void visit(CastInstruction cast) {
        out.printf("  %s = %s %s to %s%n",
                cast.getName(),
                LLVMDisassemblerHelper.toString(cast.getOperator()),
                toTypedValueString(cast.getValue()),
                cast.getType());
    }

    @Override
    public void visit(CompareInstruction compare) {
        out.printf("  %s = %s %s, %s%n",
                compare.getName(),
                LLVMDisassemblerHelper.toString(compare.getLHS().getType(), compare.getOperator()),
                toTypedValueString(compare.getLHS()),
                toValueString(compare.getRHS()));
    }

    @Override
    public void visit(ConditionalBranchInstruction branch) {
        out.printf("  br i1 %s, label %%%s, label %%%s%n",
                toValueString(branch.getCondition()),
                branch.getTrueSuccessor().getName(),
                branch.getFalseSuccessor().getName());
    }

    @Override
    public void visit(ExtractElementInstruction extract) {
        out.printf("  %s = extractelement %s, %s%n",
                extract.getName(),
                toTypedValueString(extract.getVector()),
                toTypedValueString(extract.getIndex()));
    }

    @Override
    public void visit(ExtractValueInstruction extract) {
        out.printf("  %s = extractvalue %s, %d%n",
                extract.getName(),
                toTypedValueString(extract.getAggregate()),
                extract.getIndex());
    }

    @Override
    public void visit(GetElementPointerInstruction gep) {
        out.printf("  %s = getelementptr %s%s, %s",
                gep.getName(),
                gep.isInbounds() ? "inbounds " : "",
                ((PointerType) gep.getBasePointer().getType()).getPointeeType(),
                toTypedValueString(gep.getBasePointer()));
        for (int i = 0; i < gep.getIndexCount(); i++) {
            out.printf(", %s", toTypedValueString(gep.getIndex(i)));
        }
        out.println();
    }

    @Override
    public void visit(IndirectBranchInstruction branch) {
        out.printf("  indirectbr %s, [ ", toTypedValueString(branch.getAddress()));
        for (int i = 0; i < branch.getSuccessorCount(); i++) {
            if (i > 0) {
                out.print(", ");
            }
            out.printf("label %%%s", branch.getSuccessor(i).getName());
        }
        out.printf(" ]%n");
    }

    @Override
    public void visit(InsertElementInstruction insert) {
        out.printf("  %s = insertelement %s, %s, %s%n",
                insert.getName(),
                toTypedValueString(insert.getVector()),
                toTypedValueString(insert.getValue()),
                toTypedValueString(insert.getIndex()));
    }

    @Override
    public void visit(InsertValueInstruction insert) {
        out.printf("  %s = insertvalue %s, %s, %d%n",
                insert.getName(),
                toTypedValueString(insert.getAggregate()),
                toTypedValueString(insert.getValue()),
                insert.getIndex());
    }

    @Override
    public void visit(LoadInstruction load) {
        out.printf("  %s = load %s%s, %s%s%n",
                load.getName(),
                load.isVolatile() ? "volatile " : "",
                load.getType(),
                toTypedValueString(load.getSource()),
                toAlignmentString(load.getAlign()));
    }

    @Override
    public void visit(PhiInstruction phi) {
        out.printf("  %s = phi %s", phi.getName(), phi.getType());
        for (int i = 0; i < phi.getSize(); i++) {
            if (i > 0) {
                out.print(",");
            }
            out.printf(" [ %s, %%%s ]",
                    toValueString(phi.getValue(i)),
                    phi.getBlock(i).getName());
        }
        out.println();
    }

    @Override
    public void visit(ReturnInstruction ret) {
        if (ret.getValue() == null) {
            out.println("  ret void");
        } else {
            out.printf("  ret %s%n",
                    toTypedValueString(ret.getValue()));
        }
    }

    @Override
    public void visit(SelectInstruction select) {
        out.printf("  %s = select %s, %s, %s%n",
                select.getName(),
                toTypedValueString(select.getCondition()),
                toTypedValueString(select.getTrueValue()),
                toTypedValueString(select.getFalseValue()));
    }

    @Override
    public void visit(ShuffleVectorInstruction shuffle) {
        out.printf("  %s = shufflevector %s, %s, %s%n",
                shuffle.name,
                toTypedValueString(shuffle.getVector1()),
                toTypedValueString(shuffle.getVector2()),
                toTypedValueString(shuffle.getMask()));
    }

    @Override
    public void visit(StoreInstruction store) {
        out.printf("  store %s%s, %s%s%n",
                store.isVolatile() ? "volatile " : "",
                toTypedValueString(store.getSource()),
                toTypedValueString(store.getDestination()),
                toAlignmentString(store.getAlign()));
    }

    @Override
    public void visit(SwitchInstruction select) {
        out.printf("  switch %s, label %%%s [ ",
                toTypedValueString(select.getCondition()),
                select.getDefaultBlock().getName());
        for (int i = 0; i < select.getCaseCount(); i++) {
            out.printf("%s, label %%%s ",
                    toTypedValueString(select.getCaseValue(i)),
                    select.getCaseBlock(i).getName());
        }
        out.printf("]%n");
    }

    @Override
    public void visit(SwitchOldInstruction select) {
        Type type = select.getCondition().getType();
        out.printf("  switch %s, label %%%s [ ",
                toTypedValueString(select.getCondition()),
                select.getDefaultBlock().getName());
        for (int i = 0; i < select.getCaseCount(); i++) {
            out.printf("%s %d, label %%%s ",
                    type,
                    select.getCaseValue(i),
                    select.getCaseBlock(i).getName());
        }
        out.printf("]%n");
    }

    @Override
    public void visit(UnreachableInstruction unreachable) {
        out.printf("  unreachable%n");
    }

    @Override
    public void visit(VoidCallInstruction call) {
        out.printf("  call void %s(%s)%n",
                toValueString(call.getCallTarget()),
                toArgumentsString(call));
    }
}
