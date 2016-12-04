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

/**
 * Created by kylebolton on 12/3/16.
 */
public class MemoryResponse {
    private String pid;
    private String address;
    private boolean success;

    public MemoryResponse(String pid, String address, boolean success) {
        this.pid = pid;
        if(!success && address != null){
            throw new IllegalArgumentException("address must be null when success is false");
        }
        this.success = success;
        this.address = address;
    }

    public String getAddress() {
        if(!wasSuccessful()){
           throw new NullPointerException("address is null when response not success");
        }
        return address;
    }

    public String getPid() {
        return pid;
    }

    public boolean wasSuccessful(){
        return success;
    }

}
