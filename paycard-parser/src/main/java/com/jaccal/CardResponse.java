/*
 * Copyright (c) 2005 Chang Sau Sheong, Thomas Tarpin-Lyonnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaccal;

import com.jaccal.util.NumUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The response from the smart card after a command is sent to the card. Contains a status word object
 *
 * @author Chang Sau Sheong
 * @see com.jaccal.StatusWord
 */
public class CardResponse {

    private byte[] data;
    public StatusWord statusWord;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public StatusWord getStatusWord() {
        return statusWord;
    }

    public void setStatusWord(StatusWord statusWord) {
        this.statusWord = statusWord;
    }


    public boolean isSuccess() {
        if (statusWord != null) {
            return statusWord.isSuccess();
        }
        return false;

    }

    public final byte[] getBytes() throws IOException {
        byte[] byteArray;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        if (data != null) {
            baos.write(data);
        }
        baos.write(statusWord.getSw1());
        baos.write(statusWord.getSw2());

        byteArray = baos.toByteArray();
        baos.close();

        return byteArray;
    }

    public final byte[] getRespBytes() throws IOException {
        byte[] byteArray;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        if (data != null) {
            baos.write(data);
        }

        byteArray = baos.toByteArray();
        baos.close();

        return byteArray;
    }

    public String toString() {
        String ret = null;

        try {
            if (data.length != 0) {
                ret = "[R] " + NumUtil.toHexString(getRespBytes()) + "\n[SW] " + NumUtil.toHexString(statusWord.getBytes()) + "\n";
            } else {
                ret = "[SW] " + NumUtil.toHexString(statusWord.getBytes()) + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

}
