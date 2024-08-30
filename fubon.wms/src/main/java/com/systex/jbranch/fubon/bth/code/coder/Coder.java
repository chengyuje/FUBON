/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.fubon.bth.code.coder;

/**
 * the interface for decoding/encoding logic
 * @author Eli
 * @since 20180305
 */
public interface Coder {
    StringBuffer decode(StringBuffer content) throws Exception;
    StringBuffer encode(StringBuffer content) throws Exception;
    String ENCODE = "ENCODE";
    String DECODE = "DECODE";
}
