package simbot.yzg.bot.commonapi.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class authority {
    public final HashMap<String,Integer> auth=new HashMap<>();
	final boolean isGroup;
	public  authority(boolean isGroup) throws FileNotFoundException {
		Scanner in;
		this.isGroup=isGroup;
		if(isGroup) {
			in=new Scanner(new File("D:\\botPic\\authority\\groupAuthority.txt"));
		}else {
			in=new Scanner(new File("D:\\botPic\\authority\\userAuthority.txt"));
		}
		while (in.hasNext()){
			String id=in.next().trim(),au=in.nextLine().toLowerCase(Locale.ROOT).trim();
			switch (au) {
				case "admin":
					auth.put(id,4);
					break;
				case "r18":
					auth.put(id,3);
					break;
				case "pic":
					auth.put(id,2);
					break;
				case "basic":
					auth.put(id,1);
					break;
				case "ban":
					auth.put(id,0);
					break;
				default:
					auth.put(id,1);
					break;
			}

		}
	}
	/**
	 * 
	 *
	 * @Author  Guo 
	 * @CreateTime   2022-05-13 21:51
	 * @Return boolean
	 * @Discription 
	 * @param id
 * @param level
	 */
	public boolean haveAuth(String id,String level){
		int l;
		switch (level) {
			case "admin":
				l=4;
				break;
			case "r18":
				l=3;
				break;
			case "pic":
				l=2;
				break;
			case "basic":
				l=1;
				break;
			default:
				l=5;
				break;
		}
		System.out.println("群" + id + "要求" + level + "权限,结果为："+(auth.getOrDefault(id,0)>=l));
		return auth.getOrDefault(id,isGroup?1:2)>=l;
	}
}
