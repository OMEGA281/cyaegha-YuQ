package com.cyaegha.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.cyaegha.pluginHelper.annotations.RegistCommand;
import com.cyaegha.pluginHelper.dataExchanger.DataExchanger;
import com.cyaegha.surveillance.Log;
import com.cyaegha.tools.GetBestName;
import com.cyaegha.tools.XMLUtils;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;

import net.sourceforge.jeval.Evaluator;

@GroupController
@PrivateController
@EventListener
public class ERPG
{
	private final String SAME_STRING="samestring";
	private final String[] ShortCrazy={"失忆：发现自己只记得最后身处的安全地点，却没有任何来到这里的记忆","假性残疾：陷入了心理性的失明，失聪或躯体缺失感中",
			"暴力倾向：陷入了六亲不认的暴力行为中，对周围的敌人与友方进行着无差别的攻击","偏执：陷入了严重的偏执妄想之中","人际依赖：因为一些原因而降他人误认为了他重要的人并且努力的会与那个人保持那种关系",
			"昏厥：当场昏倒","逃避行为：会用任何的手段试图逃离现在所处的位置","竭嘶底里：表现出大笑，哭泣，嘶吼，害怕等的极端情绪表现","恐惧：获得了一种恐惧症状(由kp决定，或.draw 恐怖症状)",
			"狂躁：获得了一种狂躁症状(由kp决定，或.draw 狂躁症状)"};
	private final String[] LongCrazy={"失忆：发现自己身处一个陌生的地方，并忘记了自己是谁。记忆会随时间恢复。","被窃：时间过后恢复清醒，发觉自己被盗，身体毫发无损。所有有价值的东西消失。",
			"遍体鳞伤：时间过后恢复清醒，发现自己身上满是拳痕和瘀伤。生命值减少到疯狂前的一半，但不会造成重伤。","暴力倾向：陷入强烈的暴力与破坏欲之中。回过神来可能会理解自己做了什么也可能毫无印象。",
			"极端信念：采取极端和疯狂的表现手段展示他们的思想信念之一。","重要之人：在持续或更久的时间中，将不顾一切地接近那个人，并为他们之间的关系做出行动。",
			"被收容：在精神病院病房或警察局牢房中回过神来，可能会慢慢回想起导致自己被关在这里的事情。","逃避行为：恢复清醒时发现自己在很远的地方。",
			"恐惧：患上一个新的恐惧症。(由kp决定，或.draw 恐怖症状)时间过后将会尽力避开恐惧源。","狂躁：患上一个新的狂躁症。(由kp决定，或.draw 狂躁症状)时间过后恢复理智"};

	private enum LevelStatus
	{
		EX_SUCCESS("极难成功"),S_SUCCESS("困难成功"),SUCCESS("成功"),FAILED("失败");

		private String string;

		public String getString()
		{
			return string;
		}

		LevelStatus(String string)
		{
			this.string=string;
		}
	}

	private enum SpecialStatus
	{
		BIGSUCCESS("大成功!"),BIGFAILED("大失败!");

		private String string;

		public String getString()
		{
			return string;
		}

		SpecialStatus(String string)
		{
			this.string=string;
		}
	}

	/**
	 * 提升技能上限的情况
	 * 
	 * @author GuoJiaCheng
	 *
	 */
	private enum SkillUBound
	{
		san("克苏鲁神话",-1);

		private String effectSkill;
		private int effectNum;

		public String getEffectSkill()
		{
			return effectSkill;
		}

		public int getEffect()
		{
			return effectNum;
		}

		SkillUBound(String string,int i)
		{
			effectNum=i;
			effectSkill=string;
		}

	}

	/**
	 * 提升技能下限的情况
	 * 
	 * @author GuoJiaCheng
	 *
	 */
	private enum SkillBBound
	{
		;
		private String effectSkill;
		private int effectNum;

		public String getEffectSkill()
		{
			return effectSkill;
		}

		public int getEffect()
		{
			return effectNum;
		}

		SkillBBound(String string,int i)
		{
			effectNum=i;
			effectSkill=string;
		}

	}

	/**
	 * 用于返回检定情况的类，包含{@link LevelStatus}和{@link SpecialStatus}
	 * 
	 * @author GuoJiaCheng
	 *
	 */
	private class CheckStatus
	{
		LevelStatus levelStatus;
		SpecialStatus specialStatus;
		int randomNum;

		public CheckStatus(LevelStatus levelStatus,SpecialStatus specialStatus,int randomNum)
		{
			this.levelStatus=levelStatus;
			this.specialStatus=specialStatus;
			this.randomNum=randomNum;
		}
	}

	private static final String XMLLABLE_CARD="card";
	private static final String XMLLABLE_NAME="name";
	private static final String XMLLABLE_POINT="point";

	private static HashMap<String,String[]> SameStringList;
	private DataExchanger exchanger;
	private File dataFloder;

