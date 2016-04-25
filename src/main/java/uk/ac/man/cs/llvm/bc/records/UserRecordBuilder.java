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
package uk.ac.man.cs.llvm.bc.records;

import java.util.Arrays;
import java.util.List;
import uk.ac.man.cs.llvm.bc.Operation;
import uk.ac.man.cs.llvm.bc.Parser;
import uk.ac.man.cs.llvm.bc.ParserResult;

public final class UserRecordBuilder implements Operation {

    private final List<UserRecordOperand> operands;

    public UserRecordBuilder(List<UserRecordOperand> operands) {
        this.operands = operands;
    }

    @Override
    public Parser apply(Parser parser) {
        ParserResult result = operands.get(0).get(parser);
        long id = result.value();

        long[] ops = new long[operands.size() - 1];

        int idx = 0;
        for (int i = 0; i < operands.size() - 1; i++) {
            result = operands.get(i + 1).get(result.parser());
            long[] values = result.values();
            if (idx + values.length > ops.length) {
                ops = Arrays.copyOf(ops, idx + values.length);
            }
            System.arraycopy(values, 0, ops, idx, values.length);
            idx += values.length;
        }

        result.parser().record(id, ops);

        return result.parser();
    }
}
