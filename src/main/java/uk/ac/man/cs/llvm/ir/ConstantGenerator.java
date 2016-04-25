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

import uk.ac.man.cs.llvm.ir.types.Type;

public interface ConstantGenerator {

    public void createBinaryOperationExpression(Type type, int opcode, int lhs, int rhs);

    public void createBlockAddress(Type type, int method, int block);

    public void createCastExpression(Type type, int opcodee, int value);

    public void createCompareExpression(Type type, int opcode, int lhs, int rhs);

    public void createFloatingPoint(Type type, long value);

    public void createFromData(Type type, long[] data);

    public void creatFromString(Type type, String string, boolean isCString);

    public void createFromValues(Type type, int[] values);

    public void createGetElementPointerExpression(Type type, int pointer, int[] indices, boolean isInbounds);

    public void createInteger(Type type, long value);

    public void createNull(Type type);

    public void createUndefined(Type type);
}