	@Event
	public void init(AppStartEvent event)
	{
		exchanger=DataExchanger.getDataExchanger(getClass());
		dataFloder=exchanger.getPluginDataFloder();
		loadSameString();
		if(SameStringList.isEmpty())
		{
			addSameString("力量","str","STR","str","力量值");
			addSameString("敏捷","dex","DEX","Dex","敏捷值");
			addSameString("理智","san","SAN","San","理智值","san值");
			addSameString("力量","str","STR","Str","力量值");
			addSameString("敏捷","dex","DEX","Dex","敏捷值");
			addSameString("智力","int","INT","Int","智力值","灵感");
			addSameString("体质","con","CON","Con","体质值");
			addSameString("外貌","app","APP","App","外貌值");
			addSameString("意志","pow","POW","Pow","意志值");
			addSameString("体型","siz","SIZ","Siz","体型值");
			addSameString("教育","edu","EDU","Edu","教育值");
			addSameString("移动","mov","MOV","Mov","移动值");
			addSameString("体力","hp","HP","Hp","体力值");
			addSameString("魔力","mp","MP","Mp","魔力值","魔法");
			addSameString("幸运","运气","幸运值");
			addSameString("计算机使用","计算机","电脑");
			addSameString("信用评级","信用","信誉");
			addSameString("克苏鲁神话","克苏鲁","cm","CM","Cm");
			addSameString("汽车驾驶","汽车","驾驶");
			addSameString("步霰","步枪/霰弹枪","步枪","霰弹枪");
			addSameString("图书馆使用","图书馆");
			addSameString("锁匠","开锁");
			addSameString("博物学","博物","自然学","自然");
			addSameString("领航","导航");
			addSameString("操作重型机械","重型机械","重型操作","重型","重型机械操作");
			loadSameString();
		}
	}

