package simbot.yzg.bot.commonapi.service;

import simbot.yzg.bot.commonapi.entity.PictureResponse;

import java.util.List;

public interface PictureService {
	PictureResponse geiAiPicture();

	boolean refreshDB();

	PictureResponse getLocalPic(String type);

	List<PictureResponse> getLocalPics(String type,int num);

	PictureResponse getNetPic();

	List<PictureResponse> getNetPics(int num);


}
