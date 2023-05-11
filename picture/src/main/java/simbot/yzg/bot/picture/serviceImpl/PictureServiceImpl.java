package simbot.yzg.bot.picture.serviceImpl;

import love.forte.simbot.message.Image;
import love.forte.simbot.message.ResourceImage;
import love.forte.simbot.resources.PathResource;
import love.forte.simbot.resources.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import simbot.yzg.bot.commonapi.entity.PictureResponse;
import simbot.yzg.bot.commonapi.service.PictureService;
import simbot.yzg.bot.picture.dao.PicDao;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PictureServiceImpl implements PictureService {
	@Autowired
	PicDao picDao;

	@Override
	public PictureResponse geiAiPicture() {
		return null;
	}

	@Override
	public boolean refreshDB() {
		return false;
	}

	@Override
	public PictureResponse getLocalPic(String type) {
		return null;
	}

	@Override
	public List<PictureResponse> getLocalPics(String type, int num) {
		return null;
	}

	@Override
	public PictureResponse getNetPic() {

		int id = myProduce.idleId.pollLast();
		String path = myProduce.netPicPath + id + ".jpg";
		Random r = new Random(25);
		int k = r.nextInt(100);

		if (k >= 20) {
			myProduce.down("http://api.iw233.cn/api.php?sort=random", path);
		} else if (k >= 10) {
			myProduce.down("https://api.ghser.com/random/pc.php", path);
		} else {
			myProduce.down("https://api.ixiaowai.cn/api/api.php", path);
		}
		if (new File(path).exists()) {
			//TODO:test new image
			Path image = Paths.get("xxx/image.jpg");
			PathResource resource = Resource.of(image);
			ResourceImage imageResource = Image.of(resource);
			sendMessages = sendMessages.plus(imageResource);
		}

		return null;
	}

	@Override
	public List<PictureResponse> getNetPics(int num) {
		List<PictureResponse> res=new ArrayList<>();
		for (int i = 0; i < num; i++) {
			res.add(getNetPic());
		}
		return res;
	}
}
