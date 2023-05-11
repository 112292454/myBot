package simbot.yzg.bot.commonapi.service;

import org.springframework.stereotype.Service;
import simbot.yzg.bot.commonapi.vo.Result;

import java.util.List;

@Service
public interface AIPaintingService {
//    Result<List<byte[]>> getSameSizePaints(initArgs args);

//	Result<List<byte[]>> getDiffSizePaints(initArgs args);

	Result<List<byte[]>> doPaint(String input);

	Result<List<byte[]>> doPaint(long id);

	Result<List<byte[]>> doPaint();

//	t2iArgs parseArgs(String input);

	byte[] fastGetStoreImage();

	String helpMsg();

//	double getQueueTime();

	double getPredictTime(long id);

	long prepare(String args);
}
