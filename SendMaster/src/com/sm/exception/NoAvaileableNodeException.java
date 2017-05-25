package com.sm.exception;
/**
 * @author zhoujia
 *
 * @date 2015年9月8日
 */
public class NoAvaileableNodeException extends Exception{

	private static final long serialVersionUID = 5389326965695826023L;
	
	public String exception ; 
	public NoAvaileableNodeException(String exception) {
		this.exception = exception;
	}

}
