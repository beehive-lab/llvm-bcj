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
package uk.ac.man.cs.llvm.ir.module.records;

public enum ConstantsRecord {
    UNUSED_0,
    SETTYPE,
    NULL,
    UNDEF,
    INTEGER,
    WIDE_INTEGER,
    FLOAT,
    AGGREGATE,
    STRING,
    CSTRING,
    CE_BINOP,
    CE_CAST,
    CE_GEP,
    CE_SELECT,
    CE_EXTRACTELT,
    CE_INSERTELT,
    CE_SHUFFLEVEC,
    CE_CMP,
    INLINEASM_OLD,
    CE_SHUFVEC_EX,
    CE_INBOUNDS_GEP,
    BLOCKADDRESS,
    DATA,
    INLINEASM;

    public static ConstantsRecord decode(long id) {
        return values()[(int) id];
    }
}
