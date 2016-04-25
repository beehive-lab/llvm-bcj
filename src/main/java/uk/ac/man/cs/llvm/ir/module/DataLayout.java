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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import uk.ac.man.cs.llvm.ir.model.enums.Mangling;
import uk.ac.man.cs.llvm.ir.types.Type;

public final class DataLayout {

    private boolean isBigEndian = true;

    private int alignment = 64;

    private Map<String, TypeSpecification> specifications = new HashMap<>();

    private TypeSpecification aggregates;

    private PointerSpecification pointers;

    private Mangling mangling = null;

    private int[] widths = new int[0];

    public DataLayout() {
    }

    public int getStackAlignment() {
        return alignment;
    }

    public int getPointerAlignment(Type type) {
        return pointers == null ? 8 : pointers.getAlignment() >>> 3;
    }

    public int getPointerSize(Type type) {
        return pointers == null ? 8 : pointers.getSize() >>> 3;
    }

    public int getTypeAlignment(Type type) {
        return 1;
    }

    public int getTypeSize(Type type) {
        return type.sizeof();
    }

    public boolean isBigEndian() {
        return isBigEndian;
    }

    public void parse(String definition) {
        for (String def : definition.split("\\-")) {
            if ("E".equals(def)) {
                isBigEndian = true;
            } else if ("e".equals(def)) {
                isBigEndian = false;
            } else if (def.startsWith("S")) {
                alignment = Integer.parseInt(def.substring(1));
            } else if (def.startsWith("m")) {
                mangling = Mangling.decode(def.substring(2));
            } else if (def.startsWith("n")) {
                String[] defs = def.substring(1).split(":");
                widths = new int[defs.length];
                for (int i = 0; i < defs.length; i++) {
                    widths[i] = Integer.parseInt(defs[i]);
                }
            } else if (def.startsWith("p:")) {
                pointers = PointerSpecification.parse(def.substring(2));
            } else if (def.matches("[ifv].*")) {
                int delim = def.indexOf(":");
                specifications.put(
                        def.substring(0, delim),
                        TypeSpecification.parse(def.substring(delim + 1)));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(isBigEndian ? "E-" : "e-");
        if (mangling != null) {
             sb.append(String.format("m:%s-", mangling));
        }
        if (pointers != null) {
            sb.append(String.format("p:%s-", pointers));
        }
        for (Entry<String, TypeSpecification> spec : specifications.entrySet()) {
            sb.append(String.format("%s:%s-", spec.getKey(), spec.getValue()));
        }
        for (int i = 0; i < widths.length; i++) {
            if (i == 0) {
                sb.append("n");
            } else {
                sb.append(":");
            }
            sb.append(widths[i]);
        }
        if (widths.length > 0) {
            sb.append("-");
        }
        sb.append(String.format("S%d", alignment));

        return sb.toString();
    }


    private static class TypeSpecification {

        public static TypeSpecification parse(String definition) {
            String[] widths = definition.split(":");
            int w0 = Integer.parseInt(widths[0]);
            if (widths.length == 1) {
                return new TypeSpecification(-1, w0);
            } else {
                return new TypeSpecification(w0, Integer.parseInt(widths[1]));
            }
        }

        private final int abi, alignment;

        public TypeSpecification(int abi, int alignment) {
            this.abi = abi;
            this.alignment = alignment;
        }

        public int getABI() {
            return abi;
        }

        public int getAlignment() {
            return alignment;
        }

        @Override
        public String toString() {
            if (abi < 0) {
                return String.valueOf(alignment);
            } else {
                return String.format("%d:%d", abi, alignment);
            }
        }
    }

    private static class PointerSpecification extends TypeSpecification {

        public static PointerSpecification parse(String definition) {
            return new PointerSpecification(0, 0, 0);
        }

        private final int size;

        public PointerSpecification(int size, int abi, int alignment) {
            super(abi, alignment);
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
