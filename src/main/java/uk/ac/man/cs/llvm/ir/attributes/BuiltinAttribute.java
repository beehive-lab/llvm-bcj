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
package uk.ac.man.cs.llvm.ir.attributes;

public enum BuiltinAttribute implements Attribute {
    ALIGNMENT("alignment"),
    ALWAYS_INLINE("?"),
    BY_VAL("byval"),
    INLINE_HINT(null),
    IN_REG("?"),
    MIN_SIZE("?"),
    NAKED("?"),
    NEST("?"),
    NO_ALIAS("?"),
    NO_BUILTIN("?"),
    NO_CAPTURE("?"),
    NO_DUPLICATE("?"),
    NO_IMPLICIT_FLOAT("?"),
    NO_INLINE("?"),
    NON_LAZY_BIND("?"),
    NO_RED_ZONE("?"),
    NO_RETURN("?"),
    NO_UNWIND("nounwind"),
    OPTIMIZE_FOR_SIZE("?"),
    READ_NONE("readnone"),
    READ_ONLY("readonly"),
    RETURNED("?"),
    RETURNS_TWICE("?"),
    S_EXT("signext"),
    STACK_ALIGNMENT("?"),
    STACK_PROTECT("?"),
    STACK_PROTECT_REQ("?"),
    STACK_PROTECT_STRONG("?"),
    STRUCT_RET("?"),
    SANITIZE_ADDRESS("?"),
    SANITIZE_THREAD("?"),
    SANITIZE_MEMORY("?"),
    UW_TABLE("uwtable"),
    Z_EXT("zeroext"),
    BUILTIN("?"),
    COLD("?"),
    OPTIMIZE_NONE("?"),
    IN_ALLOCA("?"),
    NON_NULL("?"),
    JUMP_TABLE("?"),
    DEREFERENCEABLE("?"),
    DEREFERENCEABLE_OR_NULL("?"),
    CONVERGENT("?"),
    SAFESTACK("?"),
    ARGMEMONLY("?"),
    SWIFT_SELF("?"),
    SWIFT_ERROR("?"),
    NO_RECURSE("norecurse");

    public static BuiltinAttribute lookup(int id) {
        if (id >= 1 && id <= 48) {
            return values()[id - 1];
        }
        return null;
    }

    private final String key;

    private BuiltinAttribute(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
