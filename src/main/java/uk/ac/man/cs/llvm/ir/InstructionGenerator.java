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
package uk.ac.man.cs.llvm.ir;
;
import uk.ac.man.cs.llvm.ir.types.Type;

public interface InstructionGenerator {

    public void createAllocation(Type type, int count, int align);

    public void createBinaryOperation(Type type, int opcode, int flags, int lhs, int rhs);

    public void createBranch(int block);

    public void createBranch(int condition, int blockTrue, int blockFalse);

    public void createCall(Type type, int target, int[] arguments);

    public void createCast(Type type, int opcode, int value);

    public void createCompare(Type type, int opcode, int lhs, int rhs);

    public void createExtractElement(Type type, int vector, int index);

    public void createExtractValue(Type type, int aggregate, int index);

    public void createGetElementPointer(Type type, int pointer, int[] indices, boolean isInbounds);

    public void createIndirectBranch(int address, int[] successors);

    public void createInsertElement(Type type, int vector, int index, int value);

    public void createInsertValue(Type type, int aggregate, int index, int value);

    public void createLoad(Type type, int source, int align, boolean isVolatile);

    public void createPhi(Type type, int[] values, int[] blocks);

    public void createReturn();

    public void createReturn(int value);

    public void createSelect(Type type, int condition, int trueValue, int falseValue);

    public void createShuffleVector(Type type, int vector1, int vector2, int mask);

    public void createStore(int destination, int source, int align, boolean isVolatile);

    public void createSwitch(int condition, int defaultBlock, int[] caseValues, int[] caseBlocks);

    public void createSwitchOld(int condition, int defaultBlock, long[] caseConstants, int[] caseBlocks);

    public void createUnreachable();

    public void enterBlock(long id);

    public void exitBlock();
}
