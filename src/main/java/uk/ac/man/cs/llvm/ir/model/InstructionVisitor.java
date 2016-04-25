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
package uk.ac.man.cs.llvm.ir.model;

import uk.ac.man.cs.llvm.ir.model.elements.*;

public interface InstructionVisitor {

    public void visit(AllocateInstruction allocate);

    public void visit(BinaryOperationInstruction operation);

    public void visit(BranchInstruction branch);

    public void visit(CallInstruction call);

    public void visit(CastInstruction cast);

    public void visit(CompareInstruction operation);

    public void visit(ConditionalBranchInstruction branch);

    public void visit(ExtractElementInstruction extract);

    public void visit(ExtractValueInstruction extract);

    public void visit(GetElementPointerInstruction gep);

    public void visit(IndirectBranchInstruction branch);

    public void visit(InsertElementInstruction insert);

    public void visit(InsertValueInstruction insert);

    public void visit(LoadInstruction load);

    public void visit(PhiInstruction phi);

    public void visit(ReturnInstruction ret);

    public void visit(SelectInstruction select);

    public void visit(ShuffleVectorInstruction shuffle);

    public void visit(StoreInstruction store);

    public void visit(SwitchInstruction select);

    public void visit(SwitchOldInstruction select);

    public void visit(UnreachableInstruction unreachable);

    public void visit(VoidCallInstruction call);
}
