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
import uk.ac.man.cs.llvm.ir.model.*;
import uk.ac.man.cs.llvm.ir.types.PointerType;
import uk.ac.man.cs.llvm.ir.types.StructureType;
import uk.ac.man.cs.llvm.ir.types.Type;

public final class LLVMDisassemblerVisitor implements ModelVisitor {

    private final PrintStream out;

    public LLVMDisassemblerVisitor(PrintStream out) {
        this.out = out;
    }

    private String toAlignmentString(int align) {
        if (align == 0) {
            return "";
        } else {
            return ", align " + (1 << (align - 1));
        }
    }

    @Override
    public void visit(GlobalConstant variable) {
        out.printf("%s = global constant %s %s%s%n",
                variable.getName(),
                ((PointerType) variable.getType()).getPointeeType(),
                LLVMDisassemblerHelper.toString(variable.getValue()),
                toAlignmentString(variable.getAlign()));
        out.println();
    }

    @Override
    public void visit(GlobalVariable variable) {
        out.printf("%s = global %s %s%s%n",
                variable.getName(),
                ((PointerType) variable.getType()).getPointeeType(),
                LLVMDisassemblerHelper.toString(variable.getValue()),
                toAlignmentString(variable.getAlign()));
        out.println();
    }

    @Override
    public void visit(FunctionDeclaration function) {
        out.printf("declare %s %s(%s)%n",
                function.getReturnType(),
                function.getName(),
                LLVMDisassemblerHelper.toString(function.getArgumentTypes(), function.isVarArg()));
        out.println();
    }

    @Override
    public void visit(FunctionDefinition function) {
        out.printf("define %s %s(%s) {%n",
                function.getReturnType(),
                function.getName(),
                LLVMDisassemblerHelper.toString(function.getParameters(), function.isVarArg()));
        function.accept(new LLVMDisassemblerMethodVisitor(out));
        out.println("}");
        out.println();
    }

    @Override
    public void visit(Type type) {
        if (type instanceof StructureType) {
            StructureType structure = (StructureType) type;
            if (!"<anon>".equals(structure.getName())) {
                out.printf("%%%s = type %s%n",
                        structure.getName(),
                        structure.toDeclarationString());
                out.println();
            }
        }
    }
}
