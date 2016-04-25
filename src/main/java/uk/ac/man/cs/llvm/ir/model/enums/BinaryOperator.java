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
package uk.ac.man.cs.llvm.ir.model.enums;

public enum BinaryOperator {

    INT_ADD(13),
    INT_SUBTRACT(14),
    INT_MULTIPLY(15),
    INT_UNSIGNED_DIVIDE(-1),
    INT_SIGNED_DIVIDE(16),
    INT_UNSIGNED_REMAINDER(-1),
    INT_SIGNED_REMAINDER(17),
    INT_SHIFT_LEFT(-1),
    INT_LOGICAL_SHIFT_RIGHT(-1),
    INT_ARITHMETIC_SHIFT_RIGHT(-1),
    INT_AND(-1),
    INT_OR(-1),
    INT_XOR(-1),

    FP_ADD(-1),
    FP_SUBTRACT(-1),
    FP_MULTIPLY(-1),
    FP_DIVIDE(-1),
    FP_REMAINDER(-1);

    public static BinaryOperator decode(int opcode, boolean isFloatingPoint) {
        BinaryOperator[] ops = values();
        if (opcode >= 0 && opcode <= INT_XOR.ordinal()) {
            BinaryOperator op = ops[opcode];
            return isFloatingPoint ? op.fp() : op;
        }
        return null;
    }

    private final int fpmap;

    private BinaryOperator(int fpmap) {
        this.fpmap = fpmap;
    }

    private BinaryOperator fp() {
        return fpmap < 0 ? null : values()[fpmap];
    }
}
