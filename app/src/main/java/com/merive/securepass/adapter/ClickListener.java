package com.merive.securepass.adapter;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface ClickListener {

    /**
     * This method is executing after clicking on item.
     *
     * @param name Item name value.
     * @throws IllegalBlockSizeException Illegal block size exception.
     * @throws InvalidKeyException       Invalid key exception.
     * @throws BadPaddingException       Bad padding exception.
     * @throws NoSuchAlgorithmException  No such algorithm exception.
     * @throws NoSuchPaddingException    No such padding exception.
     * @see IllegalBlockSizeException
     * @see InvalidKeyException
     * @see BadPaddingException
     * @see NoSuchAlgorithmException
     * @see NoSuchPaddingException
     */
    void onItemClick(String name) throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;
}
