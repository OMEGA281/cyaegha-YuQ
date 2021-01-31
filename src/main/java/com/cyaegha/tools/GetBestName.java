package com.cyaegha.tools;

import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Member;

public class GetBestName
{
	public static String getName(Friend friend,Member member)
	{
		if(member!=null)
		{
			String string=member.getNameCard();
			if(string==null||string.isEmpty())
				return member.getName();
			else
				return string;
		}else if(friend!=null)
			return friend.getName();
		else
			return null;
	}
}