	@RegistCommand(CommandString="r",Help="随机骰数字")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]r",format=".r")
	@Action(".r")
	public String r(Friend qq,Member sender)
	{
		return r("d",qq,sender);
	}

	@RegistCommand(CommandString="r",Help="随机骰数字")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]r\\s*(.)",format=".r %s")
	@Action(".r {time}")
	public String r(String time,Friend qq,Member sender)
	{
		long result;
		try
		{
			result=transRandomString(time);
		}catch(Exception e)
		{
			return "表达式格式不正确";
		}
		return GetBestName.getName(qq,sender)+"掷出了"+time+"="+result;
	}

	@RegistCommand(CommandString="rh",Help="进行暗骰，结果会私聊发送")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rh",format=".rh")
	@Action(".rh")
	public String rh(Friend qq,Member sender)
	{
		return rh("d",qq,sender);
	}

	@RegistCommand(CommandString="rh",Help="进行暗骰，结果会私聊发送")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rh\\s*(.)",format=".rh %s")
	@Action(".rh {time}")
	public String rh(String time,Friend qq,Member sender)
	{
		if(qq!=null)
			return "私聊没必要暗骰";
		long result;
		try
		{
			result=transRandomString(time);
		}catch(Exception e)
		{
			return "表达式格式不正确";
		}
		if(sender!=null)
		{
			sender.sendMessage(new Message().plus("你掷出了"+time+"="+result));
			return GetBestName.getName(qq,sender)+"进行了一次暗骰";
		}
		return null;
	}

	@RegistCommand(CommandString="rb",Help="投掷带奖励骰的百分骰")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rb",format=".rb")
	@Action(".rb")
	public String rb(Friend qq,Member sender)
	{
		return rb(1,qq,sender);
	}

	@RegistCommand(CommandString="rb",Help="投掷带奖励骰的百分骰")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rb\\s*(\\d+)",format=".rb %s")
	@Action(".rb {time}")
	public String rb(int time,Friend qq,Member sender)
	{
		if(time>10||time<1)
			return "奖励骰数量错误";
		int[] ex=new int[time];
		for(int i=1;i<=time;i++)
			ex[i-1]=getRandomNum(0,10,1);
		int r=getRandomNum(1,101,1);
		int p=replaceByReward(r,ex);
		StringBuilder s=new StringBuilder();
		for(int i:ex)
			s.append(i+",");
		s.deleteCharAt(s.length()-1);
		return GetBestName.getName(qq,sender)+"掷出了1d100="+r+"，奖励骰：{"+s.toString()+"}，得到1d100b"+time+"="+p;
	}

	@RegistCommand(CommandString="rp",Help="投掷带惩罚骰的百分骰")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rp",format=".rp")
	@Action("{pt:[\\.。]}rp")
	public String rp(Friend qq,Member sender)
	{
		return rp(1,qq,sender);
	}

	@RegistCommand(CommandString="rp",Help="投掷带惩罚骰的百分骰")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rp\\s*(\\d+)",format=".rp %s")
	@Action(".rp {time}")
	public String rp(int time,Friend qq,Member sender)
	{
		if(time>10||time<1)
			return "奖励骰数量错误";
		int[] ex=new int[time];
		for(int i=1;i<=time;i++)
			ex[i-1]=getRandomNum(0,10,1);
		int r=getRandomNum(1,101,1);
		int p=replaceByPunish(r,ex);
		StringBuilder s=new StringBuilder();
		for(int i:ex)
			s.append(i+",");
		s.deleteCharAt(s.length()-1);
		return GetBestName.getName(qq,sender)+"掷出了1d100="+r+"，惩罚骰：{"+s.toString()+"}，得到1d100b"+time+"="+p;
	}

	@RegistCommand(CommandString="st",Help="设置技能数值")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]st\\s*(.)",format=".st %s")
	@Action(".st {string}")
	public String st(String string,Friend qq,Member sender)
	{
		string=string.trim();

		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0L;
		}

		Pattern normalSet=Pattern.compile("([\u4e00-\u9fa5a-zA-z]+\\s*[0-9]+)+");
		Pattern taSet=Pattern.compile("([\u4e00-\u9fa5a-zA-z]+)\b*-\\s*([\u4e00-\u9fa5a-zA-z]+\b*[0-9]+)+");
		Pattern huiSet=Pattern.compile("([\u4e00-\u9fa5a-zA-z]+)\b*[?？]\\s*([\u4e00-\u9fa5a-zA-z]+\b*[0-9]+)+");

		Matcher matcher;
		String name=null;
		HashMap<String,Integer> map=new HashMap<>();

		if((matcher=normalSet.matcher(string)).matches())
		{
			Pattern pattern=Pattern.compile("(\\D+)(\\d+)");
			Matcher matcher2=pattern.matcher(string);
			while(matcher2.find())
			{
				try
				{
					map.put(matcher2.group(1),Integer.parseInt(matcher2.group(2)));
				}catch(NumberFormatException e)
				{
					continue;
				}
			}
		}else if((matcher=taSet.matcher(string)).matches()||(matcher=huiSet.matcher(string)).matches())
		{
			name=matcher.group(1);
			String resource=matcher.group(2);
			Pattern pattern=Pattern.compile("(\\D+)(\\d+)");
			Matcher matcher2=pattern.matcher(resource);
			while(matcher2.find())
			{
				try
				{
					map.put(matcher2.group(1),Integer.parseInt(matcher2.group(2)));
				}catch(NumberFormatException e)
				{
					continue;
				}
			}
		}else
		{
			StringBuilder builder=new StringBuilder(string.toLowerCase());
			Pattern num=Pattern.compile("[-+*/0-9d]");
			String mainSkill=null;
			for(int i=0;i<builder.length();i++)
			{
				if(!num.matcher(String.valueOf(builder.charAt(i))).matches())
				{
					int x=i+1;
					for(;x<builder.length();x++)
					{
						if(num.matcher(String.valueOf(builder.charAt(x))).matches())
							break;
					}
					String s=builder.substring(i,x);
					if(mainSkill!=null)
						if(!transToMain(mainSkill).equals(transToMain(s)))
							return "你设置的表达式有误";
					mainSkill=s;
					int kn=getSkill(qqNum,groupNum,mainSkill);
					if(kn==-1000)
						return "不存在该技能";
					builder.replace(i,x,Integer.toString(kn));
					i=x;
				}
			}
			if(mainSkill==null)
				return "你设置的表达式有误";
			long l;
			try
			{
				l=transRandomString(builder.toString());
			}catch(Exception e)
			{
				return "你设置的表达式有误";
			}

			return setSkill(qqNum,groupNum,mainSkill,(int) l)? "把"+mainSkill+"设置成了"+l:"意料之外的错误";
		}
		if(name==null)
			name=String.format("自定义%d",groupNum);
		return setSkill(qqNum,groupNum,map)&&setCardName(qqNum,groupNum,name)? "成功设置了技能值":"意料之外的错误";
	}

	@RegistCommand(CommandString="ra",Help="对技能进行检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]ra\b*(.+?)",format=".ra %s")
	@Action(".ra {name}")
	public String ra(String name,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0L;
		}

		int skillnum=getSkill(qqNum,groupNum,name);
		if(skillnum==-1000)
			return "你还没有设置技能数值";
		CheckStatus status=numCheck(skillnum,getRandomNum(1,101,1));
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+name+"进行检定，掷出1d100="+status.randomNum+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		return builder.toString();
	}

	@RegistCommand(CommandString="ra",Help="对技能进行检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]ra\\s*(.+?)\\s*(\\d+)",format=".ra %s %s")
	@Action(".ra {name} {num}")
	public String ra(String name,int num,Friend qq,Member sender)
	{
		int skillnum=num;
		CheckStatus status=numCheck(skillnum,getRandomNum(1,101,1));
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+name+"进行检定，掷出1d100="+status.randomNum+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		return builder.toString();
	}

	@RegistCommand(CommandString="rab",Help="对技能进行含奖励骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rab\\s*(.+?)",format=".rab %s")
	@Action(".rab {name}")
	public String rab(String name,Friend qq,Member sender)
	{
		return rab(1,name,qq,sender);
	}

	@RegistCommand(CommandString="rab",Help="对技能进行含奖励骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rab\\s*(.+?)\\s*(\\d+)",format=".rab %s %s")
	@Action(".rab {name} {num}")
	public String rab(String name,int num,Friend qq,Member sender)
	{
		return rab(1,name,num,qq,sender);
	}

	@RegistCommand(CommandString="rab",Help="对技能进行含奖励骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rab\\s*(\\d+)\\s*(.+?)",format=".rab %s %s")
	@Action(".rab {time} {name}")
	public String rab(int time,String name,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0L;
		}

		if(time>5||time<1)
			return "奖励骰的数量异常";
		int[] bs=getRandomNumArr(0,10,time);
		int skillnum=getSkill(qqNum,groupNum,name);
		if(skillnum==-1000)
			return "你还没有设置技能数值";
		int rnum=getRandomNum(1,101,1);
		int num=replaceByReward(rnum,bs);
		CheckStatus status=numCheck(skillnum,num);
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+name+"进行检定，掷出1d100="+rnum+"奖励骰："+transArrToString(bs)+"="
				+num+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		return builder.toString();
	}

	@RegistCommand(CommandString="rab",Help="对技能进行含奖励骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rab\\s*(\\d+)\\s*(.+?)\\s*(\\d+)",format=".rab %s %s %s")
	@Action(".rab {time} {name} {num}")
	public String rab(Integer time,String name,Integer num,Friend qq,Member sender)
	{
		if(time>5||time<0)
			return "奖励骰的数量异常";
		int[] bs=getRandomNumArr(0,10,time);
		int skillnum=num;
		int rnum=getRandomNum(1,101,1);
		int $num=replaceByReward(rnum,bs);
		CheckStatus status=numCheck(skillnum,$num);
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+name+"进行检定，掷出1d100="+rnum+"奖励骰："+transArrToString(bs)+"="
				+$num+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		return builder.toString();
	}

	@RegistCommand(CommandString="rap",Help="对技能进行含惩罚骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rap\\s*(.+?)",format=".rap %s")
	@Action(".rap {name}")
	public String rap(String name,Friend qq,Member sender)
	{
		return rap(1,name,qq,sender);
	}

	@RegistCommand(CommandString="rap",Help="对技能进行含惩罚骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rap\\s*(.+?)\\s*(\\d+)",format=".rap %s %s")
	@Action(".rap {name} {num}")
	public String rap(String name,Integer num,Friend qq,Member sender)
	{
		return rap(1,name,num,qq,sender);
	}

	@RegistCommand(CommandString="rap",Help="对技能进行含惩罚骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rap\\s*(\\d+)\\s*(.+?)",format=".rap %s %s")
	@Action(".rap {time} {name}")
	public String rap(Integer time,String name,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0L;
		}

		if(time>5||time<1)
			return "惩罚骰的数量异常";
		int[] bs=getRandomNumArr(0,10,time);
		int skillnum=getSkill(qqNum,groupNum,name);
		if(skillnum==-1000)
			return "你还没有设置技能数值";
		int rnum=getRandomNum(1,101,1);
		int num=replaceByPunish(rnum,bs);
		CheckStatus status=numCheck(skillnum,num);
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+name+"进行检定，掷出1d100="+rnum+"惩罚骰："+transArrToString(bs)+"="
				+num+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		return builder.toString();
	}

	@RegistCommand(CommandString="rap",Help="对技能进行含惩罚骰的检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]rap\\s*(\\d+)\\s*(.+?)\\s*(\\d+)",format=".rap %s %s %s")
	@Action(".rap {time} {name} {num}")
	public String rap(Integer time,String name,Integer num,Friend qq,Member sender)
	{
		if(time>5||time<0)
			return "惩罚骰的数量异常";
		int[] bs=getRandomNumArr(0,10,time);
		int skillnum=num;
		int rnum=getRandomNum(1,101,1);
		int $num=replaceByPunish(rnum,bs);
		CheckStatus status=numCheck(skillnum,$num);
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+name+"进行检定，掷出1d100="+rnum+"惩罚骰："+transArrToString(bs)+"="
				+$num+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		return builder.toString();
	}

	@RegistCommand(CommandString="sc",Help="理智检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]sc\\s*(.*)",format=".sc %s")
	@Action(".sc {string}")
	public String sc(String string,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0L;
		}

		String a,b;
		{
			String[] k=string.split("/");
			if(k.length<2)
				return "表达式有误";
			a=k[0];
			b=k[1];
		}
		long q,p;
		try
		{
			q=transRandomString(a);
			p=transRandomString(b);
		}catch(Exception e)
		{
			return "表达式有误";
		}
		int skillnum=getSkill(qqNum,groupNum,"理智");
		if(skillnum==-1000)
			return "你还没有设置理智数值";
		int num=getRandomNum(0,101,1);
		CheckStatus status=numCheck(skillnum,num);
		if(status.specialStatus==SpecialStatus.BIGFAILED)
			try
			{
				p=transRandomStringToMax(b);
			}catch(Exception e)
			{
//				其实这里是不可能发生的
				return "表达式有误";
			}
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"进行理智检定，掷出1d100="+num+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		builder.append("\n理智减少了"+(status.levelStatus==LevelStatus.FAILED? p:q));
		skillnum-=(status.levelStatus==LevelStatus.FAILED? p:q);
		int con=getSkill(qqNum,groupNum,"意志");
		int kk=getSkill(qqNum,groupNum,"克苏鲁知识");
		if(con<0)
			return "你没有意志，何来的理智？";
		if(kk<0)
			kk=0;
		if(skillnum<=0)
		{
			skillnum=0;
			builder.append("，理智归零\n你陷入了永久疯狂");
		}else if(skillnum>con-kk)
		{
			skillnum=con-kk;
			builder.append("，还剩"+skillnum);
		}else if((status.levelStatus==LevelStatus.FAILED? p:q)>5)
		{
			builder.append("，还剩"+skillnum);
			builder.append("\n你临时的疯狂了");
		}else
			builder.append("，还剩"+skillnum);
		setSkill(qqNum,groupNum,"理智",skillnum);
		return builder.toString();
	}

	@RegistCommand(CommandString="sc",Help="理智检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]sc\\s*(.*?)\\s*(\\d+)",format=".sc %s %s")
	@Action(".sc {string} {skill}")
	public String sc(String string,Integer skill,Friend qq,Member sender)
	{
		String a,b;
		{
			String[] k=string.split("/");
			if(k.length<2)
				return "表达式有误";
			a=k[0];
			b=k[1];
		}
		long q,p;
		try
		{
			q=transRandomString(a);
			p=transRandomString(b);
		}catch(Exception e)
		{
			return "表达式有误";
		}
		int skillnum=skill;
		int num=getRandomNum(0,101,1);
		CheckStatus status=numCheck(skillnum,num);
		if(status.specialStatus==SpecialStatus.BIGFAILED)
			try
			{
				p=transRandomStringToMax(b);
			}catch(Exception e)
			{
//				其实这里是不可能发生的
				return "表达式有误";
			}
		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"进行理智检定，掷出1d100="+num+"/"+skillnum+"\n");
		builder.append("检定"+status.levelStatus.getString());
		if(status.specialStatus!=null)
			builder.append(","+status.specialStatus.getString());
		builder.append("\n理智减少了"+(status.levelStatus==LevelStatus.FAILED? p:q));
		skillnum-=status.levelStatus==LevelStatus.FAILED? p:q;
		int con=100;
		int kk=0;
		if(skillnum<=0)
		{
			skillnum=0;
			builder.append("，理智归零\n你陷入了永久疯狂");
		}else if(skillnum>con-kk)
		{
			skillnum=con-kk;
			builder.append("，还剩"+skillnum);
		}else if((status.levelStatus==LevelStatus.FAILED? p:q)>5)
		{
			builder.append("，还剩"+skillnum);
			builder.append("\n你临时的疯狂了");
		}else
			builder.append("，还剩"+skillnum);
		return builder.toString();
	}

	@RegistCommand(CommandString="en",Help="技能增长检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]en\\s*(.*)",format=".en %s")
	@Action(".en {skill}")
	public String en(String skill,Friend qq,Member sender)
	{
		long qqNum,groupNum;
		if(qq==null)
		{
			qqNum=sender.getId();
			groupNum=sender.getGroup().getId();
		}else
		{
			qqNum=qq.getId();
			groupNum=0L;
		}

		int rnum=getSkill(qqNum,groupNum,skill);
		int num;
		if(rnum<0)
			return "你尚未设置技能值";
		int d=getRandomNum(1,101,1);
		int grow;
		if(d>rnum||(d<=100&&d>95))
			grow=getRandomNum(1,11,1);
		else
			grow=0;
		num=rnum+grow;

		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+skill+"技能进行增长检定，掷出1d100="+d+"/"+rnum+"\n");
		builder.append("检定"+(grow==0? "失败":"成功"));
		if(grow!=0)
		{
			builder.append(skill+"提升了"+grow+"，变成了"+num);
			if(rnum<90&&num>90&&!transToMain(skill).equals(transToMain("信用评级"))
					&&!transToMain(skill).equals(transToMain("克苏鲁知识"))&&!transToMain(skill).equals(transToMain("san"))
					&&!transToMain(skill).equals(transToMain("力量"))&&!transToMain(skill).equals(transToMain("体质"))
					&&!transToMain(skill).equals(transToMain("体型"))&&!transToMain(skill).equals(transToMain("敏捷"))
					&&!transToMain(skill).equals(transToMain("智力"))&&!transToMain(skill).equals(transToMain("意志"))
					&&!transToMain(skill).equals(transToMain("教育"))&&!transToMain(skill).equals(transToMain("幸运"))
					&&!transToMain(skill).equals(transToMain("外貌")))
				builder.append("\n"+GetBestName.getName(qq,sender)+"的"+skill+"已达精通！（可选获得2D6的理智恢复）");
		}
		setSkill(qqNum,groupNum,skill,num);
		return builder.toString();
	}

	@RegistCommand(CommandString="en",Help="技能增长检定")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]en\\s*(.*?)\\s*(\\d+)",format=".en %s %s")
	@Action(".en {skill} {i}")
	public String en(String skill,Integer i,Friend qq,Member sender)
	{
		int rnum=i;
		int num;
		int d=getRandomNum(1,101,1);
		int grow;
		if(d>=rnum||(d<=100&&d>95))
			grow=getRandomNum(1,11,1);
		else
			grow=0;
		num=rnum+grow;

		StringBuilder builder=new StringBuilder();
		builder.append("对"+GetBestName.getName(qq,sender)+"的"+skill+"技能进行增长检定，掷出1d100="+d+"/"+rnum+"\n");
		builder.append("检定"+(grow==0? "失败":"成功"));
		if(grow!=0)
		{
			builder.append(skill+"提升了"+grow+"，变成了"+num);
			if(rnum<90&&num>90&&!transToMain(skill).equals(transToMain("信用评级"))
					&&!transToMain(skill).equals(transToMain("克苏鲁知识"))&&!transToMain(skill).equals(transToMain("san"))
					&&!transToMain(skill).equals(transToMain("力量"))&&!transToMain(skill).equals(transToMain("体质"))
					&&!transToMain(skill).equals(transToMain("体型"))&&!transToMain(skill).equals(transToMain("敏捷"))
					&&!transToMain(skill).equals(transToMain("智力"))&&!transToMain(skill).equals(transToMain("意志"))
					&&!transToMain(skill).equals(transToMain("教育"))&&!transToMain(skill).equals(transToMain("幸运"))
					&&!transToMain(skill).equals(transToMain("外貌")))
				builder.append("\n"+GetBestName.getName(qq,sender)+"的"+skill+"已达精通！（可选获得2D6的理智恢复）");
		}
		return builder.toString();
	}

	@RegistCommand(CommandString="ti",Help="短期疯狂")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]ti",format=".ti")
	@Action(".ti")
	public String ti(Friend qq,Member sender)
	{
		StringBuilder stringBuilder=new StringBuilder();
		int type=getRandomNum(1,10,1);
		stringBuilder.append(GetBestName.getName(qq,sender)+"获得了疯狂症状1d10="+type+"：\n");
		stringBuilder.append(ShortCrazy[type-1]+"\n");
		stringBuilder.append("持续1d10="+getRandomNum(1,10,1)+"轮");
		return stringBuilder.toString();
	}

	@RegistCommand(CommandString="li",Help="长期疯狂")
	@com.cyaegha.sysController.messageHelper.Pattern(pattern="[\\.。]li",format=".li")
	@Action(".li")
	public String li(Friend qq,Member sender)
	{
		StringBuilder stringBuilder=new StringBuilder();
		int type=getRandomNum(1,10,1);
		stringBuilder.append(GetBestName.getName(qq,sender)+"获得了疯狂症状1d10="+type+"：\n");
		stringBuilder.append(LongCrazy[type-1]+"\n");
		stringBuilder.append("时间1d10="+getRandomNum(1,10,1)+"小时");
		return stringBuilder.toString();
	}

