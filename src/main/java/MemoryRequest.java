/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Kyle Bolton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.Objects;

/**
 * Created by kylebolton on 12/3/16.
 */
public class MemoryRequest {
    String pid;
    String address;
    int segment;
    int page;
    int offset;
    MemoryRequest(String pid, String virtual_address, int segment, int page, int offset){
        this.pid = pid;
        this.address = virtual_address;
        this.segment = segment;
        this.page = page;
        this.offset = offset;
    }

    @Override
    public boolean equals(Object object){
        if (object == null) {
            return false;
        }
        if (!MemoryRequest.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        final MemoryRequest other = (MemoryRequest) object;
        if ((this.pid == null) ? (other.pid != null) : !this.pid.equals(other.pid)) {
            return false;
        }
        return this.address.equals(other.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, address);
    }
}
