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

import uk.ac.man.cs.llvm.bc.Parser;
import uk.ac.man.cs.llvm.bc.ParserResult;
import uk.ac.man.cs.llvm.util.Pair;

public abstract class UserRecordOperand {

    public static Pair<ParserResult, UserRecordOperand> parse(Parser parser) {
        ParserResult result = parser.read(1);
        if (result.value() == 1) {
            result = result.parser().readVBR(8);
            return new Pair<>(result, new UserRecordLiteral(result.value()));
        } else {
            result = result.parser().read(3);
            long encoding = result.value();
            switch ((int) encoding) {
                case 1:
                    result = result.parser().readVBR(5);
                    return new Pair<>(result, new UserRecordFixedOperand(result.value()));

                case 2:
                    result = result.parser().readVBR(5);
                    return new Pair<>(result, new UserRecordVariableOperand(result.value()));

                case 3:
                    return new Pair<>(result, new UserRecordArrayOperand(null));

                case 4:
                    return new Pair<>(result, new UserRecordCharOperand());

                case 5:
                    return new Pair<>(result, new UserRecordBinaryOperand());

                default:
                    throw new IllegalStateException("Illegal encoding");
            }

        }
    }

    protected UserRecordOperand() {
    }

    protected abstract ParserResult get(Parser parser);
}
