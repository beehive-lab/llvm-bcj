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
package uk.ac.man.cs.llvm.bc;

import java.util.ArrayList;
import java.util.List;
import uk.ac.man.cs.llvm.bc.records.UserRecordArrayOperand;
import uk.ac.man.cs.llvm.bc.records.UserRecordBuilder;
import uk.ac.man.cs.llvm.bc.records.UserRecordOperand;
import uk.ac.man.cs.llvm.util.Pair;

@FunctionalInterface
public interface Operation {

    public Parser apply(Parser parser);

    public static final Operation DEFINE_ABBREV = (parser) -> {
        ParserResult result = parser.readVBR(5);
        long count = result.value();

        List<UserRecordOperand> operands = new ArrayList<>();

        for (long i = 0; i < count; i++) {
            Pair<ParserResult, UserRecordOperand> pair = UserRecordOperand.parse(result.parser());
            result = pair.i1();
            UserRecordOperand operand = pair.i2();
            if (operand instanceof UserRecordArrayOperand) {
                pair = UserRecordOperand.parse(result.parser());
                result = pair.i1();
                operand = pair.i2();
                operands.add(new UserRecordArrayOperand(operand));
                i++;
            } else {
                operands.add(operand);
            }
        }

        return result.parser().operation(new UserRecordBuilder(operands));
    };

    public static final Operation END_BLOCK = (parser) -> {
        return parser.exit();
    };

    public static final Operation ENTER_SUBBLOCK = (parser) -> {
        ParserResult result = parser.readVBR(8);
        long id = result.value();

        result = result.parser().readVBR(4);
        long idsize = result.value();

        parser = result.parser().align(32);

        result = parser.read(32);
        long size = result.value();

        return result.parser().enter(id, size, idsize);
    };

    public static final Operation UNABBREV_RECORD = (parser) -> {
        ParserResult result = parser.readVBR(6);
        long id = result.value();

        result = result.parser().readVBR(6);
        long count = result.value();

        long[] operands = new long[(int) count];

        for (long i = 0; i < count; i++) {
            result = result.parser().readVBR(6);
            operands[(int) i] = result.value();
        }

        parser = result.parser().record(id, operands);

        return parser;
    };
}
