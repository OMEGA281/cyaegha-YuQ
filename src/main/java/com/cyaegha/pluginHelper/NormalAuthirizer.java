package com.cyaegha.pluginHelper;

import com.cyaegha.Permission;

public class NormalAuthirizer implements Permission
{
	private static MainAuthirizerList mainAuthirizerList;

	public static int getPermission(long qq)
	{
		if(mainAuthirizerList.isSOP(qq))
			return SOP;
		else if(mainAuthirizerList.isOP(qq))
			return OP;
		else
			return PRIVATE;
	}
}
