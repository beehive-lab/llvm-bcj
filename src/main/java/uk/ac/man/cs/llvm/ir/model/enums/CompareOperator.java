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

public enum CompareOperator {

    FP_FALSE,
    FP_ORDERED_EQUAL,
    FP_ORDERED_GREATER_THAN,
    FP_ORDERED_GREATER_OR_EQUAL,
    FP_ORDERED_LESS_THAN,
    FP_ORDERED_LESS_OR_EQUAL,
    FP_ORDERED_NOT_EQUAL,
    FP_ORDERED,
    FP_UNORDERED,
    FP_UNORDERED_EQUAL,
    FP_UNORDERED_GREATER_THAN,
    FP_UNORDERED_GREATER_OR_EQUAL,
    FP_UNORDERED_LESS_THAN,
    FP_UNORDERED_LESS_OR_EQUAL,
    FP_UNORDERED_NOT_EQUAL,
    FP_TRUE,

    INT_EQUAL,
    INT_NOT_EQUAL,
    INT_UNSIGNED_GREATER_THAN,
    INT_UNSIGNED_GREATER_OR_EQUAL,
    INT_UNSIGNED_LESS_THAN,
    INT_UNSIGNED_LESS_OR_EQUAL,
    INT_SIGNED_GREATER_THAN,
    INT_SIGNED_GREATER_OR_EQUAL,
    INT_SIGNED_LESS_THAN,
    INT_SIGNED_LESS_OR_EQUAL;

    public static CompareOperator decode(long opcode) {
        CompareOperator[] ops = values();

        int fpops = FP_TRUE.ordinal() + 1;

        if (opcode >= 0 && opcode < fpops) {
            return ops[(int) opcode];
        } else {
            opcode = (opcode + fpops) - 32;
            if (opcode >= fpops && opcode < values().length) {
                return ops[(int) opcode];
            }
        }
        return null;
    }
}
