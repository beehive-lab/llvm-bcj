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
package uk.ac.man.cs.llvm.ir.model.constants;

import java.util.Arrays;
import uk.ac.man.cs.llvm.ir.types.Type;
import uk.ac.man.cs.llvm.ir.types.VectorType;

public final class VectorConstant extends AbstractConstant {

    private final Constant[] elements;

    public VectorConstant(VectorType type, Constant[] elements) {
        super(type);
        this.elements = elements;
    }

    public VectorConstant(VectorType type, Constant element) {
        super(type);
        elements = new Constant[type.getElementCount()];
        Arrays.fill(elements, element);
    }

    public Constant getElement(int index) {
        return elements[index];
    }

    public Constant[] getElements() {
        return Arrays.copyOf(elements, elements.length);
    }

    public Type getElementType() {
        return ((VectorType) getType()).getElementType();
    }

    public int getLength() {
        return elements.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        for (int i = 0; i < getLength(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Constant element = getElement(i);
            sb.append(element.getType()).append(" ").append(element);
        }
        return sb.append(">").toString();
    }
}