//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------
//													内部方法
//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------

	/**
	 * 从a（包含）~b（不包含）中抽取一个数，返回和
	 * 
	 * @param a
	 * @param b
	 * @param time
	 * @return
	 */
	private int getRandomNum(int a,int b,int time)
	{
		if(a>b)
		{
			int x=a;
			a=b;
			b=x;
		}else if(a==b)
			return a;
		if(a==b-1)
			return a;
		Random random=new Random();
		int num=0;
		for(int i=1;i<=time;i++)
			num+=random.nextInt(b-a)+a;
		return num;
	}

	/**
	 * 从a（包含）~b（不包含）中抽取一个数，返回数组
	 * 
	 * @param a
	 * @param b
	 * @param time
	 * @return
	 */
	private int[] getRandomNumArr(int a,int b,int time)
	{
		if(a>b)
		{
			int x=a;
			a=b;
			b=x;
		}
		Random random=new Random();
		int[] x=new int[time];
		for(int i=1;i<=time;i++)
			x[i-1]=random.nextInt(b-a)+a;
		return x;
	}

	/**
	 * 用奖励骰来置换数字
	 * 
	 * @param num
	 * @param rewards
	 * @return
	 */
	private int replaceByReward(int num,int...rewards)
	{
		int a=num/10;
		int b=num%10;
		for(int i:rewards)
			if(i<a)
				a=i;
		return a*10+b;
	}

	/**
	 * 用惩罚骰来置换数字
	 * 
	 * @param num
	 * @param rewards
	 * @return
	 */
	private int replaceByPunish(int num,int...punishs)
	{
		int a=num/10;
		int b=num%10;
		for(int i:punishs)
			if(i>a)
				a=i;
		return a*10+b;
	}

	/**
	 * 检定数值，返回{@link CheckStatus}
	 * 
	 * @param point     待检定的技能点数
	 * @param randerNum 检定数
	 * @return
	 */
	private CheckStatus numCheck(int point,int randerNum)
	{
		int ex_success=point/5;
		int s_success=point/2;
		int success=point;
		LevelStatus level;
		SpecialStatus special=null;
		boolean ifSuccess;
		if(randerNum<=success)
		{
			level=LevelStatus.SUCCESS;
			if(randerNum<=s_success)
			{
				level=LevelStatus.S_SUCCESS;
				if(randerNum<=ex_success)
					level=LevelStatus.EX_SUCCESS;
			}
			ifSuccess=true;
		}else
		{
			level=LevelStatus.FAILED;
			ifSuccess=false;
		}
		if(ifSuccess&&randerNum==1)
		{
			special=SpecialStatus.BIGSUCCESS;
			return new CheckStatus(level,special,randerNum);
		}
		if(!ifSuccess)
		{
			if(point<=50&&(randerNum>=96&&randerNum<=100))
			{
				special=SpecialStatus.BIGFAILED;
				return new CheckStatus(level,special,randerNum);
			}
			if(point>50&&(randerNum==100))
			{
				special=SpecialStatus.BIGFAILED;
				return new CheckStatus(level,special,randerNum);
			}
		}
		return new CheckStatus(level,special,randerNum);
	}

	/**
	 * 将包含有随机数的表达式计算出来
	 * 
	 * @param string 字符串
	 * @return 按要求的随机数
	 * @throws Exception
	 */
	private long transRandomString(String string) throws Exception
	{
		{
			Pattern pattern=Pattern.compile("[-0-9+*/d]+");
			Matcher matcher=pattern.matcher(string);
			if(!matcher.matches())
				throw new Exception();
		}

		Pattern pattern=Pattern.compile("([0-9]*)?d([0-9]*)?");
		Matcher matcher=pattern.matcher(string.toLowerCase());
		String resultString=new String(string);
		while(matcher.find())
		{
			int start=matcher.start(0);
			int end=matcher.end(0);
			String $a=matcher.group(1);
			String $b=matcher.group(2);
			int a,b;
			if($a.isEmpty())
				a=1;
			else
				a=Integer.parseInt($a);
			if($b.isEmpty())
				b=100;
			else
				b=Integer.parseInt($b);
			if(a>1000||a<1)
				a=1;
			if(b>10000||b<1)
				b=100;
			int result=getRandomNum(1,b,a);
			resultString=string.replaceFirst(string.substring(start,end),Integer.toString(result));
		}
		Evaluator evaluator=new Evaluator();
		return (long) evaluator.getNumberResult(resultString);
	}

	/**
	 * 将包含有随机数的表达式计算出其最大值
	 * 
	 * @param string 字符串
	 * @return 按要求的随机数
	 * @throws Exception
	 */
	private long transRandomStringToMax(String string) throws Exception
	{
		{
			Pattern pattern=Pattern.compile("[-0-9+*/d]+");
			Matcher matcher=pattern.matcher(string);
			if(!matcher.matches())
				throw new Exception();
		}

		Pattern pattern=Pattern.compile("([0-9]*)?d([0-9]*)?");
		Matcher matcher=pattern.matcher(string.toLowerCase());
		String resultString=new String(string);
		while(matcher.find())
		{
			int start=matcher.start(0);
			int end=matcher.end(0);
			String $a=matcher.group(1);
			String $b=matcher.group(2);
			int a,b;
			if($a.isEmpty())
				a=1;
			else
				a=Integer.parseInt($a);
			if($b.isEmpty())
				b=100;
			else
				b=Integer.parseInt($b);
			if(a>1000||a<1)
				a=1;
			if(b>10000||b<1)
				b=100;
			int result=a*b;
			string.replaceFirst(string.substring(start,end),Integer.toString(result));
		}
		Evaluator evaluator=new Evaluator();
		return (long) evaluator.getNumberResult(resultString);
	}

	/**
	 * 将数组变成“[,,,,]”的形式
	 * 
	 * @param a
	 * @return
	 */
	private String transArrToString(int[] a)
	{
		StringBuilder builder=new StringBuilder("[");
		for(int i:a)
		{
			builder.append(i);
			builder.append(",");
		}
		builder.deleteCharAt(builder.length()-1);
		builder.append("]");
		return builder.toString();
	}

