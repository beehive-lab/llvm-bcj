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
package uk.ac.man.cs.llvm.ir.module;

import java.util.Arrays;
import uk.ac.man.cs.llvm.bc.ParserListener;
import uk.ac.man.cs.llvm.bc.records.Records;

public final class Metadata implements ParserListener {

    public Metadata() {
    }

    public ParserListener attachments() {
        return ParserListener.DEFAULT;
    }

    public ParserListener kinds() {
        return ParserListener.DEFAULT;
    }

    @Override
    public void record(long id, long[] args) {
        System.out.printf("!%d = ", id);
        switch ((int) id) {
            case 1:
                System.out.printf("!{ !\"%s\" }%n", Records.toString(args));
                break;

            case 3:
                System.out.printf("!{ !%d }%n", args[0]);
                break;

            case 4:
                System.out.printf("!{ !\"%s\" }%n", Records.toString(args));
                break;

            case 6:
                System.out.printf("!{ !%d !\"%s\"}%n", args[0], Records.toString(args, 1));
                break;

            case 10:
                System.out.printf("!{ !%d }%n", args[0]);
                break;

            default:
                System.out.println(Arrays.toString(args));
                break;
        }
    }
}
