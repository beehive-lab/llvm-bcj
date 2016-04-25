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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.ac.man.cs.llvm.bc.ParserListener;
import uk.ac.man.cs.llvm.ir.attributes.Attribute;
import uk.ac.man.cs.llvm.ir.attributes.BuiltinAttribute;
import uk.ac.man.cs.llvm.ir.attributes.KeyAttribute;

public final class Parameters implements ParserListener {

    private List<List<Attribute>> groups = new ArrayList<>();

    public Parameters() {
}

    public ParserListener groups() {
        return new GroupListener();
    }

    @Override
    public void record(long id, long[] args) {
        // No idea why this is used, legacy perhaps
    }

    private static class GroupListener implements ParserListener {

        public GroupListener() {
        }

        @Override
        public void record(long id, long[] args) {
            if (id != 3) {
                System.out.println("  RECORD #" + id + " = " + Arrays.toString(args));
            }

            int entry = (int) args[0];

            // boolean isForMethod = args[1] == 0xffffffffL;

            List<Attribute> parameters = new ArrayList<>();

            int i = 2;

            while (i < args.length) {
                int v = (int) args[i++];
                if (v >= '0') {
                    StringBuilder token = new StringBuilder();
                    do {
                        token.append((char) v);
                        v = (int) args[i++];
                    } while(v != 0);
                    String attr = token.toString();

                    if (attr.matches("(\\d+|false|true)")) {
                        int last = parameters.size() - 1;
                        parameters.set(last, parameters.get(last).value(attr));
                    } else {
                        parameters.add(new KeyAttribute(attr));
                    }
                } else {
                    Attribute attr = BuiltinAttribute.lookup(v);
                    if (attr != null && attr.key() != null) {
                        parameters.add(attr);
                    }
                }
            }

            System.out.printf("  attributes #%d = { %s }%n", entry, parameters.toString().replaceAll("[,\\[\\]]", ""));
        }
    }
}
