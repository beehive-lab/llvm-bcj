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

public enum CallingConvention {

    C(0),
    FAST(8),
    COLD(9),
    WEBKIT_JS(12),
    ANYREG(13),
    PRESERVE_MOST(14),
    PRESERVE_ALL(15),
    CXX_FAST_TLS(17),
    X86_STDCALL(64),
    X86_FASTCALL(65),
    ARM_APCS(66),
    ARM_AAPCS(67),
    ARM_AAPCS_VFP(68);

    public static CallingConvention decode(long code) {
        for (CallingConvention cc : values()) {
            if (cc.code() == code) {
                return cc;
            }
        }
        return null;
    }

    private final long code;

    private CallingConvention(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }
}