//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------
//													工具方法
//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------
//	------------------------------------------------------------------------------------------------------

	/**
	 * 加载或更新相同意义字符串文件<br>
	 * {@link SameStringList}会被更新
	 */
	private void loadSameString()
	{
		Map<String,String> map=exchanger.getMap(SAME_STRING);
		if(SameStringList==null)
			SameStringList=new HashMap<>();
		if(map==null||map.size()==0)
			return;
		for(Entry<String,String> strings:map.entrySet())
			SameStringList.put(strings.getKey(),strings.getValue().split(","));
	}

	/**
	 * 保存相同意义字符串文件
	 */
	private void savaSameString()
	{
		exchanger.deleteList(SAME_STRING);
		for(Entry<String,String[]> entry:SameStringList.entrySet())
		{
			StringBuilder stringBuilder=new StringBuilder();
			for(String string:entry.getValue())
				stringBuilder.append(string+",");
			exchanger.setMapData(SAME_STRING,entry.getKey(),stringBuilder.toString());
		}
	}

	/**
	 * 添加相同意义字符串<br>
	 * 这一系列的字符串中没有一个已经存在则会新添加一个类别<br>
	 * 新添加的类别的主要名字是{@code main}<br>
	 * 如果发现一系列的名字中有两个及以上的同类词，这会返回false<br>
	 * 如果已经存在一个系列包含该词语，则不会将设定的主要词语更换<br>
	 * 
	 * @param main  主要词语
	 * @param other 次要的词语
	 */
	private boolean addSameString(String main,String...other)
	{
		String key=null;

		ArrayList<String> testList=new ArrayList<>();
		testList.add(main);
		Collections.addAll(testList,other);

		for(Entry<String,String[]> entry:SameStringList.entrySet())
		{
			ArrayList<String> sourceList=new ArrayList<>();
			sourceList.add(entry.getKey());
			Collections.addAll(sourceList,entry.getValue());

			for(String string:testList)
				for(String text:sourceList)
					if(string==text)
						if(key!=null)
						{
							if(key!=entry.getKey())
								return false;
						}else
							key=entry.getKey();
		}
		if(key==null)
		{
			SameStringList.put(main,other);
			StringBuilder builder=new StringBuilder();
			for(String string:other)
				builder.append(string+",");
			exchanger.setMapData(SAME_STRING,main,builder.toString());
		}else
		{
			String[] strings=SameStringList.get(key);
			ArrayList<String> arrayList=new ArrayList<>();
			Collections.addAll(arrayList,strings);
			for(String string:testList)
			{
				if(arrayList.contains(string)||string.equals(key))
					continue;
				else
					arrayList.add(string);
			}
			StringBuilder builder=new StringBuilder();
			for(String string:arrayList)
				builder.append(string+",");
			SameStringList.put(key,arrayList.toArray(new String[arrayList.size()]));
			exchanger.setMapData(SAME_STRING,key,builder.toString());
		}
		return true;
	}

	/**
	 * 将字符串替换为主相同字符串中的主要字符串
	 * 
	 * @param string
	 * @return 相同字符串中的主字符串 不存在的话则返回原字符串
	 */
	private String transToMain(String string)
	{
		for(Entry<String,String[]> entry:SameStringList.entrySet())
			for(String s:entry.getValue())
				if(s.equals(string))
					return entry.getKey();
		return string;
	}

	/**
	 * 将文字转换为数字
	 * 
	 * @param num  待转换数字
	 * @param from 要求下限
	 * @param to   要求上限
	 * @return 正常数字
	 * @throws NumberFormatException 如果不是数字或不在限定中
	 */
	private int formatNum(String num,int from,int to) throws NumberFormatException
	{
		int formatNum;
		try
		{
			formatNum=Integer.parseInt(num);
		}catch(NumberFormatException exception)
		{
			throw exception;
		}
		if(formatNum>to||formatNum<from)
			throw new NumberFormatException("超出限制范围");
		return formatNum;
	}

	/**
	 * 设置技能值<br>
	 * <b> 使用此方法会导致在创建新卡时，不能直接将群人-卡关联</b>
	 * 
	 * @param qq       QQ号
	 * @param cardName 卡的名字
	 * @param map      参数
	 * @return
	 */
	private boolean setSkill(long qq,String cardName,Map<String,Integer> map)
	{
		File file=new File(dataFloder.getPath()+qq+".xml");
		Document document;
		try
		{
			document=XMLUtils.getDocument(file,true);
		}catch(JDOMException|IOException e)
		{
			Log.e("写入技能时，读取文件出现错误");
			return false;
		}
		Element root=document.getRootElement();
		List<Element> cards=root.getChildren(XMLLABLE_CARD);
		for(Element element:cards)
			if(element.getChild(XMLLABLE_NAME).getText().equals(cardName))
				for(Entry<String,Integer> entry:map.entrySet())
				{
					String skillName=transToMain(entry.getKey());
					int point=entry.getValue();
					Element skill=element.getChild(skillName);
					if(skill==null)
					{
						skill=new Element(skillName);
						element.addContent(skill);
					}
					skill.setText(String.valueOf(point));
					return true;
				}
		Element newCard=new Element(XMLLABLE_CARD);
		newCard.addContent(new Element(XMLLABLE_NAME).setText(cardName));
		newCard.addContent(new Element(XMLLABLE_POINT));
		for(Entry<String,Integer> entry:map.entrySet())
			newCard.addContent(new Element(transToMain(entry.getKey())).setText(String.valueOf(entry.getValue())));
		XMLOutputter outputter=new XMLOutputter(Format.getCompactFormat().setEncoding("UTF-8").setIndent("\t"));
		try
		{
			FileWriter fileWriter=new FileWriter(file);
			outputter.output(document,fileWriter);
			fileWriter.close();
		}catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("数据XML写出失败");
			return false;
		}
		return true;
	}

	/**
	 * 设置技能值<br>
	 * <b> 使用此方法会导致在创建新卡时，不能直接将群人-卡关联</b>
	 * 
	 * @param qq        QQ号
	 * @param cardName  卡的名字
	 * @param skillName 技能名字
	 * @param point     技能值
	 * @return
	 */
	private boolean setSkill(long qq,String cardName,String skillName,int point)
	{
		HashMap<String,Integer> map=new HashMap<String,Integer>();
		map.put(skillName,point);
		return setSkill(qq,cardName,map);
	}

	/**
	 * 获取技能值
	 * 
	 * @param qq        QQ号
	 * @param cardName  该号中的卡
	 * @param skillName 技能名
	 * @return 技能数值，不存在则会返回-1000
	 */
	private int getSkill(long qq,String cardName,String skillName)
	{
		skillName=transToMain(skillName);
		File file=new File(dataFloder.getPath()+qq+".xml");
		Document document;
		try
		{
			document=XMLUtils.getDocument(file,true);
		}catch(JDOMException|IOException e)
		{
			return -1000;
		}
		Element root=document.getRootElement();
		List<Element> cards=root.getChildren(XMLLABLE_CARD);
		for(Element element:cards)
			if(element.getChild(XMLLABLE_NAME).getText().equals(cardName))
			{
				Element skill=element.getChild(skillName);
				if(skill==null)
					return -1000;
				return Integer.parseInt(skill.getText());
			}
		return -1000;
	}

	/**
	 * 获得该群中该QQ号使用的卡的名称
	 * 
	 * @param qq       QQ号
	 * @param groupNum 群号
	 * @return 卡的名称，如果不存在则会返回null
	 */
	private String getCardName(long qq,long groupNum)
	{
		File file=new File(dataFloder.getPath()+qq+".xml");
		Document document;
		try
		{
			document=XMLUtils.getDocument(file,false);
		}catch(JDOMException|IOException e)
		{
			return null;
		}
		Element root=document.getRootElement();
		Element point=root.getChild(XMLLABLE_POINT);
		Element cardName=point.getChild("G"+groupNum);
		if(cardName==null)
			return null;
		else
			return cardName.getText();
	}

	private ArrayList<String> getAllCardName(long qq)
	{
		File file=new File(dataFloder.getPath()+qq+".xml");
		Document document;
		ArrayList<String> list=new ArrayList<>();
		try
		{
			document=XMLUtils.getDocument(file,false);
		}catch(JDOMException|IOException e)
		{
			return list;
		}
		Element root=document.getRootElement();
		List<Element> point=root.getChildren(XMLLABLE_CARD);
		for(Element element:point)
			list.add(element.getChildText(XMLLABLE_NAME));
		return list;
	}

	/**
	 * 设置该群使用的卡
	 * 
	 * @param qq       QQ号
	 * @param groupNum 群号
	 * @param cardName 卡名称
	 * @return 是否成功
	 */
	private boolean setCardName(long qq,long groupNum,String cardName)
	{
		ArrayList<String> list=getAllCardName(qq);
		if(!list.contains(cardName))
			return false;
		File file=new File(dataFloder.getPath()+qq+".xml");
		Document document;
		try
		{
			document=XMLUtils.getDocument(file,false);
		}catch(JDOMException|IOException e)
		{
			return false;
		}
		Element root=document.getRootElement();
		Element point=root.getChild(XMLLABLE_POINT);
		Element $cardName=point.getChild("G"+groupNum);
		if($cardName==null)
			$cardName=new Element("G"+groupNum).setText(cardName);
		else
			$cardName.setText(cardName);
		return true;
	}

	/**
	 * 获得该群中该QQ号使用的卡的技能数值
	 * 
	 * @param qq        QQ号
	 * @param groupNum  群号
	 * @param skillName 技能名
	 * @return 技能数值，不存在会返回-1000
	 */
	private int getSkill(long qq,long groupNum,String skillName)
	{
		String point=getCardName(qq,groupNum);
		if(point==null)
			return -1000;
		else
			return getSkill(qq,point,skillName);
	}

	/**
	 * 设置技能值<br>
	 * <b> 使用此方法会导致在创建新卡时，不能直接将群人-卡关联</b>
	 * 
	 * @param qq        QQ号
	 * @param groupNum  群号
	 * @param skillName 技能名字
	 * @param num       技能值
	 * @return
	 */
	private boolean setSkill(long qq,long groupNum,String skillName,int num)
	{
		String cardName=getCardName(qq,groupNum);
		return setSkill(qq,cardName,skillName,num);
	}

	/**
	 * 设置技能值<br>
	 * <b> 使用此方法会导致在创建新卡时，不能直接将群人-卡关联</b>
	 * 
	 * @param qq       QQ号
	 * @param groupNum 群号
	 * @param map      参数
	 * @return
	 */
	private boolean setSkill(long qq,long groupNum,Map<String,Integer> map)
	{
		String cardName=getCardName(qq,groupNum);
		return setSkill(qq,cardName,map);
	}
}
