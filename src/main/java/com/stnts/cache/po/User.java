package com.stnts.cache.po;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class User  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5393004129922986143L;

	private int id;
	
	private String name;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	  public String toString() {
	        return ToStringBuilder.reflectionToString(
	                this, ToStringStyle.MULTI_LINE_STYLE);
	    }
	
}
