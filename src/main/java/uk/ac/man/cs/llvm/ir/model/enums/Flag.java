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

public enum Flag {

    INT_EXACT("exact", 1),

    INT_NO_UNSIGNED_WRAP("nuw", 1),
    INT_NO_SIGNED_WRAP("nsw", 2),

    FP_NO_NANS("nnan", 2),
    FP_NO_INFINITIES("ninf", 4),
    FP_NO_SIGNED_ZEROES("nsz", 8),
    FP_ALLOW_RECIPROCAL("arcp", 16),
    FP_FAST("fast", 31);

    private final String name;

    private final int mask;

    private Flag(String name, int mask) {
        this.name = name;
        this.mask = mask;
    }

    public int mask() {
        return mask;
    }

    public boolean test(long flags) {
        return (flags & mask) == mask;
    }

    @Override
    public String toString() {
        return name;
    }

    /* This method exists because there is ambiguity in the binary operation
     * instruction bitcode encoding. The flags can be one of three sets
     * depending on the type and operation used. This helper method converts
     * the flagbits into an array of Flag enums.
     */
    public static Flag[] decode(BinaryOperator opcode, int flagbits) {
        switch (opcode) {
            case INT_ADD:
            case INT_SUBTRACT:
            case INT_MULTIPLY:
            case INT_SHIFT_LEFT:
                return create(flagbits, INT_NO_UNSIGNED_WRAP, INT_NO_SIGNED_WRAP);

            case FP_ADD:
            case FP_SUBTRACT:
            case FP_MULTIPLY:
            case FP_DIVIDE:
            case FP_REMAINDER:
                if (FP_FAST.test(flagbits)) {
                    return new Flag[]{FP_FAST};
                }
                return create(flagbits, FP_NO_NANS, FP_NO_INFINITIES, FP_NO_SIGNED_ZEROES, FP_ALLOW_RECIPROCAL);

            default:
                return create(flagbits, INT_EXACT);
        }
    }

    private static Flag[] create(long flagbits, Flag... options) {
        int i = 0, count = Long.bitCount(flagbits);
        Flag[] flags = new Flag[count];
        for (Flag option : options) {
            if (option.test(flagbits)) {
                flags[i++] = option;
            }
        }
        return flags;
    }
}
